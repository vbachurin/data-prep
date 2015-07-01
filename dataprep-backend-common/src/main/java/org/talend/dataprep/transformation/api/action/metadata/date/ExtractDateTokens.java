package org.talend.dataprep.transformation.api.action.metadata.date;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;

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

    private static final String YEAR = "_YEAR";

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

            ColumnMetadata column = rowMetadata.getById(columnId);
            // defensive programming
            if (column == null) {
                return;
            }

            // store the current pattern in the context
            JsonNode rootNode = getStatisticsNode(mapper, column);

            JsonNode mostUsedPatternNode = rootNode.get("patternFrequencyTable").get(0); //$NON-NLS-1$
            String futureExPattern = mostUsedPatternNode.get("pattern").asText(); //$NON-NLS-1$
            context.put(PATTERN, futureExPattern);

            // go through the columns to be able to 'insert' the new columns just after the one needed.
            for (int i = 0; i < rowMetadata.getColumns().size(); i++) {

                column = rowMetadata.getColumns().get(i);
                if (!StringUtils.equals(column.getId(), columnId)) {
                    continue;
                }

                // get the new column id
                List<String> columnIds = new ArrayList<>(rowMetadata.size());
                rowMetadata.getColumns().forEach(columnMetadata -> columnIds.add(columnMetadata.getId()));

                // create the new column
                ColumnMetadata newColumnMetadata = ColumnMetadata.Builder //
                        .column() //
                        .computedId(column.getId() + YEAR) //
                        .name(column.getName() + YEAR) //
                        .type(Type.get(column.getType())) //
                        .empty(column.getQuality().getEmpty()) //
                        .invalid(column.getQuality().getInvalid()) //
                        .valid(column.getQuality().getValid()) //
                        .headerSize(column.getHeaderSize()) //
                        .build();

                // add the new column after the current one
                rowMetadata.getColumns().add(i + 1, newColumnMetadata);

            }
        };
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
            SimpleDateFormat currentDateFormat = getDateFormat((String) context.get(PATTERN));

            // parse the
            String value = row.get(columnId);
            Date date = null;
            try {
                date = currentDateFormat.parse(value);
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(date);

                row.set(columnId + YEAR, gc.get(GregorianCalendar.YEAR) + "");
            } catch (ParseException e) {
                // cannot parse the date, let's leave it as is
            }
        };
    }

    /**
     * @param pattern the date pattern.
     * @return the simple date format out of the parameters.
     */
    private SimpleDateFormat getDateFormat(String pattern) {
        try {
            if (StringUtils.isEmpty(pattern)) {
                throw new IllegalArgumentException();
            }
            return new SimpleDateFormat(pattern, Locale.ENGLISH);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("pattern '" + pattern + "' is not a valid date pattern", iae);
        }

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
