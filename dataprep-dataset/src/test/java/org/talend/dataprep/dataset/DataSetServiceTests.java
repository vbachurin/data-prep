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
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetLifecycle;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.path.json.JsonPath.from;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.talend.dataprep.api.DataSetMetadata.Builder.id;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataSetServiceTests {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    DataSetMetadataRepository dataSetMetadataRepository;

    @Autowired
    DataSetContentStore contentStore;

    private void assertQueueMessages(String dataSetId) throws Exception {
        Thread.sleep(1000); // TODO Ugly, need a client to lock until all operations are done
        DataSetMetadata metadata = dataSetMetadataRepository.get(dataSetId);
        DataSetLifecycle lifecycle = metadata.getLifecycle();
        assertThat(lifecycle.contentIndexed(), is(true));
        assertThat(lifecycle.schemaAnalyzed(), is(true));
        assertThat(lifecycle.qualityAnalyzed(), is(true));
        // Quality number assertions (TODO: temporary, values to be provided by actual analysis)
        // Test condition empty < invalid < valid
        List<ColumnMetadata> columns = metadata.getRow().getColumns();
        for (ColumnMetadata column : columns) {
            int valid = column.getQuality().getValid();
            int invalid = column.getQuality().getInvalid();
            int empty = column.getQuality().getEmpty();
            assertTrue(empty < invalid);
            assertTrue(invalid < valid);
        }
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
        dataSetMetadataRepository.clear();
        contentStore.clear();
    }

    @org.junit.After
    public void tearDown() {
        dataSetMetadataRepository.clear();
        contentStore.clear();
    }

    @Test
    public void testCORSHeaders() throws Exception {
        when().get("/datasets").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with");
    }

    @Test
    public void testList() throws Exception {
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[]"));
        // Adds 1 data set to store
        String id1 = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(id(id1).name("name1").author("anonymous").created(new Date(0)).build());
        when().get("/datasets")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("[{\"id\":\"" + id1
                        + "\",\"name\":\"name1\",\"author\":\"anonymous\",\"created\":\"01-01-1970 00:00\"}]"));
        // Adds a new data set to store
        String id2 = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(id(id2).name("name2").author("anonymous").created(new Date(0)).build());
        when().get("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("id");
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
        contentStore.storeAsRaw(dataSetMetadata, new ByteArrayInputStream(new byte[0]));
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        when().get("/datasets/{id}/content", expectedId).then().statusCode(HttpStatus.OK.value());
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
        when().put("/datasets/{id}/raw", dataSetId).then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("id");
        assertThat(ids, hasItem(dataSetId));
        assertQueueMessages(dataSetId);
    }

    @Test
    public void test1() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
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
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test1.json");
        assertNotNull(expected);
        String contentAsString = IOUtils.toString(content);
        assertThat(contentAsString, is(IOUtils.toString(expected)));
    }

    @Test
    public void test3() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        // Update content
        given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada3.csv")))
                .queryParam("Content-Type", "text/csv").when().put("/datasets/" + dataSetId + "/raw");
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test2.json");
        assertNotNull(expected);
        String contentAsString = IOUtils.toString(content);
        assertThat(contentAsString, is(IOUtils.toString(expected)));
        // Update name
        String expectedName = "testOfADataSetName";
        given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada3.csv")))
                .queryParam("Content-Type", "text/csv").when().put("/datasets/" + dataSetId + "/raw?name=" + expectedName);
        assertThat(dataSetMetadataRepository.get(dataSetId).getName(), is(expectedName));
    }

}
