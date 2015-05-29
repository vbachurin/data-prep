package org.talend.dataprep.api.dataset.diff;

/**
 * Interface that holds constants on flag names.
 */
public interface FlagNames {

    /** Key to put in the result values that tells that there are differences at column level. */
    String DIFF_KEY = "__tdpDiff";

    /** Key to put in the result values that tells that there is a difference at row level. */
    String ROW_DIFF_KEY = "__tdpRowDiff";

    /** Key to put in the result values that tells that there is a difference at column level. */
    String COLUMN_DIFF_KEY = "__tdpColumnDiff";

}
