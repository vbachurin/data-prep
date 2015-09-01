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

    private DataSetAnalysis() {}

    public static void computeStatistics(final DataSet dataSet, final SparkContext sparkContext, final Jackson2ObjectMapperBuilder builder)
            throws IOException {
        // gather infos for statistics service
        final DataSetMetadata metadata = dataSet.getMetadata();

        final String datasetContent = getContentAsJSONString(dataSet, builder);
        final int topKfreqTable = 15;
        final String binsOrBuckets = "8"; //$NON-NLS-1$

        final StatisticsClientJson statisticsClient = new StatisticsClientJson(true, sparkContext);
        statisticsClient.setJsonRecordPath("records"); //$NON-NLS-1$
        statisticsClient.setSchema(getSchemaAsJSONString(metadata));

        // Compute statistics
        final String jsonResult = statisticsClient.doStatisticsInMemory(datasetContent, topKfreqTable, binsOrBuckets);
        LOGGER.debug("Quality results: {}", jsonResult);

        // Use result from quality analysis
        final Iterator<JsonNode> columns = builder.build().readTree(jsonResult).get("column").elements(); //$NON-NLS-1$
        setStatisticsInMetadata(metadata, columns);
    }

    private static void setStatisticsInMetadata(final DataSetMetadata metadata, final Iterator<JsonNode> columns) {
        final List<ColumnMetadata> schemaColumns = metadata.getRow().getColumns();

        while (columns.hasNext()) {
            // Get the column index
            final JsonNode column = columns.next();
            final int index = column.get("index").asInt();
            if (index >= schemaColumns.size()) {
                LOGGER.error("No column found at index {}, ignoring result", index);
                continue;
            }
            // Keeps the statistics as returned by statistics library.
            final ColumnMetadata schemaColumn = schemaColumns.get(index);
            schemaColumn.setStatistics(schemaColumn.getStatistics());
        }
    }

    private static String getSchemaAsJSONString(final DataSetMetadata metadata) throws IOException {
        // Build schema for the content (JSON format expected by statistics library).
        final StringWriter schema = new StringWriter();
        final JsonGenerator generator = new JsonFactory().createGenerator(schema);
        generator.writeStartObject();
        {
            generator.writeFieldName("column"); //$NON-NLS-1$
            generator.writeStartArray();
            {
                final List<ColumnMetadata> columns = metadata.getRow().getColumns();
                for(int i = 0; i < columns.size(); i++) {
                    final ColumnMetadata column = columns.get(i);
                    generator.writeStartObject();
                    {
                        generator.writeStringField("name", StringUtils.EMPTY); //$NON-NLS-1$
                        generator.writeStringField("id", StringUtils.EMPTY); //$NON-NLS-1$
                        generator.writeStringField("type", column.getType()); //$NON-NLS-1$
                        generator.writeStringField("suggestedType", column.getType()); //$NON-NLS-1$
                        generator.writeStringField("index", String.valueOf(i));

                        // Types
                        generator.writeArrayFieldStart("types"); //$NON-NLS-1$
                        generator.writeStartObject();
                        {
                            generator.writeStringField("name", column.getType()); //$NON-NLS-1$
                            generator.writeNumberField("occurrences", column.getQuality().getValid()); //$NON-NLS-1$
                        }
                        generator.writeEndObject();
                        generator.writeEndArray();

                        // Statistics
                        generator.writeFieldName("statistics"); //$NON-NLS-1$
                        generator.writeStartObject();
                        {
                            generator.writeNumberField("count", metadata.getContent().getNbRecords()); //$NON-NLS-1$
                            generator.writeNumberField("empty", column.getQuality().getEmpty()); //$NON-NLS-1$
                            generator.writeNumberField("valid", column.getQuality().getValid()); //$NON-NLS-1$
                            generator.writeNumberField("invalid", column.getQuality().getInvalid()); //$NON-NLS-1$
                        }
                        generator.writeEndObject();
                    }
                    generator.writeEndObject();
                }
            }
            generator.writeEndArray();
        }
        generator.writeEndObject();
        generator.flush();

        return schema.toString();
    }

    private static String getContentAsJSONString(final DataSet dataSet, Jackson2ObjectMapperBuilder builder) throws IOException {
        final StringWriter content = new StringWriter();
        builder.build().writer().writeValue(content, dataSet);
        return content.toString();
    }
}
