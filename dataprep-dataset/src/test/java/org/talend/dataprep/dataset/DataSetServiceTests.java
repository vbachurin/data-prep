package org.talend.dataprep.dataset;

import com.jayway.restassured.RestAssured;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.dataset.objects.DataSetLifecycle;
import org.talend.dataprep.dataset.objects.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.talend.dataprep.dataset.objects.DataSetMetadata.Builder.id;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataSetServiceTests {

    @Value("${local.server.port}")
    public int                port;

    @Autowired
    DataSetMetadataRepository dataSetMetadataRepository;

    @Autowired
    DataSetContentStore       contentStore;

    private void assertQueueMessages(String dataSetId) throws Exception {
        Thread.sleep(1000); // TODO Ugly, need a client to lock until all operations are done
        DataSetLifecycle lifecycle = dataSetMetadataRepository.get(dataSetId).getLifecycle();
        assertThat(lifecycle.contentIndexed(), is(true));
        assertThat(lifecycle.schemaAnalyzed(), is(true));
        assertThat(lifecycle.qualityAnalyzed(), is(true));
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @org.junit.After
    public void tearDown() {
        dataSetMetadataRepository.clear();
    }

    @Test
    public void testList() throws Exception {
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[]"));
        // Adds 1 data set to store
        String id1 = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(id(id1).build());
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[\"" + id1 + "\"]"));
        // Adds a new data set to store
        String id2 = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(id(id2).build());
        when().get("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids, hasItems(id1, id2));
    }

    @Test
    public void testCreate() throws Exception {
        int before = dataSetMetadataRepository.size();
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        int after = dataSetMetadataRepository.size();
        assertThat(after - before, is(1));
        assertQueueMessages(dataSetId);
    }

    @Test
    public void testGet() throws Exception {
        String expectedId = UUID.randomUUID().toString();
        DataSetMetadata dataSetMetadata = id(expectedId).build();
        dataSetMetadataRepository.add(dataSetMetadata);
        contentStore.store(dataSetMetadata, new ByteArrayInputStream(new byte[0]));
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        when().get("/datasets/{id}", expectedId).then().statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testDelete() throws Exception {
        String expectedId = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(id(expectedId).build());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        int before = dataSetMetadataRepository.size();
        when().delete("/datasets/{id}", expectedId).then().statusCode(HttpStatus.OK.value());
        int after = dataSetMetadataRepository.size();
        assertThat(before - after, is(1));
    }

    @Test
    public void testUpdate() throws Exception {
        String dataSetId = "123456";
        when().put("/datasets/{id}", dataSetId).then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids, hasItem(dataSetId));
        assertQueueMessages(dataSetId);
    }

    @Test
    public void test1() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}", dataSetId).asInputStream();
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test1.json");
        assertNotNull(expected);
        String contentAsString = IOUtils.toString(content);
        assertThat(contentAsString, is(IOUtils.toString(expected)));
    }

    @Test
    public void test2() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada2.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}", dataSetId).asInputStream();
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test1.json");
        assertNotNull(expected);
        String contentAsString = IOUtils.toString(content);
        assertThat(contentAsString, is(IOUtils.toString(expected)));
    }
}
