package org.talend.dataprep.transformation.aggregation.api;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaBean used to model aggregation parameters.
 */
public class AggregationParameters {

    /** The dataset id. */
    private String datasetId;

    /** The preparation id. */
    private String preparationId;

    /** The Step id, if null, 'head' is used. */
    private String stepId;

    /** List of column ids to group by. */
    private List<String> groupBy;

    /** List of aggregation operations to apply. */
    private List<AggregationOperation> operations;

    /** Optional sample size (null for the whole thing). */
    private Long sampleSize;

    /**
     * Default empty constructor.
     */
    public AggregationParameters() {
        groupBy = new ArrayList<>();
        operations = new ArrayList<>();
    }

    /**
     * @return the DatasetId
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @param datasetId the datasetId to set.
     */
    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    /**
     * @return the PreparationId
     */
    public String getPreparationId() {
        return preparationId;
    }

    /**
     * @param preparationId the preparationId to set.
     */
    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }

    /**
     * @return the StepId
     */
    public String getStepId() {
        return stepId;
    }

    /**
     * @param stepId the stepId to set.
     */
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    /**
     * @return the GroupBy
     */
    public List<String> getGroupBy() {
        return groupBy;
    }

    /**
     * @param groupBy the groupBy to set.
     */
    public void setGroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * Add the given group by.
     * 
     * @param groupBy the group by to add.
     */
    public void addGroupBy(String groupBy) {
        this.groupBy.add(groupBy);
    }

    /**
     * @return the Operations
     */
    public List<AggregationOperation> getOperations() {
        return operations;
    }

    /**
     * Add the given operation to the parameters.
     * 
     * @param operation the operatio to add.
     */
    public void addOperation(AggregationOperation operation) {
        this.operations.add(operation);
    }

    /**
     * @param operations the operations to set.
     */
    public void setOperations(List<AggregationOperation> operations) {
        this.operations = operations;
    }

    /**
     * @return the SampleSize
     */
    public Long getSampleSize() {
        return sampleSize;
    }

    /**
     * @param sampleSize the sampleSize to set.
     */
    public void setSampleSize(Long sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AggregationParameters{" + "datasetId='" + datasetId + '\'' + ", preparationId='" + preparationId + '\''
                + ", stepId='" + stepId + '\'' + ", groupBy=" + groupBy + ", operations=" + operations + ", sampleSize="
                + sampleSize + '}';
    }


}
