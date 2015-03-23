package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
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
import org.talend.dataprep.preparation.RootStep;
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
        preparationRepository.clear();
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
        Preparation preparation = new Preparation("1234", RootStep.INSTANCE);
        preparation.setCreationDate(0);
        preparationRepository.add(preparation);
        assertThat(when().get("/api/preparations/?format=short").asString(), sameJSONAs("[\"ae242b07084aa7b8341867a8be1707f4d52501d1\"]"));
        assertThat(when().get("/api/preparations/?format=long").asString(), sameJSONAs("[{\"dataSetId\":\"1234\",\"author\":null,\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"creationDate\":0,\"actions\":[]}]"));
    }

    @Test
    public void testPreparationGet() throws Exception {
        Preparation preparation = new Preparation("1234", RootStep.INSTANCE);
        preparation.setCreationDate(0);
        preparationRepository.add(preparation);
        assertThat(when().get("/api/preparations/{id}/details", "ae242b07084aa7b8341867a8be1707f4d52501d1").asString(), sameJSONAs("{\"dataSetId\":\"1234\",\"author\":null,\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"creationDate\":0,\"actions\":[]}"));
    }

    @Test
    public void testPreparationAppendAction() throws Exception {
        Preparation preparation = new Preparation("1234", RootStep.INSTANCE);
        preparation.setCreationDate(0);
        preparationRepository.add(preparation);
        String actionContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        given().body(actionContent).when().post("/api/preparations/{id}/actions", "ae242b07084aa7b8341867a8be1707f4d52501d1").then().statusCode(is(200));
        assertThat(preparation.getStep().id(), is("f4657f14b316033df3d2466116c9ccf682a149ba"));
    }

    @Test
    public void testPreparationInitialContent() throws Exception {
        // Create a data set
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets?name={name}", "testPreparationContentGet").asString();
        // Create a preparation based on this dataset
        Preparation preparation = new Preparation(dataSetId, RootStep.INSTANCE);
        preparation.setCreationDate(0);
        preparationRepository.add(preparation);
        // Request preparation content (content untouched since no action was done).
        assertThat(when().get("/api/preparations/{id}/content", preparation.id()).asString(), sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"John\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"David\"}]}"));
    }

    @Ignore
    @Test
    public void testPreparationContentWithActions() throws Exception {
        // Create a data set
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets?name={name}", "testPreparationContentGet").asString();
        // Create a preparation based on this dataset
        Preparation preparation = new Preparation(dataSetId, RootStep.INSTANCE);
        preparation.setCreationDate(0);
        preparationRepository.add(preparation);
        // Add action to preparation
        String actionContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        given().body(actionContent).when().post("/api/preparations/{id}/actions", preparation.id()).then().statusCode(is(200));
        // Request preparation content (content untouched since no action was done).
        assertThat(when().get("/api/preparations/{id}/content", preparation.id()).asString(), sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"John\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"David\"}]}"));
    }

}