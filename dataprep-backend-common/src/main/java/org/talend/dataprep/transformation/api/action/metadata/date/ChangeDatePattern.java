package org.talend.dataprep.transformation.api.action.metadata.date;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

/**
 * Change the date pattern on a 'date' column.
 */
@Component(ChangeDatePattern.ACTION_BEAN_PREFIX + ChangeDatePattern.ACTION_NAME)
public class ChangeDatePattern extends AbstractActionMetadata implements IColumnAction {

    /**
     * Action name.
     */
    public static final String ACTION_NAME = "change_date_pattern"; //$NON-NLS-1$

    /**
     * Name of the new date pattern parameter.
     */
    protected static final String NEW_PATTERN = "new_pattern"; //$NON-NLS-1$

    /**
     * The parameter object for the custom new pattern.
     */
    private static final String CUSTOM_PATTERN = "custom_date_pattern"; //$NON-NLS-1$

    /**
     * The parameter object for the custom new pattern.
     */
    private static final Parameter CUSTOM_PATTERN_PARAMETER = new Parameter(CUSTOM_PATTERN, STRING.getName(), EMPTY);

    /**
     * The date formatter to use. It must be init before the transformation
     */
    private SimpleDateFormat newDateFormat;

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
     * @see ActionMetadata#getItems()@return
     */
    @Override
    @Nonnull
    public Item[] getItems() {

        ResourceBundle patterns = ResourceBundle.getBundle(
                "org.talend.dataprep.transformation.api.action.metadata.date.date_patterns", Locale.ENGLISH);
        Enumeration<String> keys = patterns.getKeys();
        List<Item.Value> values = new ArrayList<>();
        while (keys.hasMoreElements()) {
            Item.Value currentValue = new Item.Value(patterns.getString(keys.nextElement()));
            values.add(currentValue);
        }

        values.add(new Item.Value("custom", CUSTOM_PATTERN_PARAMETER));
        values.get(0).setDefault(true);

        return new Item[]{new Item(NEW_PATTERN, "patterns", values.toArray(new Item.Value[values.size()]))};
    }

    /**
     * Only works on 'date' columns.
     *
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.DATE.equals(Type.get(column.getType()));
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
        newDateFormat = getDateFormat(getNewPattern(parameters));
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        // checks for fail fast
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        if (column == null) {
            return;
        }

        // parse and checks the new date pattern
        final String newPattern = newDateFormat.toPattern();
        final JsonFactory jsonFactory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        // store the current pattern in the context
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final JsonNode mostUsedPatternNode = rootNode.get("patternFrequencyTable").get(0); //$NON-NLS-1$
        final String futureExPattern = mostUsedPatternNode.get("pattern").asText(); //$NON-NLS-1$

        // update the pattern in the column
        try {
            ((ObjectNode) mostUsedPatternNode).put("pattern", newPattern); //$NON-NLS-1$
            final StringWriter temp = new StringWriter(1000);

            final JsonGenerator generator = jsonFactory.createGenerator(temp);
            mapper.writeTree(generator, rootNode);
            column.setStatistics(temp.toString());
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_WRITE_JSON, e);
        }

        // Change the date pattern
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }
        final SimpleDateFormat currentDateFormat = getDateFormat(futureExPattern);
        try {
            final Date date = currentDateFormat.parse(value);
            row.set(columnId, newDateFormat.format(date));
        } catch (ParseException e) {
            // cannot parse the date, let's leave it as is
        }
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
     * Get the new pattern from parameters
     *
     * @param parameters the parameters map
     * @return the new date pattern
     */
    private String getNewPattern(Map<String, String> parameters) {
        return "custom".equals(parameters.get(NEW_PATTERN)) ? parameters.get(CUSTOM_PATTERN) : parameters.get(NEW_PATTERN);
    }

}
