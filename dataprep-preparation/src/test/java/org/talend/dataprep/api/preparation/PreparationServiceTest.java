package org.talend.dataprep.api.preparation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
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
import org.talend.dataprep.preparation.Application;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PreparationServiceTest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    private PreparationRepository repository;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @After
    public void tearDown() throws Exception {
        repository.clear();
    }

    @Test
    public void CORSHeaders() throws Exception {
        when().get("/preparations").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
    }

    @Test
    public void listAll() throws Exception {
        when().get("/preparations/all").then().statusCode(HttpStatus.OK.value()).body(sameJSONAs("[]"));
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        preparation.setLastModificationDate(12345);
        repository.add(preparation);
        when().get("/preparations/all")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(sameJSONAs("[{\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"dataSetId\":\"1234\",\"author\":null,\"name\":null,\"creationDate\":0,\"lastModificationDate\":12345,\"steps\":[\"f6e172c33bdacbc69bca9d32b2bd78174712a171\"],\"actions\":[],\"metadata\":[]}]"));
        Preparation preparation1 = new Preparation("5678", ROOT_STEP);
        preparation1.setCreationDate(500);
        preparation1.setLastModificationDate(456789);
        repository.add(preparation1);
        when().get("/preparations/all")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(sameJSONAs(
                        "[{\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"dataSetId\":\"1234\",\"author\":null,\"name\":null,\"creationDate\":0,\"lastModificationDate\":12345,\"steps\":[\"f6e172c33bdacbc69bca9d32b2bd78174712a171\"],\"actions\":[],\"metadata\":[]}, {\"id\":\"1de0ffaa4e00437dd0c7e1097caf5e5657440ee5\",\"dataSetId\":\"5678\",\"author\":null,\"name\":null,\"creationDate\":500,\"lastModificationDate\":456789,\"steps\":[\"f6e172c33bdacbc69bca9d32b2bd78174712a171\"],\"actions\":[],\"metadata\":[]}]")
                        .allowingAnyArrayOrdering());
    }

    @Test
    public void list() throws Exception {
        when().get("/preparations").then().statusCode(HttpStatus.OK.value()).body(sameJSONAs("[]"));
        repository.add(new Preparation("1234", ROOT_STEP));
        when().get("/preparations").then().statusCode(HttpStatus.OK.value())
                .body(sameJSONAs("[\"ae242b07084aa7b8341867a8be1707f4d52501d1\"]"));
        repository.add(new Preparation("5678", ROOT_STEP));
        List<String> list = when().get("/preparations").jsonPath().getList("");
        assertThat(list, hasItems("ae242b07084aa7b8341867a8be1707f4d52501d1", "1de0ffaa4e00437dd0c7e1097caf5e5657440ee5"));
    }

    @Test
    public void create() throws Exception {
        assertThat(repository.listAll(Preparation.class).size(), is(0));
        String preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"test_name\", \"dataSetId\": \"1234\"}")
                .when().put("/preparations").asString();
        assertThat(preparationId, is("170e086992df1848b8fc9459d87938af6be78720"));
        assertThat(repository.listAll(Preparation.class).size(), is(1));
        Preparation preparation = repository.listAll(Preparation.class).iterator().next();
        assertThat(preparation.id(), is("170e086992df1848b8fc9459d87938af6be78720"));
        assertThat(preparation.getName(), is("test_name"));
    }

    @Test
    public void delete() throws Exception {
        assertThat(repository.listAll(Preparation.class).size(), is(0));
        String preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"test_name\", \"dataSetId\": \"1234\"}")
                .when().put("/preparations").asString();
        assertThat(preparationId, is("170e086992df1848b8fc9459d87938af6be78720"));
        assertThat(repository.listAll(Preparation.class).size(), is(1));
        Preparation preparation = repository.listAll(Preparation.class).iterator().next();
        assertThat(preparation.id(), is("170e086992df1848b8fc9459d87938af6be78720"));
        assertThat(preparation.getName(), is("test_name"));

        when().delete("/preparations/{id}", preparationId).then().statusCode(HttpStatus.OK.value());
        assertThat(repository.listAll(Preparation.class).size(), is(0));
    }

    @Test
    public void update() throws Exception {
        assertThat(repository.listAll(Preparation.class).size(), is(0));
        String preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"test_name\", \"dataSetId\": \"1234\"}")
                .when().put("/preparations").asString();
        assertThat(preparationId, is("170e086992df1848b8fc9459d87938af6be78720"));
        Preparation preparation = repository.listAll(Preparation.class).iterator().next();
        long oldModificationDate = preparation.getLastModificationDate();

        // Test preparation details update
        preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"test_name_updated\", \"dataSetId\": \"1234\"}")
                .when().put("/preparations/{id}", preparationId).asString();

        // Preparation id should not change
        assertThat(preparationId, is("0d291a2159ae36ee9177b8b845b3c8f1b0e0f30b"));
        Collection<Preparation> preparations = repository.listAll(Preparation.class);
        assertThat(preparations.size(), is(1));
        preparation = preparations.iterator().next();
        assertThat(preparation.id(), is("0d291a2159ae36ee9177b8b845b3c8f1b0e0f30b"));
        assertThat(preparation.getName(), is("test_name_updated"));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
    }

    @Test
    public void get() throws Exception {
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        preparation.setLastModificationDate(12345);
        repository.add(preparation);
        String preparationDetails = when().get("/preparations/{id}", preparation.id()).asString();
        InputStream expected = PreparationServiceTest.class.getResourceAsStream("preparation_1234.json");
        assertThat(preparationDetails, sameJSONAsFile(expected));
    }

    @Test
    public void actionAddUpperCase() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);
        long oldModificationDate = preparation.getLastModificationDate();

        // Assert initial step in preparation
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("f6e172c33bdacbc69bca9d32b2bd78174712a171"));

        // Update preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
    }

    @Test
    public void modifySingleAction() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);
        long oldModificationDate = preparation.getLastModificationDate();
        // Add initial action to preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
        // Update preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparation.id(), preparation.getStep().id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("b7fad51b715f2f9d42aae663dc85f5b7bb4b9f15"));
    }

    @Test
    public void modifyLastOfActions() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);
        long oldModificationDate = preparation.getLastModificationDate();
        // Add initial action to preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
        // Add step to preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("7d7396ab3bce49bb634d880bdd20800dd418a5d0"));
        // Update preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparation.id(), preparation.getStep().id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("4115f6d965e146ddbff622633895277c96754541"));
    }

    @Test
    public void modifyFirstOfActions() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);
        long oldModificationDate = preparation.getLastModificationDate();
        // Add initial action to preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
        // Add step to preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("7d7396ab3bce49bb634d880bdd20800dd418a5d0"));
        // Update preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparation.id(), "2b6ae58738239819df3d8c4063e7cb56f53c0d59");
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("91629cad70f47957bcbcdeac436878cc5f713b8a"));
    }

    @Test
    public void shouldDeleteLastAction() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);

        // Add UPPERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        final String firstStepId = preparation.getStep().id();

        // Add LOWERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        final String secondStepId = preparation.getStep().id();

        // Delete LOWERCASE
        when().delete("/preparations/{id}/actions/{action}", preparation.id(), secondStepId);
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is(firstStepId));
    }

    @Test
    public void shouldDeleteAllActions() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);

        // Add UPPERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        final String firstStepId = preparation.getStep().id();

        // Add LOWERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);

        // Delete UPPERCASE
        when().delete("/preparations/{id}/actions/{action}", preparation.id(), firstStepId);
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is(ROOT_STEP.getId()));
    }

    @Test
    public void shouldThrowExceptionWhenActionToDeleteIsRoot() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);

        // Add UPPERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);

        // Add LOWERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);

        // Delete ROOT
        when().delete("/preparations/{id}/actions/{action}", preparation.id(), ROOT_STEP.getId())
                .then()
                .statusCode(400)
                .assertThat()
                .body("code", is("TDP_PS_PREPARATION_ROOT_STEP_CANNOT_BE_CHANGED"));
    }

    @Test
    public void shouldThrowExceptionWhenActionDoesntExist() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);

        // Add UPPERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);

        // Add LOWERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);

        // Delete unknown step
        when().delete("/preparations/{id}/actions/{action}", preparation.id(), "azerty")
                .then()
                .statusCode(400)
                .assertThat()
                .body("code", is("TDP_PS_PREPARATION_STEP_DOES_NOT_EXIST"));
    }

    @Test
    public void shouldUpdateModificationData() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);

        // Add UPPERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);

        // Add LOWERCASE action
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        final String secondStepId = preparation.getStep().id();
        long oldModificationDate = preparation.getLastModificationDate();

        // Delete LOWERCASE
        when().delete("/preparations/{id}/actions/{action}", preparation.id(), secondStepId);
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(oldModificationDate, lessThan(preparation.getLastModificationDate()));
    }

    /**
     * @see org.talend.dataprep.preparation.service.PreparationService#listByDataSet
     */
    @Test
    public void getByDataSet() throws Exception {

        String wantedDataSetId = "wanted";

        // relevant data
        Preparation preparation1 = getPreparation(wantedDataSetId, "prep_1");
        repository.add(preparation1);
        Preparation preparation2 = getPreparation(wantedDataSetId, "prep_2");
        repository.add(preparation2);

        // noise data not to be returned by the service
        repository.add(getPreparation("4523", "prep_3"));
        repository.add(getPreparation("7534", "prep_4"));
        repository.add(getPreparation("1598", "prep_5"));

        String result = when().get("/preparations?dataSetId=" + wantedDataSetId).asString();
        List<String> preparationIds = JsonPath.from(result).get("id");
        for (String preparationId : preparationIds) {
            Assert.assertTrue(result.contains(preparationId));
        }

    }

    /**
     * Check that the error listing service returns a list parsable of error codes. The content is not checked
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldListErrors() throws Exception {
        String errors = when().get("/preparations/errors").asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualErrorCodes = mapper.readTree(errors);

        assertTrue(actualErrorCodes.isArray());
        assertTrue(actualErrorCodes.size() > 0);
        // only checks mandatory attributes
        for (final JsonNode currentCode : actualErrorCodes) {
            assertTrue(currentCode.has("code"));
            assertTrue(currentCode.has("http-status-code"));
        }
    }

    /**
     * Return a preparation from the given parameters. Simple function used to simplify code writing.
     *
     * @param dataSetId the dataset id.
     * @param name      the preparation name.
     * @return a preparation from the given parameters.
     */
    private Preparation getPreparation(String dataSetId, String name) {
        Preparation preparation = new Preparation(dataSetId, ROOT_STEP);
        preparation.setName(name);
        return preparation;
    }

}
