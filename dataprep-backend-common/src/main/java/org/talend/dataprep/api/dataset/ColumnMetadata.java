package org.talend.dataprep.api.dataset;

import static org.springframework.util.StringUtils.isEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.diff.FlagNames;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    /** Quality of the column. */
    @JsonProperty("quality")
    private final Quality quality = new Quality();

    /** Technical id of the column (generated when instantiated). */
    @JsonProperty("id")
    private String id;

    /** Human readable name of the column. */
    private String name;

    /** Type of the column (N/A as default). */
    @JsonProperty("type")
    private String typeName = "N/A"; //$NON-NLS-1$

    /** Number of first lines with a text header (none per default). */
    private int headerSize = 0;

    /** Optional diff flag that shows diff status of this column metadata. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = FlagNames.COLUMN_DIFF_KEY)
    private String diffFlagValue;

    /** Statistics of the column. */
    @JsonProperty("statistics")
    @JsonRawValue
    private String statistics = "{}"; //$NON-NLS-1$

    @JsonProperty("domain")
    private String domain;

    /**
     * Default empty constructor.
     */
    public ColumnMetadata() {

    }

    /**
     * Create a column metadata from the given parameters.
     *
     * @param id the column technical id.
     * @param name the column name.
     * @param typeName the column type.
     */
    private ColumnMetadata(int id, String name, String typeName) {
        this.id = computeInternalId(id);
        this.name = name;
        this.typeName = typeName;
    }

    /**
     * Create a column metadata from the given parameters.
     *
     * @param computedId the column computed id.
     * @param name the column name.
     * @param typeName the column type.
     */
    private ColumnMetadata(String computedId, String name, String typeName) {
        if (StringUtils.isEmpty(computedId)) {
            throw new IllegalArgumentException("computed id cannot be null for a column metadata");
        }
        this.id = computedId;
        this.name = name;
        this.typeName = typeName;
    }

    /**
     * Set and convert the given id : make sure the id is padded with '000'. So dataset up to 1000 columns should be ok.
     *
     * @param id the id as integer.
     * @return the formatted id.
     */
    private String computeInternalId(int id) {
        DecimalFormat format = new DecimalFormat("0000"); //$NON-NLS-1$
        return format.format(id);
    }

    /**
     * @return The column name. It never returns <code>null</code> or empty string.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the column name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The column's type. It never returns <code>null</code>.
     * @see org.talend.dataprep.api.type.Type
     */
    public String getType() {
        return typeName;
    }

    /**
     * @param typeName the typename to set.
     */
    public void setType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the diff flag.
     */
    public String getDiffFlagValue() {
        return diffFlagValue;
    }

    /**
     * @param diffFlagValue the diff flag to set.
     */
    public void setDiffFlagValue(String diffFlagValue) {
        this.diffFlagValue = diffFlagValue;
    }

    /**
     * @return the header size.
     */
    public int getHeaderSize() {
        return headerSize;
    }

    /**
     * @param headerSize the header size to set.
     */
    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * @return the quality.
     */
    public Quality getQuality() {
        return quality;
    }

    @Override
    public boolean equals(Object obj) {
        return Optional.ofNullable(obj) //
                .filter(that -> that instanceof ColumnMetadata) //
                .map(that -> (ColumnMetadata) that) //
                .filter(that -> Objects.equals(this.diffFlagValue, that.diffFlagValue)) //
                .filter(that -> Objects.equals(this.id, that.id)) //
                .filter(that -> Objects.equals(this.name, that.name)) //
                .filter(that -> Objects.equals(this.typeName, that.typeName)) //
                .isPresent();
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + typeName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ColumnMetadata{" + //
                "quality=" + quality + //
                ", id='" + id + '\'' + //
                ", name='" + name + '\'' + //
                ", typeName='" + typeName + '\'' + //
                ", headerSize=" + headerSize + //
                ", diffFlagValue='" + diffFlagValue + '\'' + //
                ", statistics='" + statistics + '\'' + //
                '}';
    }

    /**
     * @return The statistics (as raw JSON content) returned by data quality library.
     */
    @JsonRawValue
    public String getStatistics() {
        return statistics;
    }

    /**
     * Sets the statistics as returned by data quality library.
     *
     * @param statistics The statistics as returned by the data quality library.
     */
    public void setStatistics(Object statistics) {
        if (statistics == null) {
            this.statistics = "{}"; //$NON-NLS-1$
        } else {
            if (statistics instanceof Map) {
                try {
                    final StringWriter writer = new StringWriter();
                    new ObjectMapper().writer().writeValue(writer, statistics);
                    this.statistics = writer.toString();
                } catch (IOException e) {
                    throw new TDPException(CommonErrorCodes.UNABLE_TO_SERIALIZE_TO_JSON, e);
                }
            } else if (statistics instanceof String) {
                this.statistics = String.valueOf(statistics);
            } else {
                throw new IllegalArgumentException("Received a '" + statistics.getClass().getName()
                        + "' but don't know how to interpret it.");
            }
        }
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    /**
     * This class builder to ease the constructor.
     */
    public static class Builder {

        /** The column id. */
        private int id;

        /** The column computedId. */
        private String computedId;

        /** The column name. */
        private String name;

        /** The column type. */
        private Type type;

        /** The column empty value. */
        private int empty;

        /** The column invalid value. */
        private int invalid;

        /** The columne valid value. */
        private int valid;

        /** The column header size. */
        private int headerSize;

        /** The column diff flag (null by default). */
        private String diffFlagValue = null;

        /**
         * @return A ColumnMetadata builder.
         */
        public static ColumnMetadata.Builder column() {
            return new Builder();
        }

        /**
         * Set the name of the column.
         * 
         * @param name the name of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder name(String name) {
            if (isEmpty(name)) {
                throw new IllegalArgumentException("Name cannot be null or empty.");
            }
            this.name = name;
            return this;
        }

        /**
         * Set the id of the column.
         * 
         * @param id the id of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Set the computed id of the column.
         *
         * @param computedId the computed id of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder computedId(String computedId) {
            this.computedId = computedId;
            return this;
        }

        /**
         * Set the type of the column.
         * 
         * @param type the type of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder type(Type type) {
            if (type == null) {
                throw new IllegalArgumentException("Type cannot be null.");
            }
            this.type = type;
            return this;
        }

        /**
         * Set the empty value of the column.
         * 
         * @param value the empty value of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder empty(int value) {
            empty = value;
            return this;
        }

        /**
         * Set the invalid value of the column.
         * 
         * @param value the invalid value of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder invalid(int value) {
            invalid = value;
            return this;
        }

        /**
         * Set the valid value of the column.
         * 
         * @param value the valid value of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder valid(int value) {
            valid = value;
            return this;
        }

        /**
         * Set the header size value of the column.
         * 
         * @param headerSize the header size value of the column to set.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder headerSize(int headerSize) {
            this.headerSize = headerSize;
            return this;
        }

        /**
         * Copy the column from the given one.
         * 
         * @param original the column to copy.
         * @return the builder to carry on building the column.
         */
        public ColumnMetadata.Builder copy(ColumnMetadata original) {
            this.computedId = original.getId();
            this.name = original.getName();
            Quality originalQuality = original.getQuality();
            this.empty = originalQuality.getEmpty();
            this.invalid = originalQuality.getInvalid();
            this.valid = originalQuality.getValid();
            this.headerSize = original.getHeaderSize();
            this.type = Type.get(original.getType());
            this.diffFlagValue = original.getDiffFlagValue();
            return this;
        }

        /**
         * Build the column with the previously entered values.
         * 
         * @return the buit column metadata.
         */
        public ColumnMetadata build() {
            ColumnMetadata columnMetadata;
            if (StringUtils.isEmpty(computedId)) {
                columnMetadata = new ColumnMetadata(id, name, type.getName());
            } else {
                columnMetadata = new ColumnMetadata(computedId, name, type.getName());
            }
            columnMetadata.getQuality().setEmpty(empty);
            columnMetadata.getQuality().setInvalid(invalid);
            columnMetadata.getQuality().setValid(valid);
            columnMetadata.setHeaderSize(this.headerSize);
            columnMetadata.setDiffFlagValue(this.diffFlagValue);
            return columnMetadata;
        }
    }
}
