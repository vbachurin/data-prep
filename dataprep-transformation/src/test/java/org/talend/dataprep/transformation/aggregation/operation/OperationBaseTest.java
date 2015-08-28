package org.talend.dataprep.transformation.aggregation.operation;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.aggregation.api.AggregationOperation;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.Operator;

/**
 * Base class for aggregation operation tests used to factorize code and make unit tests easier.
 */
public abstract class OperationBaseTest {

    /** The aggregator to test. */
    protected Aggregator aggregator;

    /**
     * Constructor.
     */
    public OperationBaseTest(Operator operator) {
        AggregationParameters parameters = new AggregationParameters();
        AggregationOperation operation = new AggregationOperation();
        operation.setColumnId("0001");
        operation.setOperator(operator);
        parameters.addOperation(operation);
        parameters.addGroupBy("0000");

        AggregatorFactory factory = new AggregatorFactory();
        aggregator = factory.get(parameters);
    }

    protected DataSetRow getRow(String groupBy, String value) {
        Map<String, String> values = new HashMap<>();
        values.put("0000", groupBy);
        values.put("0001", value);
        return new DataSetRow(values);
    }
}
