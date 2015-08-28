package org.talend.dataprep.api.preparation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.junit.After;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.*;
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

    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------APPEND STEP----------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_add_action_step_after_head() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "my preparation");
        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        assertThat(preparation.getStep().id(), is(ROOT_STEP.getId()));

        // when
        applyTransformation(preparationId, "upper_case.json");

        // then
        final String expectedStepId = "a41184275b046d86c8d98d413ed019bc0a7f3c49";

        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedStepId));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));

        final Step head = repository.get(expectedStepId, Step.class);
        assertThat(head.getParent(), is(ROOT_STEP.getId()));

        final PreparationActions headAction = repository.get(head.getContent(), PreparationActions.class);
        assertThat(headAction.getActions(), hasSize(1));
        assertThat(headAction.getActions().get(0).getAction(), is("uppercase"));
    }

    @Test
    public void should_add_action_step_after_specific_step() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "my preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        assertThat(firstStepId, is("a41184275b046d86c8d98d413ed019bc0a7f3c49"));
        final String secondStepId = applyTransformation(preparationId, "lower_case.json");
        assertThat(secondStepId, is("723b0a4e2b1655b9a0b62c07798df506803e6af4"));

        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        // when
        applyTransformation(preparationId, "compute_length_after_step.json"); //insertion point = firstStep

        // then
        final String expectedFirstStepId = firstStepId;
        final String expectedSecondStepId = "80e272bbb057b75c4fd91aaa6077df2ea3b606f9";
        final String expectedHeadStepId = "15fece190c5a3ff397a5ff50048d989bf75db8db";

        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedHeadStepId));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));

        final Step head = repository.get(expectedHeadStepId, Step.class);
        assertThat(head.getParent(), is(expectedSecondStepId));

        final Step secondStep = repository.get(expectedSecondStepId, Step.class);
        assertThat(secondStep.getParent(), is(expectedFirstStepId));

        final Step firstStep = repository.get(expectedFirstStepId, Step.class);
        assertThat(firstStep.getParent(), is(ROOT_STEP.getId()));

        final PreparationActions headActions = repository.get(head.getContent(), PreparationActions.class);
        assertThat(headActions.getActions(), hasSize(3));
        assertThat(headActions.getActions().get(0).getAction(), is("uppercase"));
        assertThat(headActions.getActions().get(1).getAction(), is("compute_length"));
        assertThat(headActions.getActions().get(2).getAction(), is("lowercase"));
    }

    @Test
    public void should_add_multiple_action_steps_after_head() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "my preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        final String secondStepId = applyTransformation(preparationId, "lower_case.json");

        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        // when
        applyTransformation(preparationId, "upper_case_lower_case.json");

        // then
        final String expectedFirstStepId = firstStepId;
        final String expectedSecondStepId = secondStepId;
        final String expectedThirdStepId = "b20d2df2263117379d1a670534edee67aef85d51";
        final String expectedHeadStepId = "54c21652df9119a1639fe121ac0ebaa30c36bdc3";

        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedHeadStepId));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));

        final Step head = repository.get(expectedHeadStepId, Step.class);
        assertThat(head.getParent(), is(expectedThirdStepId));

        final Step thirdStep = repository.get(expectedThirdStepId, Step.class);
        assertThat(thirdStep.getParent(), is(expectedSecondStepId));

        final Step secondStep = repository.get(expectedSecondStepId, Step.class);
        assertThat(secondStep.getParent(), is(expectedFirstStepId));

        final Step firstStep = repository.get(expectedFirstStepId, Step.class);
        assertThat(firstStep.getParent(), is(ROOT_STEP.getId()));

        final PreparationActions headActions = repository.get(head.getContent(), PreparationActions.class);
        assertThat(headActions.getActions(), hasSize(4));
        assertThat(headActions.getActions().get(0).getAction(), is("uppercase"));
        assertThat(headActions.getActions().get(1).getAction(), is("lowercase"));
        assertThat(headActions.getActions().get(2).getAction(), is("uppercase"));
        assertThat(headActions.getActions().get(3).getAction(), is("lowercase"));
    }

    @Test
    public void should_add_multiple_action_steps_after_specific_step() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "my preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        applyTransformation(preparationId, "lower_case.json");

        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        // when
        applyTransformation(preparationId, "upper_case_lower_case_after_step.json"); //insertion point = first step

        // then
        final String expectedFirstStepId = firstStepId;
        final String expectedSecondStepId = "236eca1c32087a38e7fa9f3ff615d53d5342c40e";
        final String expectedThirdStepId = "5cd4493090831f7e2ceb73fed49f3976485e13f7";
        final String expectedHeadStepId = "6885ac3ebc4ab347c41c013def2a7e2ae7fd3e0a";

        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedHeadStepId));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));

        final Step head = repository.get(expectedHeadStepId, Step.class);
        assertThat(head.getParent(), is(expectedThirdStepId));

        final Step thirdStep = repository.get(expectedThirdStepId, Step.class);
        assertThat(thirdStep.getParent(), is(expectedSecondStepId));

        final Step secondStep = repository.get(expectedSecondStepId, Step.class);
        assertThat(secondStep.getParent(), is(expectedFirstStepId));
        System.out.println(secondStep.getContent());

        final Step firstStep = repository.get(expectedFirstStepId, Step.class);
        assertThat(firstStep.getParent(), is(ROOT_STEP.getId()));

        final PreparationActions headActions = repository.get(head.getContent(), PreparationActions.class);
        assertThat(headActions.getActions(), hasSize(4));
        assertThat(headActions.getActions().get(0).getAction(), is("uppercase"));
        assertThat(headActions.getActions().get(1).getAction(), is("uppercase"));
        assertThat(headActions.getActions().get(2).getAction(), is("lowercase"));
        assertThat(headActions.getActions().get(3).getAction(), is("lowercase"));
    }

    @Test
    public void should_return_error_when_scope_is_not_consistent_on_append_transformation() throws Exception {
        //given
        final String preparationId = createPreparation("1234", "my preparation");

        //when
        final Response request = given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("incomplete_transformation_params.json")))//
                .contentType(ContentType.JSON)//
                .when()//
                .post("/preparations/{id}/actions", preparationId);

        //then
        request.then()//
                .statusCode(400)//
                .assertThat()//
                .body("code", is("TDP_ALL_MISSING_ACTION_SCOPE"));
    }

    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------UPDATE STEP----------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_modify_single_action() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "my preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        assertThat(firstStepId, is("a41184275b046d86c8d98d413ed019bc0a7f3c49"));

        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        // when
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))//
                .contentType(ContentType.JSON)//
                .when()//
                .put("/preparations/{id}/actions/{action}", preparationId, firstStepId);

        // then
        final String expectedStepId = "55ca7e996811cb944bd7053556c698ee77d6d24b";
        preparation = repository.get(preparationId, Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedStepId));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));

        final Step head = repository.get(expectedStepId, Step.class);
        assertThat(head.getParent(), is(ROOT_STEP.getId()));
    }

    @Test
    public void should_modify_last_action() throws Exception {
        // Initial preparation
        final String preparationId = createPreparation("1234", "my preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        final String secondStepId = applyTransformation(preparationId, "lower_case.json");
        assertThat(secondStepId, is("723b0a4e2b1655b9a0b62c07798df506803e6af4"));

        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        // when : update second (last) step
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparationId, secondStepId);

        // then
        final String expectedStepId = "4aa662330d7633a872a42999cb43a3d4cb7ef394";
        preparation = repository.get(preparationId, Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedStepId));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));

        final Step head = repository.get(expectedStepId, Step.class);
        assertThat(head.getParent(), is(firstStepId));
    }

    @Test
    public void should_modify_first_action() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "my preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        assertThat(firstStepId, is("a41184275b046d86c8d98d413ed019bc0a7f3c49"));
        final String secondStepId = applyTransformation(preparationId, "lower_case.json");
        assertThat(secondStepId, is("723b0a4e2b1655b9a0b62c07798df506803e6af4"));

        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        // when
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("upper_case_modified.json")))
                .contentType(ContentType.JSON).when()
                .put("/preparations/{id}/actions/{action}", preparation.id(), "a41184275b046d86c8d98d413ed019bc0a7f3c49");

        // then
        final String expectedFirstStepId = "55ca7e996811cb944bd7053556c698ee77d6d24b";
        final String expectedSecondStepId = "beca40369fdc5531f78e38332f1dced490cba137";

        preparation = repository.get(preparationId, Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedSecondStepId));
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));

        final Step head = repository.get(expectedSecondStepId, Step.class);
        assertThat(head.getParent(), is(expectedFirstStepId));

        final Step first = repository.get(expectedFirstStepId, Step.class);
        assertThat(first.getParent(), is(ROOT_STEP.getId()));
    }

    @Test
    public void should_return_error_when_scope_is_not_consistent_on_transformation_update() throws Exception {
        //given
        final String preparationId = createPreparation("1234", "my preparation");
        final String stepId = applyTransformation(preparationId, "upper_case.json");

        //when
        final Response request = given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream("incomplete_transformation_params.json")))//
                .contentType(ContentType.JSON)//
                .when()//
                .put("/preparations/{id}/actions/{action}", preparationId, stepId);

        //then
        request.then()//
                .statusCode(400)//
                .assertThat()//
                .body("code", is("TDP_ALL_MISSING_ACTION_SCOPE"));
    }

    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------DELETE STEP----------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_delete_last_step_in_cascade_mode() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "My preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        final String secondStepId = applyTransformation(preparationId, "lower_case.json");

        // when : delete second step in cascade mode
        when().delete("/preparations/{id}/actions/{action}", preparationId, secondStepId);

        // then
        final Preparation preparation = repository.get(preparationId, Preparation.class);
        assertThat(preparation.getStep().id(), is(firstStepId));

        final Step head = repository.get(firstStepId, Step.class);
        final PreparationActions headActions = repository.get(head.getContent(), PreparationActions.class);
        assertThat(headActions.getActions(), hasSize(1));
        assertThat(headActions.getActions().get(0).getAction(), is("uppercase"));
    }

    @Test
    public void should_delete_all_steps_in_cascade_mode() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "My preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        applyTransformation(preparationId, "lower_case.json");

        // when : delete first step in cascade mode
        when().delete("/preparations/{id}/actions/{action}", preparationId, firstStepId);

        // then
        final Preparation preparation = repository.get(preparationId, Preparation.class);
        assertThat(preparation.getStep().id(), is(ROOT_STEP.getId()));
    }

    @Test
    public void should_delete_step_in_single_mode() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "My preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        applyTransformation(preparationId, "lower_case.json");

        // when : delete second step in single mode
        when().delete("/preparations/{id}/actions/{action}?single=true", preparationId, firstStepId)//
                .then()//
                .statusCode(200);

        // then
        final String expectedHeadStepId = "53543f2a48b9c661f510dc428909d42b05e493f6";

        final Preparation preparation = repository.get(preparationId, Preparation.class);
        assertThat(preparation.getStep().id(), is(expectedHeadStepId));

        final Step head = repository.get(expectedHeadStepId, Step.class);
        final PreparationActions headActions = repository.get(head.getContent(), PreparationActions.class);
        assertThat(headActions.getActions(), hasSize(1));
        assertThat(headActions.getActions().get(0).getAction(), is("lowercase"));
    }

    @Test
    public void should_throw_exception_when_step_to_delete_impacts_same_column_as_following_steps() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "My preparation");
        final String firstStepId = applyTransformation(preparationId, "upper_case.json");
        applyTransformation(preparationId, "lower_case.json");
        applyTransformation(preparationId, "compute_length.json"); //impact same column as fist step

        // when : delete second step in single mode
        final Response response = when().delete("/preparations/{id}/actions/{action}?single=true", preparationId, firstStepId);

        //then
        response.then()//
                .statusCode(403)//
                .assertThat()//
                .body("code", is("TDP_PS_PREPARATION_STEP_CANNOT_BE_DELETED_IN_SINGLE_MODE"));
    }

    @Test
    public void should_throw_exception_when_step_to_delete_is_root() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "My preparation");
        applyTransformation(preparationId, "upper_case.json");
        applyTransformation(preparationId, "lower_case.json");

        // when: delete ROOT
        final Response response = when().delete("/preparations/{id}/actions/{action}", preparationId, ROOT_STEP.getId());

        //then
        response.then()
                .statusCode(403)
                .assertThat()
                .body("code", is("TDP_PS_PREPARATION_ROOT_STEP_CANNOT_BE_DELETED"));
    }

    @Test
    public void should_throw_exception_when_step_doesnt_exist() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "My preparation");
        applyTransformation(preparationId, "upper_case.json");
        applyTransformation(preparationId, "lower_case.json");

        // when : delete unknown step
        final Response response = when().delete("/preparations/{id}/actions/{action}", preparationId, "azerty");

        //then
        response.then()
                .statusCode(400)
                .assertThat()
                .body("code", is("TDP_PS_PREPARATION_STEP_DOES_NOT_EXIST"));
    }

    @Test
    public void should_update_modification_date_on_delete_step() throws Exception {
        // given
        final String preparationId = createPreparation("1234", "My preparation");
        applyTransformation(preparationId, "upper_case.json");
        final String secondStepId = applyTransformation(preparationId, "lower_case.json");

        Preparation preparation = repository.get(preparationId, Preparation.class);
        final long oldModificationDate = preparation.getLastModificationDate();

        // when: delete LOWERCASE
        when().delete("/preparations/{id}/actions/{action}", preparation.id(), secondStepId);

        // then
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(oldModificationDate, lessThan(preparation.getLastModificationDate()));
    }

    //------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------GETTERS------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * @see org.talend.dataprep.preparation.service.PreparationService#listByDataSet
     */
    @Test
    public void should_return_list_of_specific_dataset_preparations() throws Exception {
        // given : relevant preparation
        final String wantedDataSetId = "wanted";
        final String preparation1 = createPreparation(wantedDataSetId, "prep_1");
        final String preparation2 = createPreparation(wantedDataSetId, "prep_2");

        // given : noise data not to be returned by the service
        final String preparation3 = createPreparation("4523", "prep_3");
        final String preparation4 = createPreparation("7534", "prep_4");
        final String preparation5 = createPreparation("1598", "prep_5");

        // when
        final String result = when().get("/preparations?dataSetId=" + wantedDataSetId).asString();
        final List<String> preparationIds = JsonPath.from(result).get("id");

        // then
        assertThat(preparationIds, hasItem(preparation1));
        assertThat(preparationIds, hasItem(preparation2));
        assertThat(preparationIds, not(hasItem(preparation3)));
        assertThat(preparationIds, not(hasItem(preparation4)));
        assertThat(preparationIds, not(hasItem(preparation5)));
    }

    /**
     * Check that the error listing service returns a list parsable of error codes. The content is not checked
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldListErrors() throws Exception {
        // when
        final String errors = when().get("/preparations/errors").asString();

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode actualErrorCodes = mapper.readTree(errors);

        // then
        assertTrue(actualErrorCodes.isArray());
        assertTrue(actualErrorCodes.size() > 0);
        for (final JsonNode currentCode : actualErrorCodes) {
            assertTrue(currentCode.has("code"));
            assertTrue(currentCode.has("http-status-code"));
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------UTILS-------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Create a new preparation with the given name
     * @param name The preparation name
     * @return The preparation id
     */
    private String createPreparation(final String datasetId, final String name) {
        final Preparation preparation = new Preparation(datasetId, ROOT_STEP);
        preparation.setName(name);
        preparation.setCreationDate(0);
        repository.add(preparation);

        return preparation.id();
    }

    /**
     * Append an action to a preparation
     * @param preparationId The preparation id
     * @param transformationFilePath The transformation json file path
     * @return The created step id
     */
    private String applyTransformation(final String preparationId, final String transformationFilePath) throws IOException {
        given().body(IOUtils.toString(PreparationServiceTest.class.getResourceAsStream(transformationFilePath)))//
                .contentType(ContentType.JSON)//
                .when()//
                .post("/preparations/{id}/actions", preparationId)//
                .then()//
                .statusCode(200);

        final Preparation preparation = repository.get(preparationId, Preparation.class);
        return preparation.getStep().id();
    }

}
