package org.talend.dataprep.api.preparation;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.After;
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

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PreparationTest {

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
    public void rootObjects() throws Exception {
        assertThat(repository.get("cdcd5c9a3a475f2298b5ee3f4258f8207ba10879", PreparationActions.class), notNullValue());
        assertThat(repository.get("cdcd5c9a3a475f2298b5ee3f4258f8207ba10879", Step.class), nullValue());
        assertThat(repository.get("f6e172c33bdacbc69bca9d32b2bd78174712a171", PreparationActions.class), nullValue());
        assertThat(repository.get("f6e172c33bdacbc69bca9d32b2bd78174712a171", Step.class), notNullValue());
    }

    @Test
    public void nullArgs() throws Exception {
        assertThat(repository.get(null, Step.class), nullValue());
        assertThat(repository.get("cdcd5c9a3a475f2298b5ee3f4258f8207ba10879", null), notNullValue());
        Class<? extends Identifiable> objectClass = repository.get("cdcd5c9a3a475f2298b5ee3f4258f8207ba10879", null).getClass();
        assertThat(PreparationActions.class.isAssignableFrom(objectClass), Is.is(true));
        assertThat(repository.get(null, null), nullValue());
    }

    @Test
    public void initialStep() {
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");

        final PreparationActions newContent = new PreparationActions(actions);
        repository.add(newContent);

        final Step s = new Step(ROOT_STEP.id(), newContent.id());
        repository.add(s);

        Preparation preparation = new Preparation("1234", s);
        repository.add(preparation);

        MatcherAssert.assertThat(preparation.id(), Is.is("ae242b07084aa7b8341867a8be1707f4d52501d1"));
    }

    @Test
    public void initialStepWithAppend() {
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");

        final PreparationActions newContent = ROOT_CONTENT.append(actions);
        repository.add(newContent);

        final Step s = new Step(ROOT_STEP.id(), newContent.id());
        repository.add(s);

        final Preparation preparation = new Preparation("1234", s);
        repository.add(preparation);

        MatcherAssert.assertThat(preparation.id(), Is.is("ae242b07084aa7b8341867a8be1707f4d52501d1"));
    }

    @Test
    public void stepsWithAppend() {
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");

        final PreparationActions newContent1 = ROOT_CONTENT.append(actions);
        repository.add(newContent1);
        final PreparationActions newContent2 = newContent1.append(actions);
        repository.add(newContent2);

        // Steps
        final Step s1 = new Step(ROOT_STEP.id(), newContent1.id());
        repository.add(s1);

        final Step s2 = new Step(s1.id(), newContent2.id());
        repository.add(s2);

        // Preparation
        final Preparation preparation = new Preparation("1234", s2);
        repository.add(preparation);

        MatcherAssert.assertThat(preparation.id(), Is.is("ae242b07084aa7b8341867a8be1707f4d52501d1"));
    }

    @Test
    public void prettyPrint() throws Exception {
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");
        final PreparationActions newContent = new PreparationActions(actions);
        repository.add(newContent);

        final Step s = new Step(ROOT_STEP.id(), newContent.id());
        repository.add(s);

        final Preparation preparation = new Preparation("1234", s);
        repository.add(preparation);

        PreparationUtils.prettyPrint(repository, preparation, new NullOutputStream()); // Basic walk through code, no
                                                                                       // assert.
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
                .body(sameJSONAs("[{\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"dataSetId\":\"1234\",\"author\":null,\"name\":null,\"creationDate\":0,\"lastModificationDate\":12345,\"steps\":[\"f6e172c33bdacbc69bca9d32b2bd78174712a171\"],\"actions\":[]}]"));
        Preparation preparation1 = new Preparation("5678", ROOT_STEP);
        preparation1.setCreationDate(500);
        preparation1.setLastModificationDate(456789);
        repository.add(preparation1);
        when().get("/preparations/all")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(sameJSONAs(
                        "[{\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"dataSetId\":\"1234\",\"author\":null,\"name\":null,\"creationDate\":0,\"lastModificationDate\":12345,\"steps\":[\"f6e172c33bdacbc69bca9d32b2bd78174712a171\"],\"actions\":[]}, {\"id\":\"1de0ffaa4e00437dd0c7e1097caf5e5657440ee5\",\"dataSetId\":\"5678\",\"author\":null,\"name\":null,\"creationDate\":500,\"lastModificationDate\":456789,\"steps\":[\"f6e172c33bdacbc69bca9d32b2bd78174712a171\"],\"actions\":[]}]")
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
        String preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"test_name\", \"dataSetId\": \"1234\"}").when().put("/preparations").asString();
        assertThat(preparationId, is("170e086992df1848b8fc9459d87938af6be78720"));
        assertThat(repository.listAll(Preparation.class).size(), is(1));
        Preparation preparation = repository.listAll(Preparation.class).iterator().next();
        assertThat(preparation.id(), is("170e086992df1848b8fc9459d87938af6be78720"));
        assertThat(preparation.getName(), is("test_name"));
    }

    @Test
    public void update() throws Exception {
        assertThat(repository.listAll(Preparation.class).size(), is(0));
        String preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"test_name\", \"dataSetId\": \"1234\"}").when().put("/preparations").asString();
        assertThat(preparationId, is("170e086992df1848b8fc9459d87938af6be78720"));
        Preparation preparation = repository.listAll(Preparation.class).iterator().next();
        long oldModificationDate = preparation.getLastModificationDate();

        // Test preparation details update
        preparationId = given().contentType(ContentType.JSON).body("{\"name\": \"test_name_updated\", \"dataSetId\": \"1234\"}").when().put("/preparations/{id}", preparationId).asString();

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
        InputStream expected = PreparationTest.class.getResourceAsStream("preparation_1234.json");
        assertThat(preparationDetails, sameJSONAsFile(expected));
    }

    @Test
    public void testActionAddUpperCase() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", ROOT_STEP);
        preparation.setCreationDate(0);
        repository.add(preparation);
        long oldModificationDate = preparation.getLastModificationDate();

        // Assert initial step in preparation
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getStep().id(), is("f6e172c33bdacbc69bca9d32b2bd78174712a171"));

        // Update preparation
        given().body(IOUtils.toString(PreparationTest.class.getResourceAsStream("upper_case.json")))
                .contentType(ContentType.JSON).when().post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id(), Preparation.class);
        assertThat(preparation.getLastModificationDate(), is(greaterThan(oldModificationDate)));
        assertThat(preparation.getStep().id(), is("2b6ae58738239819df3d8c4063e7cb56f53c0d59"));
    }

    private List<Action> getSimpleAction(final String actionName, final String paramKey, final String paramValue) {
        final Action action = new Action();
        action.setAction(actionName);
        action.getParameters().put(paramKey, paramValue);

        final List<Action> actions = new ArrayList<>();
        actions.add(action);

        return actions;
    }
}
