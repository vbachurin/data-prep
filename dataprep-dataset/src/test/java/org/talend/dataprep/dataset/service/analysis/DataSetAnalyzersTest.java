package org.talend.dataprep.dataset.service.analysis;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.dataset.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataSetAnalyzersTest {

    @Autowired
    List<DataSetAnalyzer> allAnalyzers;

    @Autowired
    List<SynchronousDataSetAnalyzer> synchronousAnalyzers;

    @Autowired
    List<AsynchronousDataSetAnalyzer> asynchronousAnalyzers;

    @Test
    public void testSynchronousOrder() throws Exception {
        synchronousAnalyzers.sort((analyzer1, analyzer2) -> analyzer1.order() - analyzer2.order());
        int previousOrder = -1;
        for (SynchronousDataSetAnalyzer synchronousAnalyzer : synchronousAnalyzers) {
            assertThat(synchronousAnalyzer.order(), greaterThan(previousOrder));
        }
    }

    @Test
    public void testAsynchronousDestinations() throws Exception {
        for (AsynchronousDataSetAnalyzer asynchronousAnalyzer : asynchronousAnalyzers) {
            assertThat(asynchronousAnalyzer.destination(), notNullValue());
        }
    }

    @Test
    public void testArguments() throws Exception {
        for (DataSetAnalyzer analyzer : allAnalyzers) {
            try {
                analyzer.analyze(null);
                fail();
            } catch (IllegalArgumentException e) {
                // Expected
            }
            try {
                analyzer.analyze("");
                fail();
            } catch (IllegalArgumentException e) {
                // Expected
            }
        }
    }

}
