package org.talend.dataprep.metrics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Aspects.class, Configuration.class })
public class VolumeAspectTests {

    @Autowired
    VolumeMeasured   volumeMeasured;

    @Autowired
    MetricRepository repository;

    @Test
    public void testNoRun() throws Exception {
        Metric<?> runTime = repository.findOne(Aspects.getCategory(VolumeMeasured.class, "run") + ".volume");
        assertNull(runTime);
    }

    @Test
    public void testNoInput() throws Exception {
        volumeMeasured.run(new ByteArrayInputStream(new byte[0]));
        Metric<?> runTime = repository.findOne(Aspects.getCategory(VolumeMeasured.class, "run") + ".volume");
        assertNotNull(runTime);
        assertThat(runTime.getValue().intValue(), is(0));
    }

    @Test
    public void testInput1() throws Exception {
        volumeMeasured.run(VolumeAspectTests.class.getResourceAsStream("data1.csv"));
        Metric<?> runTime = repository.findOne(Aspects.getCategory(VolumeMeasured.class, "run") + ".volume");
        assertNotNull(runTime);
        assertThat(runTime.getValue().intValue(), is(113));
    }

}
