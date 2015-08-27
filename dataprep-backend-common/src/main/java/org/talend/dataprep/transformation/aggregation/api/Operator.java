package org.talend.dataprep.transformation.aggregation.api;

/**
 * Aggregation operator.
 */
public enum Operator {
                      COUNT,
                      MIN,
                      MAX,
                      AVERAGE,
                      SUM;

    /**
     * @return the Operator display string.
     */
    public String display() {
        return this.name().toLowerCase();
    }
}
