package org.talend.dataprep.dataset.objects;

import org.springframework.util.StringUtils;
import org.talend.dataprep.dataset.objects.type.Type;

/**
 * Represents information about a column in a data set. It includes:
 * <ul>
 * <li>Name ({@link #getName()})</li>
 * <li>Type ({@link #getTypeName()})</li>
 * </ul>
 * @see org.talend.dataprep.dataset.objects.ColumnMetadata.Builder
 */
public class ColumnMetadata {

    private String name;

    private String typeName;

    // Needed when objects are read back from the db.
    public ColumnMetadata() {
        // Do not remove!
    }

    public ColumnMetadata(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    /**
     * @return The column name. It never returns <code>null</code> or empty string.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The column's type. It never returns <code>null</code>.
     * @see org.talend.dataprep.dataset.objects.type.Type
     */
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
        return name.equals(that.name) && typeName.equals(that.typeName);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + typeName.hashCode();
        return result;
    }

    public static class Builder {

        private Type   type;

        private String name;

        public static ColumnMetadata.Builder column() {
            return new Builder();
        }

        public ColumnMetadata.Builder name(String name) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Name cannot be null or empty.");
            }
            this.name = name;
            return this;
        }

        public ColumnMetadata.Builder type(Type type) {
            if (type == null) {
                throw new IllegalArgumentException("Type cannot be null.");
            }
            this.type = type;
            return this;
        }

        public ColumnMetadata build() {
            return new ColumnMetadata(name, type.getName());
        }
    }
}
