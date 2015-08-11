package org.talend.dataprep.preparation.store;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
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
    public void testHasNot() throws Exception {
        // Cache is empty when test starts, has() must return false for content
        assertThat(cache.has(new ContentCacheKey(PREPARATION_ID, STEP_ID)), is(false));
    }

    @Test
    public void testPutHas() throws Exception {
        // Put a content in cache...
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, STEP_ID);
        assertThat(cache.has(key), is(false));
        addCacheEntry(key, "content");
        // ... has() must return true
        assertThat(cache.has(key), is(true));
    }

    @Test
    public void testHasWhateverSample() throws Exception {
        // Put a content in cache...
        ContentCacheKey key1 = new ContentCacheKey(PREPARATION_ID, STEP_ID, 45l);
        addCacheEntry(key1, "content with 45 lines");

        ContentCacheKey key2 = new ContentCacheKey(PREPARATION_ID, STEP_ID, 42l);
        addCacheEntry(key2, "content with 42 lines");

        ContentCacheKey simpleKey = new ContentCacheKey(PREPARATION_ID, STEP_ID);

        // ... has() must return true
        assertThat(cache.hasAny(simpleKey), is(true));
    }

    @Test
    public void testPutWithSampleSize() throws Exception {
        // Put a content in cache...
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, STEP_ID, 25l);
        assertThat(cache.has(key), is(false));
        addCacheEntry(key, "content with sample");
        // ... has() must return true
        assertThat(cache.has(key), is(true));
    }

    @Test
    public void testPutOrigin() throws Exception {
        ContentCacheKey origin = new ContentCacheKey(PREPARATION_ID, "origin");
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, Step.ROOT_STEP.id());
        // Put a content in cache...
        assertThat(cache.has(origin), is(false));
        assertThat(cache.has(key), is(false));
        addCacheEntry(key, "content");
        // ... has() must return true
        assertThat(cache.has(origin), is(true));
        assertThat(cache.has(key), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPut_Head() throws Exception {
        // Put a content in cache with "head" is not accepted
        cache.put(new ContentCacheKey(PREPARATION_ID, "head"), ContentCache.TimeToLive.DEFAULT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPut_Origin() throws Exception {
        // Put a content in cache with "origin" is not accepted
        cache.put(new ContentCacheKey(PREPARATION_ID, "origin"), ContentCache.TimeToLive.DEFAULT);
    }

    @Test
    public void testGet() throws Exception {
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, STEP_ID);
        String content = "content";
        // Put a content in cache...
        addCacheEntry(key, content);
        // ... get() should return this content back.
        final String actual = IOUtils.toString(cache.get(key));
        assertThat(actual, is(content));
    }

    @Test
    public void testGetWithSampleSize() throws Exception {
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, STEP_ID, 72l);
        String content = "content limited to 72 lines";
        // Put a content in cache...
        addCacheEntry(key, content);
        // ... get() should return this content back.
        final String actual = IOUtils.toString(cache.get(key));
        assertThat(actual, is(content));
    }

    @Test
    public void testEvict() throws Exception {
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, STEP_ID);
        // Put a content in cache...
        addCacheEntry(key, "content");
        assertThat(cache.has(key), is(true));
        // ... evict() it...
        cache.evict(key);
        // ... has() must immediately return false
        assertThat(cache.has(key), is(false));
    }

    @Test
    public void testEvictWithSample() throws Exception {
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, STEP_ID, 55l);
        // Put a content in cache...
        addCacheEntry(key, "content limited to the first 55 lines");
        assertThat(cache.has(key), is(true));
        // ... evict() it...
        cache.evict(key);
        // ... has() must immediately return false
        assertThat(cache.has(key), is(false));
    }

    @Test
    public void testEvictWhateverSample() throws Exception {

        // given
        ContentCacheKey key1 = new ContentCacheKey(PREPARATION_ID, STEP_ID, 155l);
        addCacheEntry(key1, "155 lines");
        assertThat(cache.has(key1), is(true));

        ContentCacheKey key2 = new ContentCacheKey(PREPARATION_ID, STEP_ID, 178l);
        addCacheEntry(key2, "178 lines");
        assertThat(cache.has(key2), is(true));

        ContentCacheKey key3 = new ContentCacheKey(PREPARATION_ID, STEP_ID);
        addCacheEntry(key3, "full lines");
        assertThat(cache.has(key3), is(true));

        // when
        cache.evictAllEntries(key1);

        // then
        assertThat(cache.has(key1), is(false));
        assertThat(cache.has(key2), is(false));
        assertThat(cache.has(key3), is(false));

        // bonus test :-)
        assertThat(cache.hasAny(key3), is(false));
    }

    @Test
    public void testJanitor() throws Exception {
        ContentCacheKey key = new ContentCacheKey(PREPARATION_ID, STEP_ID);
        // Put a content in cache...
        addCacheEntry(key, "content");
        assertThat(cache.has(key), is(true));
        // ... evict() it...
        cache.evict(key);
        // ... file still exists...
        final Path path = HDFSContentCache.getPath(key, true, fileSystem);
        assertThat(fileSystem.exists(path), is(true));
        // ... then run a clean up...
        cache.janitor();
        // ... cache file must have disappeared
        assertThat(fileSystem.exists(path), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPath_nullPreparation() throws Exception {
        cache.has(new ContentCacheKey(null, STEP_ID));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPath_nullStep() throws Exception {
        cache.has(new ContentCacheKey(PREPARATION_ID, null));
    }

    /**
     * Add the cache entry.
     *
     * @param key where to put the cache entry.
     * @param content the cache entry content.
     * @throws IOException if an error occurs.
     */
    private void addCacheEntry(ContentCacheKey key, String content) throws IOException {
        try (OutputStream entry = cache.put(key, ContentCache.TimeToLive.DEFAULT)) {
            entry.write(content.getBytes());
            entry.flush();
        }
    }
}
