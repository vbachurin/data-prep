package org.talend.dataprep.transformation.api.action.metadata.math;

import java.math.BigDecimal;

import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;

public abstract class AbstractAbsolute extends AbstractActionMetadata {

    /**
     * Try to parse and return the absolute value of a long value as string
     * 
     * @param value The value to execute action
     * @return the absolute value or null
     */
    protected String executeOnLong(final String value) {
        try {
            long longValue = Long.parseLong(value);
            return Long.toString(Math.abs(longValue));
        } catch (NumberFormatException nfe1) {
            return null;
        }
    }

    /**
     * Try to parse and return the absolute value of a long value as string
     * 
     * @param value The value to execute action
     * @return the absolute value or null
     */
    protected String executeOnFloat(final String value) {
        try {
            BigDecimal bd = new BigDecimal(value);
            return bd.abs().toPlainString();
        } catch (NumberFormatException nfe2) {
            return null;
        }
    }

}
