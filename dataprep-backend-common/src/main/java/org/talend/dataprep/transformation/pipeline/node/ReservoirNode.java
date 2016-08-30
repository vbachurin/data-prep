package org.talend.dataprep.transformation.pipeline.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.pipeline.Monitored;
import org.talend.dataprep.transformation.pipeline.Signal;
import org.talend.dataprep.transformation.pipeline.Visitor;
import org.talend.dataprep.util.FilesHelper;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReservoirNode extends AnalysisNode implements Monitored {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservoirNode.class);

    private final JsonGenerator generator;

    private final File reservoir;

    private final Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> delayedAnalyzer;

    private RowMetadata rowMetadata;

    private long totalTime;

    private long count;

    private Analyzer<Analyzers.Result> resultAnalyzer;

    private List<ColumnMetadata> filteredColumns;

    private Set<String> filteredColumnNames;

    public ReservoirNode(Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> analyzer, //
                         Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> delayedAnalyzer,
                         Predicate<ColumnMetadata> filter, //
                         StatisticsAdapter adapter) {
        super(analyzer, filter, adapter);
        this.delayedAnalyzer = delayedAnalyzer;
        try {
            reservoir = File.createTempFile("ReservoirNode", ".zip");
            JsonFactory factory = new JsonFactory();
            generator = factory.createGenerator(new GZIPOutputStream(new FileOutputStream(reservoir), true));
            generator.writeStartObject();
            generator.writeFieldName("records");
            generator.writeStartArray();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public void receive(DataSetRow row, RowMetadata metadata) {
        final long start = System.currentTimeMillis();
        try {
            final boolean needRefresh = rowMetadata == null || !metadata.equals(rowMetadata);
            List<ColumnMetadata> columns = metadata.getColumns();
            if (!columns.isEmpty()) {
                if (filteredColumns == null || needRefresh) {
                    filteredColumns = columns.stream().filter(filter).collect(Collectors.toList());
                    filteredColumnNames = filteredColumns.stream().map(ColumnMetadata::getId).collect(Collectors.toSet());
                }
            } else {
                // No column in row metadata, guess all type, starting from string columns.
                ColumnMetadata.Builder builder = ColumnMetadata.Builder.column().type(Type.STRING);
                final int rowSize = row.toArray(DataSetRow.SKIP_TDP_ID).length;
                columns = new ArrayList<>(rowSize + 1);
                for (int i = 0; i < rowSize; i++) {
                    final ColumnMetadata newColumn = builder.build();
                    metadata.addColumn(newColumn);
                    columns.add(newColumn);
                }
                filteredColumns = columns;
                filteredColumnNames = columns.stream().map(ColumnMetadata::getId).collect(Collectors.toSet());
            }
            rowMetadata = metadata;
            // Analyze non deleted rows
            analyze(row, columns);
            // Store rows for end of stream signal.
            store(row, columns);
        } finally {
            totalTime += System.currentTimeMillis() - start;
            count++;
        }
    }

    // Store row in temporary file
    private void store(DataSetRow row, List<ColumnMetadata> columns) {
        try {
            generator.writeStartObject();
            columns.forEach(column -> {
                try {
                    generator.writeStringField(column.getId(), row.get(column.getId()));
                } catch (IOException e) {
                    throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            });
            if (row.isDeleted()) {
                generator.writeBooleanField("_deleted", true);
            }
            final Optional<Long> tdpId = Optional.ofNullable(row.getTdpId());
            if (tdpId.isPresent()) {
                generator.writeNumberField(DataSetRow.TDP_ID, tdpId.get());
            }
            generator.writeEndObject();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    // Analyze row using lazily configured analyzer
    private void analyze(DataSetRow row, List<ColumnMetadata> columns) {
        if (!row.isDeleted()) {
            // Lazy initialization of the result analyzer
            if (resultAnalyzer == null) {
                resultAnalyzer = analyzer.apply(filteredColumns);
            }
            final String[] values = row.filter(filteredColumns) //
                    .order(columns) //
                    .toArray(DataSetRow.SKIP_TDP_ID.and(e -> filteredColumnNames.contains(e.getKey())));
            try {
                resultAnalyzer.analyze(values);
            } catch (Exception e) {
                LOGGER.debug("Unable to analyze row '{}'.", Arrays.toString(values), e);
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitNode(this);
    }

    @Override
    public void signal(Signal signal) {
        final long start = System.currentTimeMillis();
        try {
            if (signal == Signal.END_OF_STREAM) {
                // End temporary output
                generator.writeEndArray();
                generator.writeEndObject();
                generator.flush();
                // Send stored records to next steps
                final ObjectMapper mapper = new ObjectMapper();
                if (rowMetadata != null && resultAnalyzer != null) {
                    // Adapt row metadata to infer type (adapter takes care of type-forced columns)
                    resultAnalyzer.end();
                    final List<ColumnMetadata> columns = rowMetadata.getColumns();
                    adapter.adapt(columns, resultAnalyzer.getResult(), filter);
                    resultAnalyzer.close();

                    final Analyzer<Analyzers.Result> configuredAnalyzer = delayedAnalyzer.apply(filteredColumns);
                    try (JsonParser parser = mapper.getFactory()
                            .createParser(new GZIPInputStream(new FileInputStream(reservoir)))) {
                        final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
                        dataSet.getRecords().forEach(r -> {
                            if (!r.isDeleted()) {
                                final String[] values = r.filter(filteredColumns) //
                                        .order(columns) //
                                        .toArray(DataSetRow.SKIP_TDP_ID.and(e -> filteredColumnNames.contains(e.getKey())));
                                configuredAnalyzer.analyze(values);
                            }
                        });
                        configuredAnalyzer.end();
                        adapter.adapt(columns, configuredAnalyzer.getResult(), filter);
                    } finally {
                        try {
                            configuredAnalyzer.close();
                        } catch (Exception e) {
                            LOGGER.debug("Unable to close analyzer.", e);
                        }
                    }
                }
                // Continue process
                try (JsonParser parser = mapper.getFactory().createParser(new GZIPInputStream(new FileInputStream(reservoir)))) {
                    final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
                    dataSet.getRecords().forEach(r -> link.exec().emit(r, rowMetadata));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to perform delayed analysis.", e);
        } finally {
            FilesHelper.deleteQuietly(reservoir);
            totalTime += System.currentTimeMillis() - start;
        }
        super.signal(signal);
    }

    @Override
    public long getTotalTime() {
        return totalTime;
    }

    @Override
    public long getCount() {
        return count;
    }
}
