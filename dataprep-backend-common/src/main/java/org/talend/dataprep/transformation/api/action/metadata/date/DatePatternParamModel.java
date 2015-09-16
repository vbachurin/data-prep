package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.SelectParameter;

/**
 * This interface is designed to be implemented by actions that have a date pattern as parameter.
 */
public interface DatePatternParamModel {

    /**
     * Name of the new date pattern parameter.
     */
    String NEW_PATTERN = "new_pattern"; //$NON-NLS-1$

    /**
     * The parameter object for the custom new pattern.
     */
    String CUSTOM_PATTERN = "custom_date_pattern"; //$NON-NLS-1$

    /**
     * The parameter object for the custom new pattern.
     */
    Parameter CUSTOM_PATTERN_PARAMETER = new Parameter(CUSTOM_PATTERN, STRING.getName(), EMPTY);

    /**
     * @return the Parameters to display for the date related action.
     */
    default List<Parameter> getParametersForDatePattern() {

        ResourceBundle patterns = ResourceBundle
                .getBundle("org.talend.dataprep.transformation.api.action.metadata.date.date_patterns", Locale.ENGLISH);
        Enumeration<String> keys = patterns.getKeys();

        List<SelectParameter.Item> items = new ArrayList<>();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = patterns.getString(key);
            items.add(new SelectParameter.Item(value));
        }

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(SelectParameter.Builder.builder() //
                .name(NEW_PATTERN) //
                .items(items) //
                .item("custom", CUSTOM_PATTERN_PARAMETER) //
                .defaultValue(items.get(0).getValue()) //
                .build());

        return parameters;
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
