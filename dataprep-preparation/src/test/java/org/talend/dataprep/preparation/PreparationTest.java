package org.talend.dataprep.preparation;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import com.jayway.restassured.http.ContentType;
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
import org.talend.dataprep.preparation.store.PreparationRepository;

import com.jayway.restassured.RestAssured;

import java.io.ByteArrayInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PreparationTest {

    @Autowired
    private PreparationRepository repository;

    @Value("${local.server.port}")
    public int port;

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
        Repository repository = new Repository();
        MatcherAssert.assertThat(repository.get("a829c2197884f7c7e894535509c3b78cecd6a0a8", Blob.class), notNullValue());
        MatcherAssert.assertThat(repository.get("a829c2197884f7c7e894535509c3b78cecd6a0a8", Step.class), nullValue());
        MatcherAssert.assertThat(repository.get("599725f0e1331d5f8aae24f22cd1ec768b10348d", Blob.class), nullValue());
        MatcherAssert.assertThat(repository.get("599725f0e1331d5f8aae24f22cd1ec768b10348d", Step.class), notNullValue());
    }

    @Test
    public void nullArgs() throws Exception {
        Repository repository = new Repository();
        MatcherAssert.assertThat(repository.get(null, Step.class), nullValue());
        MatcherAssert.assertThat(repository.get("a829c2197884f7c7e894535509c3b78cecd6a0a8", null), notNullValue());
        Class<? extends Object> objectClass = repository.get("a829c2197884f7c7e894535509c3b78cecd6a0a8", null).getClass();
        MatcherAssert.assertThat(Blob.class.isAssignableFrom(objectClass), Is.is(true));
        MatcherAssert.assertThat(repository.get(null, null), nullValue());
    }

    @Test
    public void initialStep() {
        Repository repository = new Repository();
        Blob newContent = new JSONBlob("{\n" + "  \"actions\": [\n" + "    {\n" + "      \"action\": \"uppercase\",\n"
                + "      \"parameters\": {\n" + "        \"column_name\": \"lastname\"\n" + "      }\n" + "    }\n" + "  ]\n"
                + "}");
        repository.add(newContent);
        Step s = new Step();
        s.setContent(newContent.id());
        s.setParent(RootStep.INSTANCE.id());
        repository.add(s);
        Preparation preparation = new Preparation("1234", s);
        repository.add(preparation);
        MatcherAssert.assertThat(preparation.id(), Is.is("ae242b07084aa7b8341867a8be1707f4d52501d1"));
    }

    @Test
    public void initialStepWithAppend() {
        Repository repository = new Repository();
        String content = "{\n" + "  \"actions\": [\n" + "    {\n" + "      \"action\": \"uppercase\",\n"
                + "      \"parameters\": {\n" + "        \"column_name\": \"lastname\"\n" + "      }\n" + "    }\n" + "  ]\n"
                + "}";
        Blob newContent = ObjectUtils.append(RootBlob.INSTANCE, new ByteArrayInputStream(content.getBytes()));
        repository.add(newContent);
        Step s = new Step();
        s.setContent(newContent.id());
        s.setParent(RootStep.INSTANCE.id());
        repository.add(s);
        Preparation preparation = new Preparation("1234", s);
        repository.add(preparation);
        MatcherAssert.assertThat(preparation.id(), Is.is("ae242b07084aa7b8341867a8be1707f4d52501d1"));
    }

    @Test
    public void stepsWithAppend() {
        Repository repository = new Repository();
        String content = "{\n" + "  \"actions\": [\n" + "    {\n" + "      \"action\": \"uppercase\",\n"
                + "      \"parameters\": {\n" + "        \"column_name\": \"lastname\"\n" + "      }\n" + "    }\n" + "  ]\n"
                + "}";
        JSONBlob newContent1 = ObjectUtils.append(RootBlob.INSTANCE, new ByteArrayInputStream(content.getBytes()));
        repository.add(newContent1);
        JSONBlob newContent2 = ObjectUtils.append(newContent1, new ByteArrayInputStream(content.getBytes()));
        repository.add(newContent2);
        // Steps
        Step s1 = new Step();
        s1.setContent(newContent1.id());
        s1.setParent(RootStep.INSTANCE.id());
        repository.add(s1);
        Step s2 = new Step();
        s2.setContent(newContent2.id());
        s2.setParent(s1.id());
        repository.add(s2);
        // Preparation
        Preparation preparation = new Preparation("1234", s2);
        repository.add(preparation);
        MatcherAssert.assertThat(preparation.id(), Is.is("ae242b07084aa7b8341867a8be1707f4d52501d1"));
    }

    @Test
    public void prettyPrint() throws Exception {
        Repository repository = new Repository();
        Blob newContent = new JSONBlob("{\n" + "  \"actions\": [\n" + "    {\n" + "      \"action\": \"uppercase\",\n"
                + "      \"parameters\": {\n" + "        \"column_name\": \"lastname\"\n" + "      }\n" + "    }\n" + "  ]\n"
                + "}");
        repository.add(newContent);
        Step s = new Step();
        s.setContent(newContent.id());
        s.setParent(RootStep.INSTANCE.id());
        repository.add(s);
        Preparation preparation = new Preparation("1234", s);
        repository.add(preparation);
        ObjectUtils.prettyPrint(repository, preparation, new NullOutputStream()); // Basic walk through code, no assert.
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
        Preparation preparation = new Preparation("1234", RootStep.INSTANCE);
        preparation.setCreationDate(0);
        repository.add(preparation);
        when().get("/preparations/all")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(sameJSONAs("[{\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"dataSetId\":\"1234\",\"author\":null,\"creationDate\":0,\"actions\":[]}]"));
        Preparation preparation1 = new Preparation("5678", RootStep.INSTANCE);
        preparation1.setCreationDate(0);
        repository.add(preparation1);
        when().get("/preparations/all")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(sameJSONAs("[{\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"dataSetId\":\"1234\",\"author\":null,\"creationDate\":0,\"actions\":[]}, {\"id\":\"1de0ffaa4e00437dd0c7e1097caf5e5657440ee5\",\"dataSetId\":\"5678\",\"author\":null,\"creationDate\":0,\"actions\":[]}]"));
    }

    @Test
    public void list() throws Exception {
        when().get("/preparations").then().statusCode(HttpStatus.OK.value()).body(sameJSONAs("[]"));
        repository.add(new Preparation("1234", RootStep.INSTANCE));
        when().get("/preparations").then().statusCode(HttpStatus.OK.value())
                .body(sameJSONAs("[\"ae242b07084aa7b8341867a8be1707f4d52501d1\"]"));
        repository.add(new Preparation("5678", RootStep.INSTANCE));
        when().get("/preparations").then().statusCode(HttpStatus.OK.value())
                .body(sameJSONAs("[\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"1de0ffaa4e00437dd0c7e1097caf5e5657440ee5\"]"));
    }

    @Test
    public void create() throws Exception {
        assertThat(repository.size(), is(0));
        String preparationId = given().body("1234").when().put("/preparations").asString();
        assertThat(preparationId, is("948bed0012a5f13cd1ab93d51992f8952cbbd03b"));
        assertThat(repository.size(), is(1));
        assertThat(repository.list().iterator().next().id(), is("948bed0012a5f13cd1ab93d51992f8952cbbd03b"));
    }

    @Test
    public void get() throws Exception {
        Preparation preparation = new Preparation("1234", RootStep.INSTANCE);
        preparation.setCreationDate(0);
        repository.add(preparation);
        String preparationDetails = when().get("/preparations/{id}", preparation.id()).asString();
        assertThat(
                preparationDetails,
                sameJSONAs("{\"id\":\"ae242b07084aa7b8341867a8be1707f4d52501d1\",\"dataSetId\":\"1234\",\"author\":null,\"creationDate\":0,\"actions\":[]}"));
    }

    @Test
    public void testActionAddUpperCase() throws Exception {
        // Initial preparation
        Preparation preparation = new Preparation("1234", RootStep.INSTANCE);
        preparation.setCreationDate(0);
        repository.add(preparation);
        // Assert initial step in preparation
        preparation = repository.get(preparation.id());
        assertThat(preparation.getStep().id(), is("599725f0e1331d5f8aae24f22cd1ec768b10348d"));
        // Update preparation
        given().body(IOUtils.toString(PreparationTest.class.getResourceAsStream("upper_case.json"))).contentType(ContentType.JSON).when()
                .post("/preparations/{id}/actions", preparation.id());
        preparation = repository.get(preparation.id());
        assertThat(preparation.getStep().id(), is("4b80d91048c69239a05dd45c4fdcfe132b779c7f"));
    }
}
