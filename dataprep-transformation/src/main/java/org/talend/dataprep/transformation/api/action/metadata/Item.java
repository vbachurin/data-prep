package org.talend.dataprep.transformation.api.action.metadata;

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
