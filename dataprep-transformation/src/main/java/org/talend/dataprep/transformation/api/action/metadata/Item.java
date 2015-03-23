package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.talend.dataprep.i18n.MessagesBundle;

public class Item {

    private final String name;

    private final Type type;

    private final String category;

    private final Value[] values;

    public Item(String name, Type type, String category, Value[] values) {
        super();
        this.name = name;
        this.type = type;
        this.category = category;
        this.values = values;
    }

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

    public Type getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public Value[] getValues() {
        return values;
    }

    public static class Value {

        final String name;

        boolean isDefault = false;

        final Parameter[] parameters;

        public Value(String name, Parameter... parameters) {
            super();
            this.name = name;
            this.parameters = parameters;
        }

        public Value(String name, boolean isDefault, Parameter... parameters) {
            super();
            this.name = name;
            this.isDefault = isDefault;
            this.parameters = parameters;
        }

        public String getName() {
            return this.name;
        }

        public boolean isDefault() {
            return this.isDefault;
        }

        public Parameter[] getParameters() {
            return parameters;
        }

    }
}
