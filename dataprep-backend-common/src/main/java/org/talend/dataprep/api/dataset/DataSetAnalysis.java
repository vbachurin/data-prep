package org.talend.dataprep.api.dataset;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.datascience.statistics.StatisticsClientJson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;

public class DataSetAnalysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetAnalysis.class);

    private DataSetAnalysis() {
    }

    public static void computeStatistics(DataSet dataSet, SparkContext sparkContext, Jackson2ObjectMapperBuilder builder)
            throws IOException {
        final DataSetMetadata metadata = dataSet.getMetadata();
        StatisticsClientJson statisticsClient = new StatisticsClientJson(true, sparkContext);
        statisticsClient.setJsonRecordPath("records"); //$NON-NLS-1$
        final StringWriter content = new StringWriter();
        builder.build().writer().writeValue(content, dataSet);
        // Build schema for the content (JSON format expected by statistics library).
        StringWriter schema = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(schema);
        generator.writeStartObject();
        {
            generator.writeFieldName("column"); //$NON-NLS-1$
            generator.writeStartArray();
            {
                for (ColumnMetadata column : metadata.getRow().getColumns()) {
                    generator.writeStartObject();
                    {
                        generator.writeStringField("name", StringUtils.EMPTY); //$NON-NLS-1$
                        generator.writeStringField("id", StringUtils.EMPTY); //$NON-NLS-1$
                        generator.writeStringField("type", column.getType()); //$NON-NLS-1$
                        generator.writeStringField("suggested type", column.getType()); //$NON-NLS-1$
                        // Types
                        generator.writeArrayFieldStart("types"); //$NON-NLS-1$
                        generator.writeStartObject();
                        {
                            generator.writeStringField("name", column.getType()); //$NON-NLS-1$
                            generator.writeNumberField("occurrences", metadata.getContent().getNbRecords());
                        }
                        generator.writeEndObject();
                        generator.writeEndArray();
                    }
                    generator.writeEndObject();
                }
            }
            generator.writeEndArray();
        }
        generator.writeEndObject();
        generator.flush();
        // Compute statistics
        int topKfreqTable = 15;
        String binsOrBuckets = "8"; //$NON-NLS-1$
        statisticsClient.setSchema(schema.toString());
        String jsonResult = statisticsClient.doStatisticsInMemory(content.toString(), topKfreqTable, binsOrBuckets);
        LOGGER.debug("Quality results: {}", jsonResult);
        // Use result from quality analysis
        final Iterator<JsonNode> columns = builder.build().readTree(jsonResult).get("column").elements(); //$NON-NLS-1$
        final List<ColumnMetadata> schemaColumns = metadata.getRow().getColumns();
        while (columns.hasNext()) {
            final JsonNode column = columns.next();
            final int index = column.get("index").asInt();
            if (index >= schemaColumns.size()) {
                LOGGER.error("No column found at index {}, ignoring result", index);
                continue;
            }
            final ColumnMetadata schemaColumn = schemaColumns.get(index);
            // Get the statistics from the returned JSON
            final JsonNode statistics = column.get("statistics"); //$NON-NLS-1$
            // Keeps the statistics as returned by statistics library.
            schemaColumn.setStatistics(statistics.toString());
        }
    }
}
