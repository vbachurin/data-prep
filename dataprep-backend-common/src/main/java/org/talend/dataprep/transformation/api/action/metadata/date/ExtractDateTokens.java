package org.talend.dataprep.transformation.api.action.metadata.date;

import java.io.IOException;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Change the date pattern on a 'date' column.
 */
@Component(ExtractDateTokens.ACTION_BEAN_PREFIX + ExtractDateTokens.ACTION_NAME)
public class ExtractDateTokens extends SingleColumnAction {

    /** Action name. */
    public static final String ACTION_NAME = "extract_date_tokens"; //$NON-NLS-1$

    /** Name of the date pattern. */
    protected static final String PATTERN = "date_pattern"; //$NON-NLS-1$

    private static final String YEAR = "YEAR";

    private static final String MONTH = "MONTH";

    private static final String DAY = "DAY";

    private static final DateFieldMappingBean[] DATE_FIELDS = new DateFieldMappingBean[] {//
    new DateFieldMappingBean(YEAR, ChronoField.YEAR),//
            new DateFieldMappingBean(MONTH, ChronoField.MONTH_OF_YEAR),//
            new DateFieldMappingBean(DAY, ChronoField.DAY_OF_MONTH) //
    };

    private static class DateFieldMappingBean {

        String key;

        ChronoField field;

        public DateFieldMappingBean(String key, ChronoField field) {
            super();
            this.key = key;
            this.field = field;
        }

    }

    private static final String SEPARATOR = "_";

    /**
     * @see ActionMetadata#createMetadataClosure(Map)
     */
    @Override
    public BiConsumer<RowMetadata, TransformationContext> createMetadataClosure(Map<String, String> parameters) {

        // check the column id parameter
        String columnId = getColumnIdParameter(parameters);

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(jsonFactory);

        return (rowMetadata, context) -> {

            List<ColumnMetadata> newColumns = new ArrayList<>(rowMetadata.size() + 1);

            for (ColumnMetadata column : rowMetadata.getColumns()) {
                ColumnMetadata newColumnMetadata = ColumnMetadata.Builder.column().copy(column).build();
                newColumns.add(newColumnMetadata);

                // append the split column
                if (StringUtils.equals(columnId, column.getId())) {
                    // apply the new columns to the row metadata
                    rowMetadata.setColumns(newColumns);

                    // store the current pattern in the context
                    JsonNode rootNode = getStatisticsNode(mapper, column);

                    JsonNode mostUsedPatternNode = rootNode.get("patternFrequencyTable").get(0); //$NON-NLS-1$
                    String datePattern = mostUsedPatternNode.get("pattern").asText(); //$NON-NLS-1$
                    context.put(PATTERN, datePattern);

                    for (DateFieldMappingBean date_field : DATE_FIELDS) {
                        if (new Boolean(parameters.get(date_field.key))) {
                            // create the new column
                            newColumnMetadata = createNewColumn(column, date_field.key);

                            // add the new column after the current one
                            rowMetadata.getColumns().add(newColumnMetadata);
                        }
                    }
                }
            }

        };
    }

    /**
     * DOC stef Comment method "createNewColumn".
     * @param column
     * @return
     */
    private ColumnMetadata createNewColumn(ColumnMetadata column, String suffix) {
        ColumnMetadata newColumnMetadata = ColumnMetadata.Builder //
                .column() //
                .computedId(column.getId() + SEPARATOR + suffix) //
                .name(column.getName() + SEPARATOR + suffix) //
                .type(Type.INTEGER) //
                .empty(column.getQuality().getEmpty()) //
                .invalid(column.getQuality().getInvalid()) //
                .valid(column.getQuality().getValid()) //
                .headerSize(column.getHeaderSize()) //
                .build();
        return newColumnMetadata;
    }

    /**
     * @param parameters the parameters.
     * @return the column id parameter.
     */
    private String getColumnIdParameter(Map<String, String> parameters) {
        String columnId = parameters.get(COLUMN_ID);
        if (columnId == null) {
            throw new IllegalArgumentException("Parameter '" + COLUMN_ID + "' is required for this action");
        }
        return columnId;
    }

    /**
     * Return the json statistics node.
     *
     * @param mapper jackson object mapper.
     * @param column the column metadata to work on.
     * @return the json statistics node.
     */
    private JsonNode getStatisticsNode(ObjectMapper mapper, ColumnMetadata column) {
        try {
            return mapper.readTree(column.getStatistics());
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public BiConsumer<DataSetRow, TransformationContext> create(Map<String, String> parameters) {

        String columnId = getColumnIdParameter(parameters);

        return (row, context) -> {

            // sadly unable to do that outside of the closure since the context is not available
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern((String) context.get(PATTERN));

            // parse the
            String value = row.get(columnId);
            TemporalAccessor temporalAccessor = null;
            try {
                temporalAccessor = dtf.parse(value, new ParsePosition(0));

                for (DateFieldMappingBean date_field : DATE_FIELDS) {
                    if (new Boolean(parameters.get(date_field.key))) {
                        row.set(columnId + SEPARATOR + date_field.key, temporalAccessor.get(date_field.field) + "");
                    }
                }
            } catch (DateTimeParseException e) {
                for (DateFieldMappingBean date_field : DATE_FIELDS) {
                    if (new Boolean(parameters.get(date_field.key))) {
                        row.set(columnId + SEPARATOR + date_field.key, "");
                    }
                }
            }
        };
    }

    @Override
    @Nonnull
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER, //
                new Parameter(YEAR, Type.BOOLEAN.getName(), "true"),//
                new Parameter(MONTH, Type.BOOLEAN.getName(), "true"),//
                new Parameter(DAY, Type.BOOLEAN.getName(), "true") };
    }

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.DATE.getDisplayName();
    }

    /**
     * Only works on 'date' columns.
     * 
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.DATE.equals(Type.get(column.getType()));
    }
}
