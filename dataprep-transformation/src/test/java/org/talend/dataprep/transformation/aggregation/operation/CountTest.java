package org.talend.dataprep.transformation.aggregation.operation;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.aggregation.api.Operator;

/**
 * Unit test for Count aggregator.
 * 
 * @see Count
 */
public class CountTest extends OperationBaseTest {


    /**
     * Constructor.
     */
    public CountTest() {
        super(Operator.COUNT);
    }

    @Test
    public void shouldUpdateCount() {

        // given
        DataSetRow row = getRow("toto", "1");

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
        AggregationResult result = new AggregationResult(Operator.COUNT);
        aggregator.accept(getRow("tata", "1"), result);

        NumberContext context = (NumberContext) result.get("toto");
        Assert.assertNull(context);
    }
}