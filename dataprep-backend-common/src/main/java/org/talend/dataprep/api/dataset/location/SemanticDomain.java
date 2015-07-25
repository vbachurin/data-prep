package org.talend.dataprep.api.dataset.location;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents information about alternate semantic type
 * <ul>
 * <li>Id ({@link #getId()})</li>
 * <li>Label ({@link #getLabel()})</li>
 * <li>Count how many times this type is matched ({@link #getCount()})</li>
 * </ul>
 *
 */
public class SemanticDomain {

    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    private String label;

    @JsonProperty("count")
    private long count;

    public SemanticDomain() {
        // empty default constructor
    }

    public SemanticDomain(String id, String label, long count) {
        this.id = id;
        this.label = label;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SemanticDomain{" + "id='" + id + '\'' + ", label='" + label + '\'' + ", count=" + count + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SemanticDomain that = (SemanticDomain) o;
        return Objects.equals(count, that.count) //
                && Objects.equals(id, that.id) //
                && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, count);
    }
}
