package org.talend.dataprep.transformation.api.action.metadata.date;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Change the date pattern on a 'date' column.
 */
@Component(ChangeDatePattern.ACTION_BEAN_PREFIX + ChangeDatePattern.ACTION_NAME)
public class ChangeDatePattern extends SingleColumnAction {

    /** Action name. */
    public static final String ACTION_NAME = "change_date_pattern"; //$NON-NLS-1$
    /** Name of the new date pattern parameter. */
    protected static final String NEW_PATTERN = "new_pattern"; //$NON-NLS-1$

    /** The parameter object for the new date pattern. */
    private static final Parameter NEW_PATTERN_PARAMETER = new Parameter(NEW_PATTERN, Type.STRING.getName(), StringUtils.EMPTY);

    /** Name of the old pattern parameter. */
    protected static final String OLD_PATTERN = "old_pattern"; //$NON-NLS-1$


    /**
     * @see ActionMetadata#createMetadataClosure(Map)
     */
    @Override
    public BiConsumer<RowMetadata, TransformationContext> createMetadataClosure(Map<String, String> parameters) {

        // check the column id parameter
        String columnId = getColumnIdParameter(parameters);

        // parse and checks the new date pattern

        String newPattern = getDateFormat(parameters.get(NEW_PATTERN)).toPattern();

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
            context.put(OLD_PATTERN, futureExPattern);

            // update the pattern in the column
            try {
                ((ObjectNode) mostUsedPatternNode).put("pattern", newPattern); //$NON-NLS-1$
                StringWriter temp = new StringWriter(1000);

                JsonGenerator generator = jsonFactory.createGenerator(temp);
                mapper.writeTree(generator, rootNode);
                column.setStatistics(temp.toString());
            } catch (IOException e) {
                throw new TDPException(CommonErrorCodes.UNABLE_TO_WRITE_JSON, e);
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
        SimpleDateFormat newDateFormat = getDateFormat(parameters.get(NEW_PATTERN));

        return (row, context) -> {

            // sadly unable to do that outside of the closure since the context is not available
            SimpleDateFormat currentDateFormat = getDateFormat((String) context.get(OLD_PATTERN));

            // parse the
            String value = row.get(columnId);
            Date date = null;
            try {
                date = currentDateFormat.parse(value);
                row.set(columnId, newDateFormat.format(date));
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
            return new SimpleDateFormat(pattern);
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
     * @see ActionMetadata#getParameters()
     */
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER, NEW_PATTERN_PARAMETER };
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
