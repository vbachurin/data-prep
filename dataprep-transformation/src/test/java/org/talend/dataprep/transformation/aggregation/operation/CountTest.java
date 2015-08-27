package org.talend.dataprep.transformation.aggregation.operation;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.aggregation.api.AggregationOperation;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.aggregation.api.Operator;

/**
 * Unit test for Count aggregator.
 * 
 * @see Count
 */
public class CountTest {

    /** The aggregator to test. */
    private Count aggregator;

    /**
     * Constructor.
     */
    public CountTest() {
        AggregationParameters parameters = new AggregationParameters();
        AggregationOperation operation = new AggregationOperation();
        operation.setColumnId("0001");
        operation.setOperator(Operator.COUNT);
        parameters.addOperation(operation);
        parameters.addGroupBy("0000");

        AggregatorFactory factory = new AggregatorFactory();
        aggregator = (Count) factory.get(parameters);
    }

    @Test
    public void shouldUpdateCount() {

        // given
        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "1");
        DataSetRow row = new DataSetRow(values);

        // when
        AggregationResult result = new AggregationResult(Operator.COUNT);
        aggregator.accept(row, result);

        // then
        NumberContext context = (NumberContext) result.get("toto");
        Assert.assertNotNull(context);
        Assert.assertEquals(context.getValue(), 1, 0);

        // when
        aggregator.accept(row, result);

        // then
        Assert.assertEquals(context.getValue(), 2, 0);
    }


    @Test
    public void shouldNotUpdateCountBecauseColumnNotFound() {
        Map<String, String> values = new HashMap<>();
        values.put("0000", "tata");
        values.put("0001", "1");
        DataSetRow row = new DataSetRow(values);

        AggregationResult result = new AggregationResult(Operator.COUNT);
        aggregator.accept(row, result);

        NumberContext context = (NumberContext) result.get("toto");
        Assert.assertNull(context);
    }
}