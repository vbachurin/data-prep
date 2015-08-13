package org.talend.dataprep.api.dataset.diff;

/**
 * Interface that holds constants on flag names.
 */
public final class FlagNames {

    /** Key to put in the result values that tells that there are differences at column level. */
    public static final String DIFF_KEY = "__tdpDiff";

    /** Key to put in the result values that tells that there is a difference at row level. */
    public static final String ROW_DIFF_KEY = "__tdpRowDiff";

    /** Key to put in the result values that tells that there is a difference at column level. */
    public static final String COLUMN_DIFF_KEY = "__tdpColumnDiff";

    /**
     * Default private constructor.
     */
    private FlagNames() {
        // private constructor for utility class
    }

}
