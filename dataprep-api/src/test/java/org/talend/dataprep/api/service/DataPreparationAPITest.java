package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.talend.dataprep.preparation.Step.ROOT_STEP;
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
import org.talend.dataprep.preparation.PreparationRepository;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

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
        // Create a preparation based on dataset "1234"
        given().body("1234").post("/api/preparations").asString();
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
    public void testPreparationGet() throws Exception {
        // Create a preparation based on dataset "1234"
        String preparationId = given().body("1234").post("/api/preparations").asString();
        JsonPath longFormat = when().get("/api/preparations/{id}/details", preparationId).jsonPath();
        assertThat(longFormat.getString("dataSetId"), is("1234"));
        assertThat(longFormat.getString("author"), is("anonymousUser"));
        assertThat(longFormat.getString("id"), is("948bed0012a5f13cd1ab93d51992f8952cbbd03b"));
        assertThat(longFormat.getList("actions").size(), is(0));
    }

    @Test
    public void testPreparationAppendAction() throws Exception {
        // Create a preparation based on dataset "1234"
        String preparationId = given().body("1234").post("/api/preparations").asString();
        String actionContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        given().body(actionContent).when().post("/api/preparations/{id}/actions", preparationId).then().statusCode(is(200));
        // Assert preparation step is updated
        List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(2));
        assertThat(steps.get(0), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
        assertThat(steps.get(1), is(ROOT_STEP.id()));
    }

    @Test
    public void testPreparationInitialContent() throws Exception {
        // Create a data set
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets?name={name}", "testPreparationContentGet")
                .asString();
        // Create a preparation based on this dataset
        String preparationId = given().body(dataSetId).when().body(dataSetId).post("/api/preparations").asString();
        // Request preparation content (content untouched since no action was done).
        assertThat(
                when().get("/api/preparations/{id}/content", preparationId).asString(),
                sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"John\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"David\"}]}"));
    }

    @Test
    public void testPreparationContentWithActions() throws Exception {
        // Create a data set
        String dataSetContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv"));
        String dataSetId = given().body(dataSetContent).queryParam("Content-Type", "text/csv").when()
                .post("/api/datasets?name={name}", "testPreparationContentGet").asString();
        // Create a preparation based on this dataset
        String preparationId = given().body(dataSetId).when().body(dataSetId).post("/api/preparations").asString();
        assertThat(preparationId, notNullValue());
        assertThat(preparationId, not(""));
        List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0), is(ROOT_STEP.id()));
        // Add action to preparation
        String actionContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        given().body(actionContent).when().post("/api/preparations/{id}/actions", preparationId).then().statusCode(is(200));
        steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(2));
        assertThat(steps.get(1), is(ROOT_STEP.id()));
        // Request preparation content at different versions (preparation has 2 actions -> Root + Upper Case).
        assertThat(
                when().get("/api/preparations/{id}/content", preparationId).asString(),
                sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"JOHN\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"DAVID\"}]}"));
        assertThat(
                when().get("/api/preparations/{id}/content?version=head", preparationId).asString(),
                sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"JOHN\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"DAVID\"}]}"));
        assertThat(
                when().get("/api/preparations/{id}/content?version=" + steps.get(0), preparationId).asString(),
                sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"JOHN\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"DAVID\"}]}"));
        assertThat(
                when().get("/api/preparations/{id}/content?version=" + steps.get(1), preparationId).asString(),
                sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"John\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"David\"}]}"));
        assertThat(
                when().get("/api/preparations/{id}/content?version=origin", preparationId).asString(),
                sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"John\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"David\"}]}"));
        assertThat(
                when().get("/api/preparations/{id}/content?version=" + ROOT_STEP.id(), preparationId).asString(),
                sameJSONAs("{\"records\":[{\"firstname\":\"Lennon\",\"alive\":\"false\",\"date-of-birth\":\"10/09/1940\",\"id\":\"1\",\"age\":\"40\",\"lastname\":\"John\"},{\"firstname\":\"Bowie\",\"alive\":\"true\",\"date-of-birth\":\"01/08/1947\",\"id\":\"2\",\"age\":\"67\",\"lastname\":\"David\"}]}"));
    }

}