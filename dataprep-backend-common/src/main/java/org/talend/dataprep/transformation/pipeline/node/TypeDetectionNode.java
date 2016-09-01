package org.talend.dataprep.transformation.pipeline.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.api.dataset.row.FlagNames;
import org.talend.dataprep.api.dataset.statistics.StatisticsAdapter;
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

public class TypeDetectionNode extends ColumnFilteredNode implements Monitored {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeDetectionNode.class);

    private final JsonGenerator generator;

    private final File reservoir;

    private final Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> analyzer;

    private final Predicate<ColumnMetadata> filter;

    private final StatisticsAdapter adapter;

    private long totalTime;

    private Analyzer<Analyzers.Result> resultAnalyzer;

    private long count;

    public TypeDetectionNode(Function<List<ColumnMetadata>, Analyzer<Analyzers.Result>> analyzer, //
            Predicate<ColumnMetadata> filter, //
            StatisticsAdapter adapter) {
        super(filter);
        this.analyzer = analyzer;
        this.filter = filter;
        this.adapter = adapter;
        try {
            reservoir = File.createTempFile("TypeDetection", ".zip");
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
        performColumnFilter(row, metadata);
        store(row, metadata.getColumns());
        analyze(row);
        count++;
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
                generator.writeNumberField(FlagNames.TDP_ID, tdpId.get());
            }
            for (Map.Entry<String, String> entry : row.getInternalValues().entrySet()) {
                generator.writeStringField(entry.getKey(), entry.getValue());
            }
            generator.writeEndObject();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    // Analyze row using lazily configured analyzer
    private void analyze(DataSetRow row) {
        if (!row.isDeleted()) {
            // Lazy initialization of the result analyzer
            if (resultAnalyzer == null) {
                resultAnalyzer = analyzer.apply(filteredColumns);
            }
            final String[] values = row.filter(filteredColumns) //
                    .order(filteredColumns) //
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
