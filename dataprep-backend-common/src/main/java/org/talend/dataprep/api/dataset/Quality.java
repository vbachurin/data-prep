package org.talend.dataprep.api.dataset;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Quality bean linked to column metadata.
 */
public class Quality {

    /** Number of empty records. */
    @JsonProperty("empty")
    private int empty;

    /** Number of invalid records. */
    @JsonProperty("invalid")
    private int invalid;

    /** Number of valid records. */
    @JsonProperty("valid")
    private int valid;

    /** List of invalid values. */
    @JsonProperty("invalidValues")
    private Set<String> invalidValues = new HashSet<>();

    /**
     * @return the number of empty records.
     */
    public int getEmpty() {
        return empty;
    }

    /**
     * @param empty the number of empty records to set.
     */
    public void setEmpty(int empty) {
        this.empty = empty;
    }

    /**
     * @return the number of invalid records.
     */
    public int getInvalid() {
        return invalid;
    }

    /**
     * @param invalid the number of invalid records to set.
     */
    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }

    /**
     * @return the number of valid records.
     */
    public int getValid() {
        return valid;
    }

    /**
     * @param valid the number of valid records to set.
     */
    public void setValid(int valid) {
        this.valid = valid;
    }

    /**
     * @return the invalid values
     */
    public Set<String> getInvalidValues() {
        return invalidValues;
    }

    /**
     * @param invalidValues the invalid values to set.
     */
    public void setInvalidValues(Set<String> invalidValues) {
        this.invalidValues = invalidValues;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Quality{" + "empty=" + empty + ", invalid=" + invalid + ", valid=" + valid + '}';
    }
}
