package org.talend.dataprep.transformation.aggregation.operation;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.aggregation.api.AggregationOperation;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.aggregation.api.Operator;

/**
 * Unit test for the Average aggregator.
 * 
 * @see Average
 */
public class AverageTest {

    /** The aggregator to test. */
    private Average aggregator;

    /**
     * Init the unit test.
     */
    public AverageTest() {
        AggregationParameters parameters = new AggregationParameters();
        AggregationOperation operation = new AggregationOperation();
        operation.setColumnId("0001");
        operation.setOperator(Operator.AVERAGE);
        parameters.addOperation(operation);
        parameters.addGroupBy("0000");

        AggregatorFactory factory = new AggregatorFactory();
        aggregator = (Average) factory.get(parameters);
    }

    @Test
    public void shouldComputeAverage() {

        AggregationResult result = new AggregationResult(Operator.AVERAGE);
        aggregator.accept(getRow("toto", "10"), result);
        aggregator.accept(getRow("toto", "2"), result);
        aggregator.accept(getRow("toto", "3.6"), result);
        aggregator.accept(getRow("toto", ""), result);
        aggregator.accept(getRow("toto", "8.2"), result);
        aggregator.accept(getRow("tata", "10"), result);
        aggregator.accept(getRow("toto", "-8"), result);
        aggregator.accept(getRow("toto", "12.3"), result);
        aggregator.accept(getRow("tata", "5"), result);

        final Average.AverageContext toto = (Average.AverageContext) result.get("toto");
        assertEquals(4.683d, toto.getValue(), 0.001d);

        final Average.AverageContext tata = (Average.AverageContext) result.get("tata");
        assertEquals(7.5d, tata.getValue(), 0);

    }

    private DataSetRow getRow(String groupBy, String value) {
        Map<String, String> values = new HashMap<>();
        values.put("0000", groupBy);
        values.put("0001", value);
        return new DataSetRow(values);
    }

}