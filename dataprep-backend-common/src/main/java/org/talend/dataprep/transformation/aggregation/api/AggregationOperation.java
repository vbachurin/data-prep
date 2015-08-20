package org.talend.dataprep.transformation.aggregation.api;

/**
 * An aggregation operation specify an operator to be applied on a column.
 */
public class AggregationOperation {

    /** The operation column. */
    private String columnId;

    /** The operation operator. */
    private Operator operator;

    /**
     * @return the ColumnId
     */
    public String getColumnId() {
        return columnId;
    }

    /**
     * @param columnId the columnId to set.
     */
    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    /**
     * @return the Operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set.
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AggregationOperation{" + "columnId='" + columnId + '\'' + ", operator=" + operator + '}';
    }
}
