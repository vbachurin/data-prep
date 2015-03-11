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

        public Value(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

    }
}
