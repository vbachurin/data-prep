package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Locale;

import org.talend.dataprep.transformation.i18n.MessagesBundle;

public class Item {

    String name;

    Type type;

    String category;

    Value[] values;

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

    public String getLabel() {
        return getLabel(Locale.ENGLISH);
    }

    public String getDescription() {
        return getDescription(Locale.ENGLISH);
    }

    /**
     * the label of the parameter, translated in the user locale
     */
    public String getLabel(Locale locale) {
        return MessagesBundle.getString(locale, "parameter." + getName() + ".label");
    }

    /**
     * the description of the parameter, translated in the user locale
     */
    public String getDescription(Locale locale) {
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

        String name;

        boolean isDefault = false;

        public Value(String name) {
            super();
            this.name = name;
        }

        public Value(String name, boolean isDefault) {
            super();
            this.name = name;
            this.isDefault = isDefault;
        }

        public String getName() {
            return this.name;
        }

        public boolean isDefault() {
            return this.isDefault;
        }

    }
}
