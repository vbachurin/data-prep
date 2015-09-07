package org.talend.dataprep.transformation.aggregation;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.aggregation.api.AggregationOperation;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.Operator;

/**
 * Unit test for aggregation service.
 * 
 * @see AggregationService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AggregationServiceTest extends TestCase {

    /** The service to test. */
    @Autowired
    private AggregationService service;

    @Test(expected = TDPException.class)
    public void shouldNotAggregateBecauseNoOperation() {
        service.aggregate(new AggregationParameters(), new DataSet());
    }

    @Test(expected = TDPException.class)
    public void shouldNotAggregateBecauseNoGroupBy() {
        final AggregationParameters params = new AggregationParameters();
        params.addOperation(new AggregationOperation("0001", Operator.AVERAGE));
        service.aggregate(params, new DataSet());
    }

    // aggregation will be tested at service level, see AggregationTests.class

}