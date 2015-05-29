package org.talend.dataprep.api.dataset.diff;

/**
 * Flag used for the diff.
 */
public enum Flag {

    DELETE("delete"),
    NEW("new"),
    UPDATE("update");

    /** Expected frontend value. */
    private String value;

    /**
     * Constructor with the given value.
     *
     * @param value the expected frontend value.
     */
    Flag(String value) {
        this.value = value;
    }

    /**
     * @return the frontend expected value.
     */
    public String getValue() {
        return value;
    }

}
