//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.api.dataset.location.locator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.dataprep.api.dataset.DataSetLocation;
import org.talend.dataprep.api.dataset.location.HdfsLocation;

/**
 * Unit test for the HdfsDataSetLocator.
 * 
 * @see HdfsDataSetLocator
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HdfsDataSetLocatorTest.class)
@ComponentScan(basePackages = "org.talend.dataprep")
public class HdfsDataSetLocatorTest {

    /** The dataset locator to test. */
    @Autowired
    HdfsDataSetLocator locator;

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
