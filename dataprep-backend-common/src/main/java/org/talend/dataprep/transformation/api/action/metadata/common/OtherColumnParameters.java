package org.talend.dataprep.transformation.api.action.metadata.common;

/**
 * This interface mutualize code for actions that have a parameter which is a choice between constant/other column mode.
 */
public interface OtherColumnParameters {

    /**
     * The selected column id.
     */
    public static final String SELECTED_COLUMN_PARAMETER = "selected_column"; //$NON-NLS-1$

    /**
     * Mode: tells if fill value is taken from another column or is a constant
     */
    public static final String MODE_PARAMETER = "mode"; //$NON-NLS-1$

    /**
     * Constant to represents mode where we fill with a constant.
     */
    public static final String OTHER_COLUMN_MODE = "other_column_mode"; //$NON-NLS-1$
    public static final String CONSTANT_MODE = "constant_mode"; //$NON-NLS-1$

}
