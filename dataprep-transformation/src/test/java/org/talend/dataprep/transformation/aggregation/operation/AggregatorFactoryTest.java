package org.talend.dataprep.transformation.aggregation.operation;

import org.junit.Test;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;

/**
 * Unit test for the AggregatorFactory.
 */
public class AggregatorFactoryTest {

    /** The Factory to test. */
    private AggregatorFactory factory;

    public AggregatorFactoryTest() {
        factory = new AggregatorFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldReturnEmptyAggregator() {
        AggregationParameters parameters = new AggregationParameters();
        parameters.addGroupBy("0000");

        Aggregator actual = factory.get(parameters);
    }
}