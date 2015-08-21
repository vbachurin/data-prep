package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

import java.io.IOException;
import java.io.StringWriter;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Change the date pattern on a 'date' column.
 */
@Component(ChangeDatePattern.ACTION_BEAN_PREFIX + ChangeDatePattern.ACTION_NAME)
public class ChangeDatePattern extends AbstractDate implements ColumnAction {

    /** Action name. */
    public static final String ACTION_NAME = "change_date_pattern"; //$NON-NLS-1$

    /** Name of the new date pattern parameter. */
    protected static final String NEW_PATTERN = "new_pattern"; //$NON-NLS-1$

    /** The parameter object for the custom new pattern. */
    private static final String CUSTOM_PATTERN = "custom_date_pattern"; //$NON-NLS-1$

    /** The parameter object for the custom new pattern. */
    private static final Parameter CUSTOM_PATTERN_PARAMETER = new Parameter(CUSTOM_PATTERN, STRING.getName(), EMPTY);

    /** DataPrep ready jackson builder object. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ACTION_NAME;
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

        return new Item[] { new Item(NEW_PATTERN, "patterns", values.toArray(new Item.Value[values.size()])) };
    }


    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, TransformationContext, Map, String)
     */
    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {

        String newPattern = getNewPattern(parameters);
        DateTimeFormatter newDateFormat = getDateFormat(newPattern);

        // checks for fail fast
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        if (column == null) {
            return;
        }

        // parse and checks the new date pattern
        final ObjectMapper mapper = builder.build();

        // register the new pattern in column stats, to be able to process date action later
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final JsonNode patternFrequencyTable = rootNode.get("patternFrequencyTable"); //$NON-NLS-1$
        try {
            boolean isNewPatternRegistered = false;
            // loop on the existing pattern to see if thenew one is already present or not:
            for (int i = 0; i < patternFrequencyTable.size(); i++) {
                String pattern = patternFrequencyTable.get(i).get("pattern").asText(); //$NON-NLS-1$
                if (pattern.equals(newPattern)) {
                    isNewPatternRegistered = true;
                }
            }

            // if the new pattern is not yet present (ie: we're probably working on the first line)
            if (!isNewPatternRegistered) {
                JsonNode newPatternNode = mapper.createObjectNode();
                // creates a new json node with the new pattern to register, no need of occurence here
                ((ObjectNode) newPatternNode).put("pattern", newPattern);
                // add a new node, with our new pattern
                ((ArrayNode) patternFrequencyTable).add(newPatternNode);

                // save all the json tree in the stats column
                final StringWriter temp = new StringWriter(1000);
                final JsonGenerator generator = mapper.getFactory().createGenerator(temp);
                mapper.writeTree(generator, rootNode);
                column.setStatistics(temp.toString());
            }

        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_WRITE_JSON, e);
        }

        // Change the date pattern
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }
        try {
            final LocalDateTime date = superParse(value, row, columnId);
            row.set(columnId, newDateFormat.format(date));
        } catch (DateTimeException e) {
            // cannot parse the date, let's leave it as is
        }
    }

    /**
     * @param pattern the date pattern.
     * @return the simple date format out of the parameters.
     */
    private DateTimeFormatter getDateFormat(String pattern) {
        try {
            if (StringUtils.isEmpty(pattern)) {
                throw new IllegalArgumentException();
            }
            return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
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
