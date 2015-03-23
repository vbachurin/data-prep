package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.talend.dataprep.i18n.MessagesBundle;

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
     * the label of the parameter, translated in the user locale.
     */
    public String getLabel() {
        Locale locale = LocaleContextHolder.getLocale();
        return MessagesBundle.getString("parameter." + getName() + ".label", locale);
    }

    /**
     * the description of the parameter, translated in the user locale.
     */
    public String getDescription() {
        Locale locale = LocaleContextHolder.getLocale();
        return MessagesBundle.getString("parameter." + getName() + ".desc", locale);
    }

    public String getType() {
        return type;
    }

    public String getDefault() {
        return defaultValue;
    }
}
