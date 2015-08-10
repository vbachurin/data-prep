package org.talend.dataprep.api.preparation;

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

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.jayway.restassured.response.Response;
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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.preparation.Application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PreparationServiceTest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    private PreparationRepository repository;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

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
    public void updateWithSpecialArguments() throws Exception {
        Preparation preparation = new Preparation();
        preparation.setDataSetId("1234");
        preparation.setName("test_name");
        String preparationId = given().contentType(ContentType.JSON).body(builder.build().writer().writeValueAsBytes(preparation))
                .when().put("/preparations").asString();
        assertThat(preparationId, is("170e086992df1848b8fc9459d87938af6be78720"));

        // Test preparation details update
        preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"éàçè\", \"dataSetId\": \"1234\"}".getBytes("UTF-8"))
                .when().put("/preparations/{id}", preparationId).asString();

        // Preparation id should change (new name)
        assertThat(preparationId, is("f7c4550ce8f9e7071c638b71e2930e3dd65ac3c0"));
        Collection<Preparation> preparations = repository.listAll(Preparation.class);
        assertThat(preparations.size(), is(1));
        preparation = preparations.iterator().next();
        assertThat(preparation.id(), is("f7c4550ce8f9e7071c638b71e2930e3dd65ac3c0"));
        assertThat(preparation.getName(), is("éàçè"));
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
        assertThat(preparation.getStep().id(), is("a41184275b046d86c8d98d413ed019bc0a7f3c49"));
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
        assertThat(preparation.getStep().id(), is("a41184275b046d86c8d98d413ed019bc0a7f3c49"));
        // Update preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparation.id(), preparation.getStep().id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("55ca7e996811cb944bd7053556c698ee77d6d24b"));
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
        assertThat(preparation.getStep().id(), is("a41184275b046d86c8d98d413ed019bc0a7f3c49"));
        // Add step to preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("723b0a4e2b1655b9a0b62c07798df506803e6af4"));
        // Update preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparation.id(), preparation.getStep().id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("4aa662330d7633a872a42999cb43a3d4cb7ef394"));
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
        assertThat(preparation.getStep().id(), is("a41184275b046d86c8d98d413ed019bc0a7f3c49"));
        // Add step to preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("lower_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("723b0a4e2b1655b9a0b62c07798df506803e6af4"));
        // Update preparation
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparation.id(), "a41184275b046d86c8d98d413ed019bc0a7f3c49");
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("beca40369fdc5531f78e38332f1dced490cba137"));
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

    @Test
    public void should_return_error_when_scope_is_not_consistent_on_append_transformation() throws Exception {
        //given
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);

        preparation = repository.get(preparation.id(), Preparation.class);

        //when
        final Response request = given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("incomplete_transformation_params.json")))//
                .contentType(ContentType.JSON)//
                .when()//
                .post("/preparations/{id}/actions", preparation.id());

        //then
        request.then()//
                .statusCode(400)//
                .assertThat()//
                .body("code", is("TDP_ALL_MISSING_ACTION_SCOPE"));

    }

    @Test
    public void should_return_error_when_scope_is_not_consistent_on_transformation_update() throws Exception {
        //given
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);

        preparation = repository.get(preparation.id(), Preparation.class);
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case.json")))//
                .contentType(ContentType.JSON)//
                .when()//
                .post("/preparations/{id}/actions", preparation.id())//
                .then()//
                .statusCode(200);

        final String preparationId = preparation.id();
        final String stepId = preparation.getStep().id();

        //when
        final Response request = given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("incomplete_transformation_params.json")))//
                .contentType(ContentType.JSON).when()//
                .put("/preparations/{id}/actions/{action}", preparationId, stepId);

        //then
        request.then()//
                .statusCode(400)//
                .assertThat()//
                .body("code", is("TDP_ALL_MISSING_ACTION_SCOPE"));
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
