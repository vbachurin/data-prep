package org.talend.dataprep.dataset.service.locator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSetLocation;
import org.talend.dataprep.api.dataset.location.HdfsLocation;
import org.talend.dataprep.dataset.Application;

/**
 * Unit test for the HdfsDataSetLocator.
 * 
 * @see HdfsDataSetLocator
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class HdfsDataSetLocatorTest {

    /** The dataset locator to test. */
    @Autowired
    HdfsDataSetLocator locator;

    /** Jackson builder. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Test
    public void should_accept_media_type() {
        assertTrue(locator.accept(HdfsDataSetLocator.MEDIA_TYPE));
    }

    @Test
    public void should_not_accept_media_type() {
        assertFalse(locator.accept("application/vnd.remote-ds.http"));
        assertFalse(locator.accept(""));
        assertFalse(locator.accept(null));
    }

    @Test
    public void should_parse_location() throws IOException {
        // given
        InputStream location = HdfsDataSetLocatorTest.class.getResourceAsStream("hdfs_location_ok.json");
        HdfsLocation expected = new HdfsLocation();
        expected.setUrl("hdfs://localhost:8020/path/to/file");

        // when
        DataSetLocation actual = locator.getLocation(location);

        // then
        assertEquals(expected, actual);
    }

}
