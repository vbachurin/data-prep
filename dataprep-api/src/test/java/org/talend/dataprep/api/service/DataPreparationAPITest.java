package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.Application;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.preparation.PreparationRepository;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataPreparationAPITest {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(DataPreparationAPITest.class);

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
            apiService.setTransformationServiceURL("http://localhost:" + port);
            apiService.setPreparationServiceURL("http://localhost:" + port);
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

        // TODO temp log to see what's going in newbuild
        LOG.error("testTransformOneAction start");

        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test2.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();

        assertNotNull(dataSetId);
        assertFalse(dataSetId.equals(StringUtils.EMPTY));

        // TODO temp log to see what's going in newbuild
        LOG.error("dataset id created id " + dataSetId);

        InputStream expectedContent = DataPreparationAPITest.class.getResourceAsStream("test2_expected.json");
        String actions = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        String transformed = given().contentType(ContentType.JSON).body(actions).when().post("/api/transform/" + dataSetId)
                .asString();

        // TODO temp log to see what's going in newbuild
        LOG.error("transformed is " + transformed);

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

    /**
     * Simple dataset deletion case.
     */
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

    /**
     * DataSet deletion test case when the dataset is used by a preparation.
     */
    @Test
    public void testDataSetDeleteWhenUsedByPreparation() throws Exception {

        // create a dataset
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();

        // create a preparation that uses the previous dataset
        String preparationId = given().contentType(ContentType.JSON).body("{ \"dataSetId\": \"" + dataSetId + "\" }")
                .post("/api/preparations").asString();

        // deletion should fail
        when().delete("/api/datasets/" + dataSetId).then().log().ifValidationFails().assertThat().statusCode(400);

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
        // Create a preparation based on dataset "1234"
        given().contentType(ContentType.JSON).body("{ \"dataSetId\": \"1234\" }").post("/api/preparations").asString();
        // Test short format
        JsonPath shortFormat = when().get("/api/preparations/?format=short").jsonPath();
        List<String> values = shortFormat.getList("");
        assertThat(values.get(0), is("948bed0012a5f13cd1ab93d51992f8952cbbd03b"));
        // Test long format
        JsonPath longFormat = when().get("/api/preparations/?format=long").jsonPath();
        assertThat(longFormat.getList("dataSetId").size(), is(1));
        assertThat(longFormat.getList("dataSetId").get(0), is("1234"));
        assertThat(longFormat.getList("author").size(), is(1));
        assertThat(longFormat.getList("author").get(0), is("anonymousUser"));
        assertThat(longFormat.getList("id").size(), is(1));
        assertThat(longFormat.getList("id").get(0), is("948bed0012a5f13cd1ab93d51992f8952cbbd03b"));
        assertThat(longFormat.getList("actions").size(), is(1));
        assertThat(((List) longFormat.getList("actions").get(0)).size(), is(0));
    }

    @Test
    public void testPreparationUpdate() throws Exception {
        // Create a preparation based on dataset "1234"
        String preparationId = given().contentType(ContentType.JSON)
                .body("{ \"name\": \"original_name\", \"dataSetId\": \"1234\" }").post("/api/preparations").asString();
        // Assert on creation name
        JsonPath longFormat = when().get("/api/preparations/?format=long").jsonPath();
        assertThat(longFormat.getList("name").size(), is(1));
        assertThat(longFormat.getList("name").get(0), is("original_name"));
        assertThat(longFormat.getList("id").size(), is(1));
        assertThat(longFormat.getList("id").get(0), is(preparationId));
        // Update name
        given().contentType(ContentType.JSON).body("{ \"name\": \"updated_name\", \"dataSetId\": \"1234\" }")
                .put("/api/preparations/{id}", preparationId).asString();
        longFormat = when().get("/api/preparations/?format=long").jsonPath();
        assertThat(longFormat.getList("name").size(), is(1));
        assertThat(longFormat.getList("name").get(0), is("updated_name"));
    }

    @Test
    public void testPreparationDelete() throws Exception {
        String preparationId = given().contentType(ContentType.JSON)
                .body("{ \"name\": \"original_name\", \"dataSetId\": \"1234\" }").post("/api/preparations").asString();
        assertNotNull(preparationId);
        String list = when().get("/api/preparations").asString();
        assertTrue(list.contains(preparationId));
        when().delete("/api/preparations/" + preparationId).asString();
        list = when().get("/api/preparations").asString();
        assertEquals("[]", list);
    }

    @Test
    public void testPreparationGet() throws Exception {
        // Create a preparation based on dataset "1234"
        String preparationId = given().contentType(ContentType.JSON).body("{ \"dataSetId\": \"1234\" }")
                .post("/api/preparations").asString();
        JsonPath longFormat = given().get("/api/preparations/{id}/details", preparationId).jsonPath();
        assertThat(longFormat.getString("dataSetId"), is("1234"));
        assertThat(longFormat.getString("author"), is("anonymousUser"));
        assertThat(longFormat.getString("id"), is("948bed0012a5f13cd1ab93d51992f8952cbbd03b"));
        assertThat(longFormat.getList("actions").size(), is(0));
    }

    @Test
    public void testPreparationAppendAction() throws Exception {
        // Create a preparation based on dataset "1234"
        String preparationId = given().contentType(ContentType.JSON).body("{ \"dataSetId\": \"1234\" }")
                .post("/api/preparations").asString();
        String actionContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        given().contentType(ContentType.JSON).body(actionContent).when().post("/api/preparations/{id}/actions", preparationId)
                .then().statusCode(is(200));
        // Assert preparation step is updated
        List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(2));
        assertThat(steps.get(0), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
        assertThat(steps.get(1), is(ROOT_STEP.id()));
    }

    @Test
    public void testPreparationUpdateAction() throws Exception {
        // Create a preparation based on dataset "1234"
        String preparationId = given().contentType(ContentType.JSON).body("{ \"dataSetId\": \"1234\" }")
                .post("/api/preparations").asString();
        String actionContent1 = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("upper_case_1.json"));
        given().contentType(ContentType.JSON).body(actionContent1).when().post("/api/preparations/{id}/actions", preparationId)
                .then().statusCode(is(200));
        String actionContent2 = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("upper_case_2.json"));
        given().body(actionContent2).when().post("/api/preparations/{id}/actions", preparationId).then().statusCode(is(200));
        // Assert on current actions (before update)
        List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(3));
        assertThat(steps.get(0), is("4115f6d965e146ddbff622633895277c96754541")); // <- upper_case_2
        assertThat(steps.get(1), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59")); // <- upper_case_1
        assertThat(steps.get(2), is(ROOT_STEP.id()));
        // Update first action (upper_case_1 / "2b6ae58738239819df3d8c4063e7cb56f53c0d59") with another action
        // (lower_case_1)
        String actionContent3 = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("lower_case_1.json"));
        given().body(actionContent3)
                .put("/api/preparations/{preparation}/actions/{action}", preparationId,
                        "2b6ae58738239819df3d8c4063e7cb56f53c0d59").then().statusCode(is(200));
        // Steps id should have changed due to update
        steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(3));
        assertThat(steps.get(0), is("b96bf024a0265376ffaedcc974b8b66b3b2c7f64"));
        assertThat(steps.get(1), is("1af242477273e0dae4bb3d32cc524b61744c7895"));
        assertThat(steps.get(2), is(ROOT_STEP.id()));
    }

    @Test
    public void testPreparationInitialContent() throws Exception {
        // Create a data set
        String body = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv"));
        String dataSetId = given().contentType(ContentType.JSON).body(body).queryParam("Content-Type", "text/csv").when()
                .post("/api/datasets?name={name}", "testPreparationContentGet").asString();
        // Create a preparation based on this dataset
        String preparationId = given().contentType(ContentType.JSON).body("{ \"dataSetId\": \"" + dataSetId + "\"}").when()
                .post("/api/preparations").asString();
        // Request preparation content (content untouched since no action was done).
        InputStream expected = DataPreparationAPITest.class.getResourceAsStream("testCreate_initial.json");
        assertThat(when().get("/api/preparations/{id}/content", preparationId).asString(), sameJSONAsFile(expected));
    }

    @Test
    public void testPreparationContentWithActions() throws Exception {
        // Create a data set
        String dataSetContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv"));
        String dataSetId = given().body(dataSetContent).queryParam("Content-Type", "text/csv").when()
                .post("/api/datasets?name={name}", "testPreparationContentGet").asString();
        // Create a preparation based on this dataset
        String preparationId = given().contentType(ContentType.JSON).body("{ \"dataSetId\": \"" + dataSetId + "\"}").when()
                .post("/api/preparations").asString();
        assertThat(preparationId, notNullValue());
        assertThat(preparationId, not(""));
        List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0), is(ROOT_STEP.id()));
        // Add action to preparation
        String actionContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        given().contentType(ContentType.JSON).body(actionContent).when().post("/api/preparations/{id}/actions", preparationId)
                .then().statusCode(is(200));
        steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(2));
        assertThat(steps.get(1), is(ROOT_STEP.id()));
        // Request preparation content at different versions (preparation has 2 actions -> Root + Upper Case).
        assertThat(when().get("/api/preparations/{id}/content", preparationId).asString(),
                sameJSONAsFile(DataPreparationAPITest.class.getResourceAsStream("testCreate_upper.json")));
        assertThat(when().get("/api/preparations/{id}/content?version=head", preparationId).asString(),
                sameJSONAsFile(DataPreparationAPITest.class.getResourceAsStream("testCreate_upper.json")));
        assertThat(when().get("/api/preparations/{id}/content?version=" + steps.get(0), preparationId).asString(),
                sameJSONAsFile(DataPreparationAPITest.class.getResourceAsStream("testCreate_upper.json")));
        assertThat(when().get("/api/preparations/{id}/content?version=" + steps.get(1), preparationId).asString(),
                sameJSONAsFile(DataPreparationAPITest.class.getResourceAsStream("testCreate_initial.json")));
        assertThat(when().get("/api/preparations/{id}/content?version=origin", preparationId).asString(),
                sameJSONAsFile(DataPreparationAPITest.class.getResourceAsStream("testCreate_initial.json")));
        assertThat(when().get("/api/preparations/{id}/content?version=" + ROOT_STEP.id(), preparationId).asString(),
                sameJSONAsFile(DataPreparationAPITest.class.getResourceAsStream("testCreate_initial.json")));
    }

}