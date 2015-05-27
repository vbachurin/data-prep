package org.talend.dataprep.api.dataset;

import static org.springframework.util.StringUtils.isEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    /** Id of the column. */
    private String id;

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

    // Needed when objects are read back from the db.
    public ColumnMetadata() {
        // Do not remove!
    }

    /**
     * Create a column metadata from the given parameters.
     *
     * @param id the column name.
     * @param typeName the column type.
     */
    public ColumnMetadata(String id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    /**
     * @return The column name. It never returns <code>null</code> or empty string.
     */
    public String getId() {
        return id;
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

    public static class Builder {

        private Type type;

        private String name;

        private int empty;

        private int invalid;

        private int valid;

        private int headerSize;

        private String diffFlagValue = null;

        public static ColumnMetadata.Builder column() {
            return new Builder();
        }

        public ColumnMetadata.Builder name(String name) {
            if (isEmpty(name)) {
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

        public ColumnMetadata.Builder copy(ColumnMetadata original) {
            this.name = original.getId();
            Quality originalQuality = original.getQuality();
            this.empty = originalQuality.getEmpty();
            this.invalid = originalQuality.getInvalid();
            this.valid = originalQuality.getValid();
            this.headerSize = original.getHeaderSize();
            this.type = Type.get(original.getType());
            this.diffFlagValue = original.getDiffFlagValue();
            return this;
        }

        public ColumnMetadata build() {
            ColumnMetadata columnMetadata = new ColumnMetadata(name, type.getName());
            columnMetadata.getQuality().setEmpty(empty);
            columnMetadata.getQuality().setInvalid(invalid);
            columnMetadata.getQuality().setValid(valid);
            columnMetadata.setHeaderSize(this.headerSize);
            columnMetadata.setDiffFlagValue(this.diffFlagValue);
            return columnMetadata;
        }
    }
}
