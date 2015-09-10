package org.talend.dataprep.transformation.api.action.metadata.date;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

/**
 * This interface is designed to be implemented by actions that have a date pattern as parameter.
 */
public interface DatePatternParamModel extends ColumnAction {

    /**
     * Name of the new date pattern parameter.
     */
    static final String NEW_PATTERN = "new_pattern"; //$NON-NLS-1$

    /**
     * The parameter object for the custom new pattern.
     */
    static final String CUSTOM_PATTERN = "custom_date_pattern"; //$NON-NLS-1$

    /**
     * The parameter object for the custom new pattern.
     */
    static final Parameter CUSTOM_PATTERN_PARAMETER = new Parameter(CUSTOM_PATTERN, STRING.getName(), EMPTY);

    default Item[] getItemsForDatePattern() {

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
     * Get the new pattern from parameters.
     *
     * @param parameters the parameters map
     * @return a DatePattern object representing the pattern
     */
    default DatePattern getDateFormat(Map<String, String> parameters) {
        String pattern = "custom".equals(parameters.get(NEW_PATTERN)) ? parameters.get(CUSTOM_PATTERN) : parameters.get(NEW_PATTERN);
        try {
            if (StringUtils.isEmpty(pattern)) {
                throw new IllegalArgumentException();
            }
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
            return new DatePattern(pattern, dateTimeFormatter);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("pattern '" + pattern + "' is not a valid date pattern", iae);
        }

    }

}
