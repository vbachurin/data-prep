package org.talend.dataprep.transformation.api.transformer.type;

import static java.util.stream.StreamSupport.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.spark.SparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.*;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;
import org.talend.datascience.common.inference.quality.ValueQuality;
import org.talend.datascience.common.inference.quality.ValueQualityAnalyzer;
import org.talend.datascience.common.inference.type.DataType;

/**
 * Transforms dataset rows.
 */
@Component
public class RecordsTransformerStep implements TransformerStep {

    @Autowired
    SparkContext sparkContext;

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    /**
     * @see TransformerStep#process(TransformerConfiguration)
     */
    @Override
    public void process(final TransformerConfiguration configuration) {
        final TransformerWriter writer = configuration.getOutput();
        final DataSet dataSet = configuration.getInput();

        final List<BiConsumer<DataSetRow, TransformationContext>> actions = configuration.getRecordActions();
        final List<Integer> indexes = configuration.getIndexes();

        final BiConsumer<DataSetRow, TransformationContext> referenceAction = configuration.isPreview() ? actions.get(0) : null;

        final BiConsumer<DataSetRow, TransformationContext> action = configuration.isPreview() ? actions.get(1) : actions.get(0);
        TransformationContext context = configuration.isPreview() ? configuration.getTransformationContext(1) : configuration
                .getTransformationContext(0);

        final boolean isIndexLimited = indexes != null && !indexes.isEmpty();
        final Integer minIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).min().getAsInt() : 0;
        final Integer maxIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).max().getAsInt() : Integer.MAX_VALUE;

        Stream<DataSetRow> records = dataSet.getRecords();
        try {
            writer.fieldName("records");
            writer.startArray();
            AtomicInteger index = new AtomicInteger(0);
            if (!configuration.isPreview()) {
                // No preview (no 'old row' and 'new row' to compare when writing results).
                Stream<Processing> process = records.map(row -> new Processing(row, index.getAndIncrement())) //
                        .map(p -> {
                            action.accept(p.row, context);
                            return p;
                        }) //
                        .skip(minIndex) //
                        .limit(maxIndex);
                // Filter by indexes (if any specified in configuration).
                if (indexes != null) {
                    process = process.filter(p -> indexes.contains(p.index));
                }
                // Configure quality analysis (if column metadata information is present in stream).
                final List<ColumnMetadata> columns = context.getTransformedRowMetadata().getColumns();
                final DataType.Type[] types = TypeUtils.convert(columns);
                ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(types);
                if (types.length > 0) {
                    process = process.map(p -> {
                        if (!p.row.isDeleted()) {
                            final Map<String, Object> rowValues = p.row.values();
                            final List<String> strings = stream(rowValues.values().spliterator(), false) //
                                    .map(String::valueOf) //
                                    .collect(Collectors.<String>toList());
                            qualityAnalyzer.analyze(strings.toArray(new String[strings.size()]));
                        }
                        return p;
                    });
                }
                // Write transformed records to stream
                List<DataSetRow> transformedRows = new ArrayList<>();
                process.forEach(row -> {
                    transformedRows.add(row.row);
                    writeRow(writer, row.row);
                });
                // Column statistics
                if (!context.getTransformedRowMetadata().getColumns().isEmpty()) {
                    // Spark statistics
                    final DataSet statisticsDataSet = new DataSet();
                    final DataSetMetadata transformedMetadata = new DataSetMetadata("", //
                            "", //
                            "", //
                            0, //
                            context.getTransformedRowMetadata());
                    statisticsDataSet.setMetadata(transformedMetadata);
                    statisticsDataSet.setRecords(transformedRows.stream());
                    DataSetAnalysis.computeStatistics(statisticsDataSet, sparkContext, builder);
                    // Set new quality information in transformed column metadata
                    final List<ValueQuality> result = qualityAnalyzer.getResult();
                    for (int i = 0; i < result.size(); i++) {
                        final ValueQuality column = result.get(i);
                        final Quality quality = columns.get(i).getQuality();
                        quality.setEmpty((int) column.getEmptyCount());
                        quality.setInvalid((int) column.getInvalidCount());
                        quality.setValid((int) column.getValidCount());
                    }
                }
            } else {
                if (referenceAction == null) {
                    throw new IllegalStateException("No old action to perform for preview.");
                }
                TransformationContext referenceContext = configuration.getTransformationContext(0);
                final AtomicInteger resultIndexShift = new AtomicInteger();
                // With preview (no 'old row' and 'new row' to compare when writing results).
                Stream<Processing[]> process = records.map(row -> new Processing(row, index.getAndIncrement() - resultIndexShift.get())) //
                        .map(p -> new Processing[]{new Processing(p.row.clone(), p.index), p}) //
                        .map(p -> {
                            referenceAction.accept(p[0].row, referenceContext);
                            if (p[0].row.isDeleted()) {
                                resultIndexShift.incrementAndGet();
                            }
                            action.accept(p[1].row, context);
                            return p;
                        }); //
                if (indexes != null) {
                    process = process.filter(p -> {
                        final boolean inRange = p[1].index >= minIndex && p[1].index <= maxIndex;
                        final boolean include = indexes.contains(p[1].index) || (p[0].row.isDeleted() && !p[1].row.isDeleted());
                        return inRange && include;
                    });
                }
                process.forEach(p -> {
                    p[1].row.diff(p[0].row);
                    writeRow(writer, p[1].row);
                });
            }
                writer.endArray();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
    }

    /**
     * Write the given row using the given writer.
     *
     * @param writer the writer to use.
     * @param row the row to write.
     */
    private void writeRow(TransformerWriter writer, DataSetRow row) {
        try {
            if (row.shouldWrite()) {
                writer.write(row);
            }
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
    }

    public static class Processing {
        DataSetRow row;
        int index;

        public Processing(DataSetRow row, int index) {
            this.row = row;
            this.index = index;
        }
    }

}
