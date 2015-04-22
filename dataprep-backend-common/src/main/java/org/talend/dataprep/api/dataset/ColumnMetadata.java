package org.talend.dataprep.api.dataset;

import org.springframework.util.StringUtils;
import org.talend.dataprep.api.type.Type;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents information about a column in a data set. It includes:
 * <ul>
 * <li>Name ({@link #getId()})</li>
 * <li>Type ({@link #getType()})</li>
 * </ul>
 * 
 * @see ColumnMetadata.Builder
 */
public class ColumnMetadata {

    private final Quality quality = new Quality();

    private String name;

    private String typeName;

    // number of first lines with a text header
    // non per default
    private int headerSize = 0;

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
    public String getId() {
        return name;
    }

    /**
     * @return The column's type. It never returns <code>null</code>.
     * @see org.talend.dataprep.api.type.Type
     */
    public String getType() {
        return typeName;
    }

    public void setType(String typeName) {
        this.typeName = typeName;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public Quality getQuality() {
        return quality;
    }

    @Override
    public boolean equals(Object obj) {
        return Optional.ofNullable( obj ) //
            .filter(that -> that instanceof ColumnMetadata) //
            .map(that -> (ColumnMetadata) that) //
            .filter(that -> Objects.equals( this.name, that.name )) //
            .filter(that -> Objects.equals(this.typeName, that.typeName)) //
            .isPresent();
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + typeName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ColumnMetadata{" + "quality=" + quality + ", name='" + name + '\'' + ", typeName='" + typeName + '\''
                + ", headerSize=" + headerSize + '}';
    }

    public static class Builder {

        private Type type;

        private String name;

        private int empty;

        private int invalid;

        private int valid;

        private int headerSize;

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

        public ColumnMetadata.Builder empty(int value) {
            empty = value;
            return this;
        }

        public ColumnMetadata.Builder invalid(int value) {
            invalid = value;
            return this;
        }

        public ColumnMetadata.Builder valid(int value) {
            valid = value;
            return this;
        }

        public ColumnMetadata.Builder headerSize(int headerSize) {
            this.headerSize = headerSize;
            return this;
        }

        public ColumnMetadata build() {
            ColumnMetadata columnMetadata = new ColumnMetadata(name, type.getName());
            columnMetadata.getQuality().setEmpty(empty);
            columnMetadata.getQuality().setInvalid(invalid);
            columnMetadata.getQuality().setValid(valid);
            columnMetadata.setHeaderSize(this.headerSize);
            return columnMetadata;
        }
    }
}
