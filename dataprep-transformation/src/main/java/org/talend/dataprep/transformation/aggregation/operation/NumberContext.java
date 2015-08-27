package org.talend.dataprep.transformation.aggregation.operation;

import org.talend.dataprep.transformation.aggregation.api.WorkingContext;

/**
 * Simple working context that only wraps a number.
 */
public class NumberContext implements WorkingContext {

    /** The wrapped value. */
    private double value;

    /**
     * Create a Number context with the given value.
     * 
     * @param value the initialization value.
     */
    NumberContext(double value) {
        this.value = value;
    }

    /**
     * @see WorkingContext#getValue()
     */
    @Override
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set.
     */
    public void setValue(double value) {
        this.value = value;
    }
}
