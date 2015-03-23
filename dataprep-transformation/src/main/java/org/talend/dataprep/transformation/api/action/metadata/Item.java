package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Locale;

import org.talend.dataprep.transformation.i18n.MessagesBundle;

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

    private String getName() {
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
    private String getLabel(Locale locale) {
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
    private String getDescription(Locale locale) {
        return MessagesBundle.getString(locale, "parameter." + getName() + ".desc");
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
