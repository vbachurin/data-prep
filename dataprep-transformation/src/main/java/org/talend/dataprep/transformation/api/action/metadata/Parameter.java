package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Locale;

import org.talend.dataprep.transformation.i18n.MessagesBundle;

public class Parameter {

    private final String name;

    private final String type;

    private final String defaultValue;

    public Parameter(String name, String type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    /**
     * the unique identifier of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Temp method, cleaner solution should use Spring mappoiing with following method.
     */
    public String getLabel() {
        return getLabel(Locale.ENGLISH);
    }

    /**
     * the label of the parameter, translated in the user locale.
     */
    public String getLabel(Locale locale) {
        return MessagesBundle.getString(locale, "parameter." + getName() + ".label");
    }

    /**
     * Temp method, cleaner solution should use Spring mappoiing with following method.
     */
    public String getDescription() {
        return getDescription(Locale.ENGLISH);
    }

    /**
     * the description of the parameter, translated in the user locale.
     */
    public String getDescription(Locale locale) {
        return MessagesBundle.getString(locale, "parameter." + getName() + ".desc");
    }

    public String getType() {
        return type;
    }

    public String getDefault() {
        return defaultValue;
    }
}
