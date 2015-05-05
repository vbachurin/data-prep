package org.talend.dataprep.transformation;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TransformationServiceTests {

    @Value("${local.server.port}")
    public int port;

    private static String encode(String actions) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(actions.getBytes("UTF-8"));
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void CORSHeaders() throws Exception {
        when().post("/transform").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
        when().post("/suggest/column").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
    }

    @Test
    public void emptyTransformation() {
        when().post("/transform").then().statusCode(HttpStatus.OK.value());
    }

    @Test
    public void noAction() throws Exception {
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when().post("/transform")
                .asString();
        assertEquals(initialContent, transformedContent, false);
    }

    @Test
    public void noActionWithCarrierReturn() throws Exception {
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("carrierReturn.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when().post("/transform")
                .asString();
        assertEquals(initialContent, transformedContent, false);
    }

    @Test
    public void action1() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("action1.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1_action1.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void action2() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("action2.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1_action2.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void testInvalidJSONInput() throws Exception {
        given().contentType(ContentType.JSON).body("invalid content on purpose.").when().post("/transform").then()
                .statusCode(400).content("code", is("TDP_TS_UNABLE_TO_PARSE_JSON"));
    }

    @Test
    public void fillEmptyWithDefaultAction() throws Exception {
        String actions = IOUtils
                .toString(TransformationServiceTests.class.getResourceAsStream("fillEmptyWithDefaultAction.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test3.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("test3_fillEmptyWithDefaultAction.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void fillEmptyWithDefaultActionBoolean() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("fillEmptyWithDefaultBooleanAction.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test3.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("test3_fillEmptyWithDefaultBooleanAction.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void fillEmptyWithDefaultActionInteger() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("fillEmptyWithDefaultIntegerAction.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test3.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("test3_fillEmptyWithDefaultIntegerAction.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void negateActionBoolean() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("negateAction.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test3.json"));
        String expectedContent = IOUtils
                .toString(TransformationServiceTests.class.getResourceAsStream("test3_negateAction.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void cutAction() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("cutAction.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test4.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test4_cutAction.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void deleteEmptyActionString() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("deleteEmptyAction.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test3.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("test3_deleteEmptyAction.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void emptyColumnSuggest() throws Exception {
        String response = given().contentType(ContentType.JSON).body("").when().post("/suggest/column").asString();
        assertEquals("[]", response, false);
    }

    @Test
    public void stringColumnSuggest() throws Exception {
        String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("column1.json"));
        String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggest1.json"));
        Response post = given().contentType(ContentType.JSON).body(columnMetadata).when().post("/suggest/column");
        String response = post.asString();
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void numericColumnSuggest() throws Exception {
        String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("column2.json"));
        String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("suggest_numeric.json"));
        Response post = given().contentType(ContentType.JSON).body(columnMetadata).when().post("/suggest/column");
        String response = post.asString();
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void booleanColumnSuggest() throws Exception {
        String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("column3.json"));
        String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("suggest_boolean.json"));
        Response post = given().contentType(ContentType.JSON).body(columnMetadata).when().post("/suggest/column");
        String response = post.asString();
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void dataSetSuggest() throws Exception {
        String dataSetMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("metadata1.json"));
        String response = given().contentType(ContentType.JSON).body(dataSetMetadata).when().post("/suggest/dataset").asString();
        assertEquals("[]", response, false);
    }

    /**
     * Check that the error listing service returns a list parsable of error codes. The content is not checked
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldListErrors() throws Exception {
        String errors = when().get("/transform/errors").asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualErrors = mapper.readTree(errors);

        assertTrue(actualErrors.isArray());
        assertTrue(actualErrors.size() > 0);
        for (final JsonNode errorCode : actualErrors) {
            assertTrue(errorCode.has("code"));
            assertTrue(errorCode.has("http-status-code"));
        }
    }
    @Test
    public void previewDiff() throws Exception {
        //given
        final String datasetContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("preview.json"));
        final String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("preview_result.json"));

        final String oldActions = getSingleTransformation();
        final String newActions = getMultipleTransformation();
        final String indexes = "WzEsMyw1XQ=="; // [1,3,5] Base64 encoded

        //when
        final Response post = given().contentType(ContentType.JSON).body(datasetContent).when().post("/transform/preview?oldActions=" + oldActions + "&newActions=" + newActions + "&indexes=" + indexes);
        final String response = post.asString();

        //then
        assertEquals(expectedSuggestions, response, false);
    }

    private String getSingleTransformation() {
        /**
         * {"actions": [
         *    {
         *        "action": "uppercase",
         *        "parameters":{
         *          "column_name": "lastname"
         *        }
         *    }
         * ]}
         */
        return "eyJhY3Rpb25zIjogWw0KICAgIHsNCiAgICAgICJhY3Rpb24iOiAidXBwZXJjYXNlIiwNCiAgICAgICJwYXJhbWV0ZXJzIjp7DQogICAgICAgICAgICAiY29sdW1uX25hbWUiOiAibGFzdG5hbWUiDQogICAgICB9DQogICAgfQ0KICBdDQp9";
    }

    private String getMultipleTransformation() {
        /**
         * {"actions": [
         *    {
         *        "action": "uppercase",
         *        "parameters":{
         *          "column_name": "lastname"
         *        }
         *    },
         *    {
         *        "action": "uppercase",
         *        "parameters":{
         *          "column_name": "firstname"
         *        }
         *    },
         *    {
         *        "action": "delete_on_value",
         *        "parameters":{
         *          "column_name": "city",
         *          "value": "Columbia"
         *         }
         *    }
         *]}
         */
        return "eyJhY3Rpb25zIjogWw0KICAgIHsNCiAgICAgICJhY3Rpb24iOiAidXBwZXJjYXNlIiwNCiAgICAgICJwYXJhbWV0ZXJzIjp7DQogICAgICAgICAgICAiY29sdW1uX25hbWUiOiAibGFzdG5hbWUiDQogICAgICB9DQogICAgfSwNCiAgICB7DQogICAgICAiYWN0aW9uIjogInVwcGVyY2FzZSIsDQogICAgICAicGFyYW1ldGVycyI6ew0KICAgICAgICAiY29sdW1uX25hbWUiOiAiZmlyc3RuYW1lIg0KICAgICAgfQ0KICAgIH0sDQogICAgew0KICAgICAgImFjdGlvbiI6ICJkZWxldGVfb25fdmFsdWUiLA0KICAgICAgInBhcmFtZXRlcnMiOnsNCiAgICAgICAgImNvbHVtbl9uYW1lIjogImNpdHkiLA0KICAgICAgICAidmFsdWUiOiAiQ29sdW1iaWEiDQogICAgICB9DQogICAgfQ0KICBdDQp9";
    }
    /**
     * Check that the error listing service returns a list parsable of error codes. The content is not checked
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldListErrors() throws Exception {
        String errors = when().get("/transform/errors").asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualErrors = mapper.readTree(errors);

        assertTrue(actualErrors.isArray());
        assertTrue(actualErrors.size() > 0);
        for (final JsonNode errorCode : actualErrors) {
            assertTrue(errorCode.has("code"));
            assertTrue(errorCode.has("http-status-code"));
        }
    }
}
