package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;
import static org.talend.dataprep.api.type.ExportType.CSV;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.Application;
import org.talend.dataprep.api.dataset.DataSetGovernance.Certification;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.preparation.PreparationRepository;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
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
    ConfigurableEnvironment environment;

    @Before
    public void setUp() {
        RestAssured.port = port;

        // Overrides connection information with random port value
        MockPropertySource connectionInformation = new MockPropertySource()
                .withProperty("dataset.service.url", "http://localhost:" + port)
                .withProperty("transformation.service.url", "http://localhost:" + port)
                .withProperty("preparation.service.url", "http://localhost:" + port);
        environment.getPropertySources().addFirst(connectionInformation);
    }

    @BeforeClass
    public static void startBootApp() {

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
        // given
        final String dataSetId = createDataset("test1.csv", "testDataset", "text/csv");
        final String expectedContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1_expected.json"));

        // when
        final String transformed = given().contentType(ContentType.JSON).body("").when().post("/api/transform/" + dataSetId)
                .asString();

        // then
        assertThat(expectedContent, sameJSONAs(transformed));
    }

    @Test
    public void testTransformOneAction() throws Exception {
        // given
        final String dataSetId = createDataset("test2.csv", "testDataset", "text/csv");

        final InputStream expectedContent = DataPreparationAPITest.class.getResourceAsStream("test2_expected.json");
        final String actions = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));

        // when
        final String transformed = given().contentType(ContentType.JSON).body(actions).when().post("/api/transform/" + dataSetId)
                .asString();

        // then
        assertThat(transformed, sameJSONAsFile(expectedContent));
    }

    @Test
    public void testTransformTwoActions() throws Exception {
        // given
        final String dataSetId = createDataset("test3.csv", "testDataset", "text/csv");
        final String actions = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action2.json"));
        final InputStream expectedContent = DataPreparationAPITest.class.getResourceAsStream("test3_expected.json");

        // when
        final String transformed = given().contentType(ContentType.JSON).body(actions).when().post("/api/transform/" + dataSetId)
                .asString();

        // then
        assertThat(transformed, sameJSONAsFile(expectedContent));
    }

    @Test
    public void testDataSetList() throws Exception {
        // given
        final String dataSetId = createDataset("test1.csv", "testDataset", "text/csv");

        // when
        final String list = when().get("/api/datasets").asString();

        // then
        assertTrue(list.contains(dataSetId));
    }

    /**
     * Simple dataset deletion case.
     */
    @Test
    public void testDataSetDelete() throws Exception {
        // given
        final String dataSetId = createDataset("test1.csv", "testDataset", "text/csv");

        final String list = when().get("/api/datasets").asString();
        assertTrue(list.contains(dataSetId));

        // when
        when().delete("/api/datasets/" + dataSetId).asString();
        final String updatedList = when().get("/api/datasets").asString();

        // then
        assertEquals("[]", updatedList);
    }

    /**
     * DataSet deletion test case when the dataset is used by a preparation.
     */
    @Test
    public void testDataSetDeleteWhenUsedByPreparation() throws Exception {
        // given
        final String dataSetId = createDataset("test1.csv", "testDataset", "text/csv");
        createPreparationFromDataset(dataSetId, "testPreparation");

        // when/then
        when().delete("/api/datasets/" + dataSetId).then().log().ifValidationFails().assertThat().statusCode(400);
    }

    @Test
    public void testDataSetCreate() throws Exception {
        // given
        final String dataSetId = createDataset("testCreate.csv", "tagada", "text/csv");
        final InputStream expected = DataPreparationAPITest.class.getResourceAsStream("testCreate_expected.json");

        // when
        final String contentAsString = when().get("/api/datasets/{id}?metadata=true&columns=false", dataSetId).asString();

        // then
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void testDataSetCreateWithSpace() throws Exception {
        // given
        String dataSetId = createDataset("testCreate.csv", "Test with spaces", "text/csv");

        // when
        final DataSetMetadata metadata = dataSetMetadataRepository.get(dataSetId);

        // then
        assertEquals("Test with spaces", metadata.getName());
    }

    @Test
    public void testDataSetColumnActions() throws Exception {
        // given
        String columnDescription = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("first_name_metadata.json"));

        // when
        String content = given().body(columnDescription).when().post("/api/transform/suggest/column").asString();

        // then
        InputStream expected = DataPreparationAPITest.class.getResourceAsStream("suggest1.json");
        assertThat(content, sameJSONAsFile(expected));
    }

    @Test
    public void testDataSetActions() throws Exception {
        // given
        final String dataSetId = createDataset("testCreate.csv", "testDataset", "text/csv");

        // when
        final String contentAsString = when().get("/api/datasets/{id}/actions", dataSetId).asString();

        // then
        assertThat(contentAsString, sameJSONAs("[]"));
    }

    @Test
    public void testAskCertification() throws Exception {
        // given
        final String dataSetId = createDataset("testCreate.csv", "tagada", "text/csv");
        // TODO remove this when a better solution is available
        Thread.sleep(200);

        DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        int originalNbLines = dataSetMetadata.getContent().getNbRecords(); // to check later if no modified
        assertEquals(Certification.NONE, dataSetMetadata.getGovernance().getCertificationStep());

        // when
        when().put("/api/datasets/{id}/processcertification", dataSetId).then().statusCode(HttpStatus.OK.value());

        // then
        dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        assertEquals(Certification.PENDING, dataSetMetadata.getGovernance().getCertificationStep());
        assertEquals(originalNbLines, dataSetMetadata.getContent().getNbRecords());

        // when
        when().put("/api/datasets/{id}/processcertification", dataSetId).then().statusCode(HttpStatus.OK.value());

        // then
        dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        assertEquals(Certification.CERTIFIED, dataSetMetadata.getGovernance().getCertificationStep());
        assertEquals(originalNbLines, dataSetMetadata.getContent().getNbRecords());
    }

    @Test
    public void testEmptyPreparationList() throws Exception {
        assertThat(when().get("/api/preparations").asString(), sameJSONAs("[]"));
        assertThat(when().get("/api/preparations/?format=short").asString(), sameJSONAs("[]"));
        assertThat(when().get("/api/preparations/?format=long").asString(), sameJSONAs("[]"));
    }

    @Test
    public void testPreparationsList() throws Exception {
        // given
        createPreparationFromDataset("1234", "testPreparation");

        // when : short format
        final JsonPath shortFormat = when().get("/api/preparations/?format=short").jsonPath();

        // then
        final List<String> values = shortFormat.getList("");
        assertThat(values.get(0), is("6726763ed6f12386064d41d61ff6580f1cfabc2d"));

        // when : long format
        final JsonPath longFormat = when().get("/api/preparations/?format=long").jsonPath();

        // then
        assertThat(longFormat.getList("dataSetId").size(), is(1));
        assertThat(longFormat.getList("dataSetId").get(0), is("1234"));
        assertThat(longFormat.getList("author").size(), is(1));
        assertThat(longFormat.getList("author").get(0), is("anonymousUser"));
        assertThat(longFormat.getList("id").size(), is(1));
        assertThat(longFormat.getList("id").get(0), is("6726763ed6f12386064d41d61ff6580f1cfabc2d"));
        assertThat(longFormat.getList("actions").size(), is(1));
        assertThat(((List) longFormat.getList("actions").get(0)).size(), is(0));
    }

    @Test
    public void testPreparationUpdate() throws Exception {
        // given
        final String preparationId = createPreparationFromDataset("1234", "original_name");

        JsonPath longFormat = when().get("/api/preparations/?format=long").jsonPath();
        assertThat(longFormat.getList("name").size(), is(1));
        assertThat(longFormat.getList("name").get(0), is("original_name"));
        assertThat(longFormat.getList("id").size(), is(1));
        assertThat(longFormat.getList("id").get(0), is(preparationId));

        // when
        given().contentType(ContentType.JSON).body("{ \"name\": \"updated_name\", \"dataSetId\": \"1234\" }")
                .put("/api/preparations/{id}", preparationId).asString();

        // then
        longFormat = when().get("/api/preparations/?format=long").jsonPath();
        assertThat(longFormat.getList("name").size(), is(1));
        assertThat(longFormat.getList("name").get(0), is("updated_name"));
    }

    @Test
    public void testPreparationDelete() throws Exception {
        // given
        final String preparationId = createPreparationFromDataset("1234", "original_name");

        String list = when().get("/api/preparations").asString();
        assertTrue(list.contains(preparationId));

        // when
        when().delete("/api/preparations/" + preparationId).asString();

        // then
        list = when().get("/api/preparations").asString();
        assertEquals("[]", list);
    }

    @Test
    public void testPreparationGet() throws Exception {
        // when
        final String preparationId = createPreparationFromDataset("1234", "testPreparation");

        // then
        final JsonPath longFormat = given().get("/api/preparations/{id}/details", preparationId).jsonPath();
        assertThat(longFormat.getString("dataSetId"), is("1234"));
        assertThat(longFormat.getString("author"), is("anonymousUser"));
        assertThat(longFormat.getString("id"), is("6726763ed6f12386064d41d61ff6580f1cfabc2d"));
        assertThat(longFormat.getList("actions").size(), is(0));
    }

    @Test
    public void testPreparationAppendAction() throws Exception {
        // when
        final String preparationId = createPreparationFromDataset("1234", "testPreparation");

        // when
        applyActionFromFile(preparationId, "action1.json");

        // then
        final List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath()
                .getList("steps");
        assertThat(steps.size(), is(2));
        assertThat(steps.get(0), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
        assertThat(steps.get(1), is(ROOT_STEP.id()));
    }

    @Test
    public void testPreparationUpdateAction() throws Exception {
        // given
        final String preparationId = createPreparationFromDataset("1234", "testPreparation");
        applyActionFromFile(preparationId, "upper_case_1.json");
        applyActionFromFile(preparationId, "upper_case_2.json");

        List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(3));
        assertThat(steps.get(0), is("4115f6d965e146ddbff622633895277c96754541")); // <- upper_case_2
        assertThat(steps.get(1), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59")); // <- upper_case_1
        assertThat(steps.get(2), is(ROOT_STEP.id()));

        // when : Update first action (upper_case_1 / "2b6ae58738239819df3d8c4063e7cb56f53c0d59") with another action
        final String actionContent3 = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("lower_case_1.json"));
        given().body(actionContent3)
                .put("/api/preparations/{preparation}/actions/{action}", preparationId,
                        "2b6ae58738239819df3d8c4063e7cb56f53c0d59").then().statusCode(is(200));

        // then : Steps id should have changed due to update
        steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(3));
        assertThat(steps.get(0), is("b96bf024a0265376ffaedcc974b8b66b3b2c7f64"));
        assertThat(steps.get(1), is("1af242477273e0dae4bb3d32cc524b61744c7895"));
        assertThat(steps.get(2), is(ROOT_STEP.id()));
    }

    @Test
    public void testPreparationInitialContent() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("testCreate.csv", "testPreparationContentGet", "text/csv");
        final InputStream expected = DataPreparationAPITest.class.getResourceAsStream("testCreate_initial.json");

        // when
        final String content = when().get("/api/preparations/{id}/content", preparationId).asString();

        // then
        assertThat(content, sameJSONAsFile(expected));
    }

    @Test
    public void testPreparationContentWithActions() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("testCreate.csv", "testPreparationContentGet", "text/csv");

        List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0), is(ROOT_STEP.id()));

        // when
        applyActionFromFile(preparationId, "action1.json");

        // then
        steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath().getList("steps");
        assertThat(steps.size(), is(2));
        assertThat(steps.get(1), is(ROOT_STEP.id()));
        // Request preparation content at different versions (preparation has 2 steps -> Root + Upper Case).
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

    /**
     * Test that errors are properly listed and displayed.
     */
    @Test
    public void shouldListErrors() throws IOException {
        // given
        final ObjectMapper mapper = new ObjectMapper();

        // when
        final String errors = when().get("/api/errors").asString();

        // then : content is not checked, only mandatory fields
        final JsonNode rootNode = mapper.readTree(errors);
        assertTrue(rootNode.isArray());
        assertTrue(rootNode.size() > 0);
        for (final JsonNode errorCode : rootNode) {
            assertTrue(errorCode.has("code"));
            assertTrue(errorCode.has("http-status-code"));
        }
    }

    @Test
    public void testPreparationDiffPreview() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("preview/preview_dataset.csv", "testPreview", "text/csv");
        applyActionFromFile(preparationId, "preview/upper_case_lastname.json");
        applyActionFromFile(preparationId, "preview/upper_case_firstname.json");
        applyActionFromFile(preparationId, "preview/delete_city.json");

        final List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath()
                .getList("steps");

        final String input = "{" //
                + "   \"preparationId\": \"" + preparationId + "\",\n" //
                + "   \"currentStepId\": \"" + steps.get(2) + "\",\n" // action 1
                + "   \"previewStepId\": \"" + steps.get(0) + "\",\n" // action 1 + 2 + 3
                + "   \"tdpIds\": [1, 3, 5]" //
                + "}";

        final InputStream expectedDiffStream = DataPreparationAPITest.class
                .getResourceAsStream("preview/expected_diff_preview.json");

        // when
        final String diff = given().contentType(ContentType.JSON).body(input).when().post("/api/preparations/preview/diff")
                .asString();

        // then
        assertThat(diff, sameJSONAsFile(expectedDiffStream));
    }

    @Test
    public void testPreparationUpdatePreview() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("preview/preview_dataset.csv", "testPreview", "text/csv");
        applyActionFromFile(preparationId, "preview/upper_case_lastname.json");
        applyActionFromFile(preparationId, "preview/upper_case_firstname.json");
        applyActionFromFile(preparationId, "preview/delete_city.json");

        final List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath()
                .getList("steps");

        final String input = "{" //
                + "   \"preparationId\": \"" + preparationId + "\",\n" //
                + "   \"currentStepId\": \"" + steps.get(0) + "\",\n" // action 1 + 2 + 3
                + "   \"updateStepId\": \"" + steps.get(0) + "\",\n" // action 3
                + "   \"tdpIds\": [1, 3, 5]," //
                + "   \"action\": {" //
                + "       \"action\": \"delete_on_value\",\n"//
                + "       \"parameters\": {" //
                + "           \"column_name\": \"city\"," //
                + "           \"value\": \"Coast city\""//
                + "       }" //
                + "   }"//
                + "}";

        final InputStream expectedDiffStream = DataPreparationAPITest.class
                .getResourceAsStream("preview/expected_update_preview.json");

        // when
        final String diff = given().contentType(ContentType.JSON).body(input).when().post("/api/preparations/preview/update")
                .asString();

        // then
        assertThat(diff, sameJSONAsFile(expectedDiffStream));
    }

    @Test
    public void testExportCsvFromDataset() throws Exception {
        // given
        final String datasetId = createDataset("export/export_dataset.csv", "testExport", "text/csv");

        final String expectedExport = IOUtils.toString(DataPreparationAPITest.class
                .getResourceAsStream("export/expected_export_default_separator.csv"));

        // when
        Thread.sleep(200);// TODO remove this when a better solution is available
        final String export = given().formParam("exportType", CSV).formParam("datasetId", datasetId).when().get("/api/export")
                .asString();

        // then
        assertEquals(expectedExport, export);
    }

    @Test
    public void testExportCsvFromPreparationStep() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("export/export_dataset.csv", "testExport", "text/csv");
        applyActionFromFile(preparationId, "export/upper_case_firstname.json");
        applyActionFromFile(preparationId, "export/upper_case_lastname.json");
        applyActionFromFile(preparationId, "export/delete_city.json");

        final String expectedExport = IOUtils.toString(DataPreparationAPITest.class
                .getResourceAsStream("export/expected_export_preparation_uppercase_firstname.csv"));

        final List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath()
                .getList("steps");

        // when
        final String export = given().formParam("exportType", CSV).formParam("preparationId", preparationId)
                .formParam("stepId", steps.get(2)).when().get("/api/export").asString();

        // then
        assertEquals(expectedExport, export);
    }

    @Test
    public void testExportCsvWithDefaultSeparator() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("export/export_dataset.csv", "testExport", "text/csv");

        final String expectedExport = IOUtils.toString(DataPreparationAPITest.class
                .getResourceAsStream("export/expected_export_default_separator.csv"));

        // when
        final String export = given().formParam("exportType", CSV).formParam("preparationId", preparationId)
                .formParam("stepId", "head").when().get("/api/export").asString();

        // then
        assertEquals(expectedExport, export);
    }

    @Test
    public void testExportCsvWithSpecifiedSeparator() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("export/export_dataset.csv", "testExport", "text/csv");

        final String expectedExport = IOUtils.toString(DataPreparationAPITest.class
                .getResourceAsStream("export/expected_export_semicolon_separator.csv"));

        // when
        final String export = given().formParam("exportType", CSV).formParam("csvSeparator", ";")
                .formParam("preparationId", preparationId).formParam("stepId", "head").when().get("/api/export").asString();

        // then
        assertEquals(expectedExport, export);
    }

    @Test
    public void testExportCsvWithBadBodyInput_noExportType() throws Exception {
        // when
        final Response response = given().formParam("csvSeparator", ";").formParam("preparationId", "4552157454657")
                .formParam("stepId", "head").when().get("/api/export");

        // then
        response.then().statusCode(400);
    }

    @Test
    public void testExportCsvWithBadBodyInput_noPrepId_noDatasetId() throws Exception {
        // when
        final Response response = given().formParam("exportType", CSV).formParam("csvSeparator", ";").formParam("stepId", "head")
                .when().get("/api/export");

        // then
        response.then().statusCode(400);
    }

    @Test
    public void testSuggestActionParams_should_return_dynamic_params_with_dataset() throws Exception {
        // given
        final String dataSetId = createDataset("transformation/cluster_dataset.csv", "testClustering", "text/csv");
        final String expectedClusterParameters = IOUtils.toString(DataPreparationAPITest.class
                .getResourceAsStream("transformation/expected_cluster_params.json"));

        // when
        final String actualClusterParameters = given().formParam("datasetId", dataSetId).formParam("columnId", "uglystate")
                .when().get("/api/transform/suggest/textclustering/params").asString();

        // then
        assertThat(actualClusterParameters, sameJSONAs(expectedClusterParameters));
    }

    @Test
    public void testSuggestActionParams_should_return_dynamic_params_with_preparation() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("transformation/cluster_dataset.csv", "testClustering", "text/csv");
        final String expectedClusterParameters = IOUtils.toString(DataPreparationAPITest.class
                .getResourceAsStream("transformation/expected_cluster_params.json"));

        // when
        final String actualClusterParameters = given().formParam("preparationId", preparationId)
                .formParam("columnId", "uglystate").when().get("/api/transform/suggest/textclustering/params").asString();

        // then
        assertThat(actualClusterParameters, sameJSONAs(expectedClusterParameters));
    }

    @Test
    public void testSuggestActionParams_should_return_400_with_no_preparationId_and_no_datasetId() throws Exception {
        // when
        final Response response = given().formParam("columnId", "uglystate").when()
                .get("/api/transform/suggest/textclustering/params");

        // then
        response.then().statusCode(400);
    }

    private String createDataset(final String file, final String name, final String type) throws IOException {
        final String datasetContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream(file));
        final String dataSetId = given().contentType(ContentType.JSON).body(datasetContent).queryParam("Content-Type", type)
                .when().post("/api/datasets?name={name}", name).asString();
        assertNotNull(dataSetId);
        assertThat(dataSetId, not(""));

        return dataSetId;
    }

    private String createPreparationFromFile(final String file, final String name, final String type) throws IOException {
        final String dataSetId = createDataset(file, "testDataset", type);
        return createPreparationFromDataset(dataSetId, name);
    }

    private String createPreparationFromDataset(final String dataSetId, final String name) throws IOException {
        final String preparationId = given().contentType(ContentType.JSON)
                .body("{ \"name\": \"" + name + "\", \"dataSetId\": \"" + dataSetId + "\"}").when().post("/api/preparations")
                .asString();
        assertThat(preparationId, notNullValue());
        assertThat(preparationId, not(""));

        return preparationId;
    }

    private void applyActionFromFile(final String preparationId, final String actionFile) throws IOException {
        final String action = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream(actionFile));
        applyAction(preparationId, action);
    }

    private void applyAction(final String preparationId, final String action) throws IOException {
        given().contentType(ContentType.JSON).body(action).when().post("/api/preparations/{id}/actions", preparationId).then()
                .statusCode(is(200));
    }
}
