package org.talend.dataprep.dataset.objects;

import org.talend.dataprep.dataset.objects.type.Type;

public class ColumnMetadata {

    private final String name;

    private final Type   type;

    private ColumnMetadata(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColumnMetadata)) {
            return false;
        }
        ColumnMetadata that = (ColumnMetadata) o;
        return name.equals(that.name) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public static class Builder {

        private Type   type;

        private String name;

        public static ColumnMetadata.Builder column() {
            return new Builder();
        }

        public ColumnMetadata.Builder name(String name) {
            this.name = name;
            return this;
        }

        public ColumnMetadata.Builder type(Type type) {
            this.type = type;
            return this;
        }

        public ColumnMetadata build() {
            return new ColumnMetadata(name, type);
        }
    }
}
