package org.talend.dataprep.metrics;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Aspects.class, Configuration.class })
public class TimedAspectTests {

    private static final int PRECISION = 50;

    @Autowired
    TimeMeasured             timeMeasured;

    @Autowired
    MetricRepository         repository;

    @Test
    public void testNoRun() throws Exception {
        Metric<?> runTime = repository.findOne(Aspects.getCategory(TimeMeasured.class, "run") + ".time");
        assertNull(runTime);
    }

    @Test
    public void testElapsedTime() throws Exception {
        // No delay
        timeMeasured.setDelay(0);
        timeMeasured.run();
        Metric<?> runTime = repository.findOne(Aspects.getCategory(TimeMeasured.class, "run") + ".time");
        assertNotNull(runTime);
        assertThat(runTime.getValue().intValue(), Matchers.lessThan(PRECISION));
        // Set delay = 1s
        timeMeasured.setDelay(1000);
        timeMeasured.run();
        runTime = repository.findOne(Aspects.getCategory(TimeMeasured.class, "run") + ".time");
        assertNotNull(runTime);
        assertThat(runTime.getValue().intValue(), Matchers.lessThan(1000 + PRECISION));
    }

    @Test
    public void testElapsedTimeOnError() throws Exception {
        timeMeasured.setError(true);
        // No delay
        timeMeasured.setDelay(0);
        try {
            timeMeasured.run();
            Assert.fail("Expected a failure since setError(true) was called.");
        } catch (Error e) {
            // Expected;
        }
        Metric<?> runTime = repository.findOne(Aspects.getCategory(TimeMeasured.class, "run") + ".time");
        assertNotNull(runTime);
        assertThat(runTime.getValue().intValue(), Matchers.lessThan(PRECISION));
        // Set delay = 1s
        timeMeasured.setDelay(1000);
        try {
            timeMeasured.run();
            Assert.fail("Expected a failure since setError(true) was called.");
        } catch (Error e) {
            // Expected
        }
        runTime = repository.findOne(Aspects.getCategory(TimeMeasured.class, "run") + ".time");
        assertNotNull(runTime);
        assertThat(runTime.getValue().intValue(), Matchers.lessThan(1000 + PRECISION));
    }
}
