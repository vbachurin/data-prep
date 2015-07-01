package org.talend.dataprep.preparation.store;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.Application;
import org.talend.dataprep.api.preparation.Step;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class HDFSContentCacheTest {

    private static final String PREPARATION_ID = "1234";

    private static final String STEP_ID = "5678";

    @Autowired
    FileSystem fileSystem;

    @Autowired
    HDFSContentCache cache;

    @Autowired
    ConfigurableEnvironment environment;

    @Before
    public void setUp() {
        // Ensures HDFS is activated for HDFS content cache
        MockPropertySource connectionInformation = new MockPropertySource().withProperty("hdfs.location", "file:/target");
        environment.getPropertySources().addFirst(connectionInformation);
    }

    @After
    public void tearDown() throws Exception {
        cache.clear();
    }

    @Test
    public void testHas() throws Exception {
        // Cache is empty when test starts, has() must return false for content
        assertThat(cache.has(PREPARATION_ID, STEP_ID), is(false));
    }

    @Test
    public void testPut() throws Exception {
        // Put a content in cache...
        assertThat(cache.has(PREPARATION_ID, STEP_ID), is(false));
        try (OutputStream entry = cache.put(PREPARATION_ID, STEP_ID, ContentCache.TimeToLive.DEFAULT)) {
            entry.write("content".getBytes());
            entry.flush();
        }
        // ... has() must return true
        assertThat(cache.has(PREPARATION_ID, STEP_ID), is(true));
    }

    @Test
    public void testPutOrigin() throws Exception {
        // Put a content in cache...
        assertThat(cache.has(PREPARATION_ID, "origin"), is(false));
        assertThat(cache.has(PREPARATION_ID, Step.ROOT_STEP.id()), is(false));
        try (OutputStream entry = cache.put(PREPARATION_ID, Step.ROOT_STEP.id(), ContentCache.TimeToLive.DEFAULT)) {
            entry.write("content".getBytes());
            entry.flush();
        }
        // ... has() must return true
        assertThat(cache.has(PREPARATION_ID, "origin"), is(true));
        assertThat(cache.has(PREPARATION_ID, Step.ROOT_STEP.id()), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPut_Head() throws Exception {
        // Put a content in cache with "head" is not accepted
        cache.put(PREPARATION_ID, "head", ContentCache.TimeToLive.DEFAULT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPut_Origin() throws Exception {
        // Put a content in cache with "origin" is not accepted
        cache.put(PREPARATION_ID, "origin", ContentCache.TimeToLive.DEFAULT);
    }

    @Test
    public void testGet() throws Exception {
        // Put a content in cache...
        try (OutputStream entry = cache.put(PREPARATION_ID, STEP_ID, ContentCache.TimeToLive.DEFAULT)) {
            entry.write("content".getBytes());
            entry.flush();
        }
        // ... get() should return this content back.
        final String actual = IOUtils.toString(cache.get(PREPARATION_ID, STEP_ID));
        assertThat(actual, is("content"));
    }

    @Test
    public void testEvict() throws Exception {
        // Put a content in cache...
        try (OutputStream entry = cache.put(PREPARATION_ID, STEP_ID, ContentCache.TimeToLive.DEFAULT)) {
            entry.write("content".getBytes());
            entry.flush();
        }
        assertThat(cache.has(PREPARATION_ID, STEP_ID), is(true));
        // ... evict() it...
        cache.evict(PREPARATION_ID, STEP_ID);
        // ... has() must immediately return false
        assertThat(cache.has(PREPARATION_ID, STEP_ID), is(false));
    }

    @Test
    public void testJanitor() throws Exception {
        // Put a content in cache...
        try (OutputStream entry = cache.put(PREPARATION_ID, STEP_ID, ContentCache.TimeToLive.DEFAULT)) {
            entry.write("content".getBytes());
            entry.flush();
        }
        assertThat(cache.has(PREPARATION_ID, STEP_ID), is(true));
        // ... evict() it...
        cache.evict(PREPARATION_ID, STEP_ID);
        // ... file still exists...
        final Path path = HDFSContentCache.getPath(PREPARATION_ID, STEP_ID, true, fileSystem);
        assertThat(fileSystem.exists(path), is(true));
        // ... then run a clean up...
        cache.janitor();
        // ... cache file must have disappeared
        assertThat(fileSystem.exists(path), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPath_nullPreparation() throws Exception {
        cache.has(null, STEP_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPath_nullStep() throws Exception {
        cache.has(PREPARATION_ID, null);
    }

}
