package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.junit.Assert.*;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.Application;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.preparation.Preparation;
import org.talend.dataprep.preparation.store.PreparationRepository;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataPreparationAPITest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    DataSetMetadataRepository dataSetMetadataRepository;

    @Autowired
    DataSetContentStore contentStore;

    @Autowired
    PreparationRepository preparationRepository;

    @Autowired
    private List<APIService> apiServices;

    @Before
    public void setUp() {
        RestAssured.port = port;
        for (APIService apiService : apiServices) {
            apiService.setDataSetServiceURL("http://localhost:" + port + "/datasets");
            apiService.setTransformationServiceURL("http://localhost:" + port + "/");
            apiService.setPreparationServiceURL("http://localhost:" + port + "/");
        }
    }

    @org.junit.After
    public void tearDown() {
        dataSetMetadataRepository.clear();
        contentStore.clear();
    }

    @Test
    public void testCORSHeaders() throws Exception {
        when().post("/transform").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
    }

    @Test
    public void testDataSetUpdate() throws Exception {
        given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().put("/api/datasets/123456789").asString();
        String dataSetContent = when().get("/api/datasets/123456789?metadata=false&columns=false").asString();
        assertNotNull(dataSetContent);
    }

    @Test
    public void testTransformNoOp() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        String expectedContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1_expected.json"));
        String transformed = given().contentType(ContentType.JSON).body("").when().post("/api/transform/" + dataSetId).asString();
        assertEquals(expectedContent, transformed);
    }

    @Test
    public void testTransformOneAction() throws Exception {
        String actions = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test2.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        assertFalse(dataSetId.equals(StringUtils.EMPTY));
        InputStream expectedContent = DataPreparationAPITest.class.getResourceAsStream("test2_expected.json");
        String transformed = given().contentType(ContentType.JSON).body(actions).when().post("/api/transform/" + dataSetId)
                .asString();
        assertThat(transformed, sameJSONAsFile(expectedContent));
    }

    @Test
    public void testTransformTwoActions() throws Exception {
        String actions = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action2.json"));
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test3.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        InputStream expectedContent = DataPreparationAPITest.class.getResourceAsStream("test3_expected.json");
        String transformed = given().contentType(ContentType.JSON).body(actions).when().post("/api/transform/" + dataSetId)
                .asString();
        assertThat(transformed, sameJSONAsFile(expectedContent));
    }

    @Test
    public void testDataSetList() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        String list = when().get("/api/datasets").asString();
        assertTrue(list.contains(dataSetId));
    }

    @Test
    public void testDataSetDelete() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        String list = when().get("/api/datasets").asString();
        assertTrue(list.contains(dataSetId));
        when().delete("/api/datasets/" + dataSetId).asString();
        list = when().get("/api/datasets").asString();
        assertEquals("[]", list);
    }

    @Test
    public void testDataSetCreate() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets?name={name}", "tagada").asString();
        InputStream content = when().get("/api/datasets/{id}?metadata=true&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        InputStream expected = DataPreparationAPITest.class.getResourceAsStream("testCreate_expected.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void testDataSetCreateWithSpace() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets?name={name}", "Test with spaces").asString();
        DataSetMetadata metadata = dataSetMetadataRepository.get(dataSetId);
        assertEquals("Test with spaces", metadata.getName());
    }

    @Test
    public void testDataSetColumnActions() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        InputStream content = when().get("/api/datasets/{id}/{column}/actions", dataSetId, "firstname").asInputStream();
        String contentAsString = IOUtils.toString(content);
        InputStream expected = DataPreparationAPITest.class.getResourceAsStream("suggest1.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void testDataSetActions() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        InputStream content = when().get("/api/datasets/{id}/actions", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);
        assertThat(contentAsString, sameJSONAs("[]"));
    }

    @Test
    public void testEmptyPreparationList() throws Exception {
        assertThat(when().get("/api/preparations").asString(), sameJSONAs("[]"));
        assertThat(when().get("/api/preparations/?format=short").asString(), sameJSONAs("[]"));
        assertThat(when().get("/api/preparations/?format=long").asString(), sameJSONAs("[]"));
    }

    @Test
    public void testPreparationsList() throws Exception {
        Preparation preparation = new Preparation("1234");
        preparation.setCreationDate(0);
        preparationRepository.add(preparation);
        assertThat(when().get("/api/preparations/?format=short").asString(), sameJSONAs("[\"7110eda4d09e062aa5e4a390b0a572ac0d2c0220\"]"));
        assertThat(when().get("/api/preparations/?format=long").asString(), sameJSONAs("[{\"dataSetId\":\"1234\",\"author\":null,\"id\":\"7110eda4d09e062aa5e4a390b0a572ac0d2c0220\",\"creationDate\":0,\"actions\":[]}]"));
    }
}