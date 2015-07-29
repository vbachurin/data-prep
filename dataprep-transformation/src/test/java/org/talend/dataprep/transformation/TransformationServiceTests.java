package org.talend.dataprep.transformation;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.http.ContentType.JSON;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@DirtiesContext
public class TransformationServiceTests {

    @Value("${local.server.port}")
    public int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void CORSHeaders() throws Exception {
        when().post("/transform/JSON").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
        when().post("/suggest/column").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
    }

    //------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------Actions Odd cases------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    @Test
    public void emptyTransformation() {
        given()//
                .multiPart("content", "")//
                .when()//
                .post("/transform/JSON")//
                .then()//
                .statusCode(OK.value());
    }

    @Test
    public void noAction() throws Exception {
        //given
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_case.json"));

        //when
        final String transformedContent = given()//
                .multiPart("actions", "")//
                .multiPart("content", initialContent)//
                .when()
                .post("/transform/JSON")//
                .asString();

        //then
        assertEquals(initialContent, transformedContent, false);
    }

    @Test
    public void noActionWithCarrierReturn() throws Exception {
        //given
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_with_carrier_return.json"));

        //when
        final String transformedContent = given()//
                .multiPart("actions", "")//
                .multiPart("content", initialContent)//
                .when()//
                .post("/transform/JSON")//
                .asString();

        //then
        assertEquals(initialContent, transformedContent, false);
    }

    @Test
    public void testInvalidJSONInput() throws Exception {
        given()//
                .multiPart("content", "invalid content on purpose.")//
                .when()//
                .post("/transform/JSON")//
                .then()//
                .statusCode(400)//
                .content("code", is("TDP_TS_UNABLE_TO_PARSE_JSON"));
    }


    //------------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------Actions---------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    @Test
    public void uppercaseAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/uppercaseAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_case.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/uppercaseAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void lowercaseAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/lowercaseAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_case.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/lowercaseAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void fillEmptyWithDefaultAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/fillEmptyWithDefaultAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/fillEmptyWithDefaultAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void fillEmptyWithDefaultActionBoolean() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/fillEmptyWithDefaultBooleanAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/fillEmptyWithDefaultBooleanAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void fillEmptyWithDefaultActionInteger() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/fillEmptyWithDefaultIntegerAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/fillEmptyWithDefaultIntegerAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void negateActionBoolean() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/negateAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/negateAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void cutAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/cutAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_cut.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/cutAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void duplicateAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/duplicateAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_duplicate.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/duplicateAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void deleteEmptyActionString() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/deleteEmptyAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/deleteEmptyAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void absoluteIntAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/absoluteIntAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_absoluteAction.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/absoluteIntAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void absoluteFloatAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/absoluteFloatAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input_absoluteAction.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/absoluteFloatAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void replaceOnValueAction() throws Exception {
        //given
        final String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/replaceOnValueAction.json"));
        final String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/input.json"));
        final String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("actions/replaceOnValueAction_expected.json"));

        //when
        final String transformedContent = given() //
                .multiPart("actions", actions) //
                .multiPart("content", initialContent) //
                .when() //
                .post("/transform/JSON") //
                .asString();

        //then
        assertEquals(expectedContent, transformedContent, false);
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------Suggestions-------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void dataSetSuggest() throws Exception {
        //given
        final String dataSetMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/dataset_metadata.json"));

        //when
        final String response = given() //
                .contentType(JSON) //
                .body(dataSetMetadata) //
                .when() //
                .post("/suggest/dataset") //
                .asString();

        //then
        assertEquals("[]", response, false);
    }

    @Test
    public void emptyColumnSuggest() throws Exception {
        //when
        final String response = given() //
                .contentType(JSON) //
                .body("") //
                .when() //
                .post("/suggest/column") //
                .asString();

        //then
        assertEquals("[]", response, false);
    }

    @Test
    public void stringColumnSuggest() throws Exception {
        //given
        final String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/string_column.json"));
        final String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/string_column_suggestions.json"));

        //when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        //then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void floatColumnSuggest() throws Exception {
        //given
        final String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/float_column.json"));
        final String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/float_column_suggestions.json"));

        //when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        //then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void integerColumnSuggest() throws Exception {
        //given
        final String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/integer_column.json"));
        final String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/integer_column_suggestions.json"));

        //when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        //then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void booleanColumnSuggest() throws Exception {
        //given
        final String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/boolean_column.json"));
        final String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/boolean_column_suggestions.json"));

        //when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        //then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void dateColumnSuggest() throws Exception {
        //given
        final String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/date_column.json"));
        final String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/date_column_suggestions.json"));

        //when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        //then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void dateColumnSuggestWithStringType() throws Exception {
        //given
        final String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/date_column_string_type.json"));
        final String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggestions/date_column_string_type_suggestions.json"));

        //when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        //then
        assertEquals(expectedSuggestions, response, false);
    }


    //------------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------Diff------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    @Test
    public void previewDiff() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("preview/input.json"));
        final String expected = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("preview/expected_output.json"));

        final String oldActions = getSingleTransformation();
        final String newActions = getMultipleTransformation();
        final String indexes = "[1,3,5]";

        // when
        final Response post = given() //
                .multiPart("oldActions", oldActions) //
                .multiPart("newActions", newActions) //
                .multiPart("indexes", indexes) //
                .multiPart("content", datasetContent) //
                .when() //
                .post("/transform/preview");
        final String response = post.asString();

        // then
        assertEquals(expected, response, false);
    }

    //------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------Dynamic Params------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void testDynamicParams_should_return_textclustering_dynamic_params() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("parameters/dataset.json"));
        final String expectedParameters = IOUtils.toString(TransformationServiceTests.class
                .getResourceAsStream("parameters/expected_cluster_params_soundex.json"));

        // when
        final Response post = given() //
                .contentType(JSON) //
                .body(datasetContent) //
                .when() //
                .post("/transform/suggest/textclustering/params?columnId=uglystate");
        final String response = post.asString();

        // then
        assertEquals(expectedParameters, response, false);
    }

    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------ERRORS---------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

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

    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------Utils----------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    private String getSingleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\" } } ]}";
    }

    private String getMultipleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\" } }, { \"action\": \"uppercase\",\"parameters\":{ \"column_id\": \"firstname\" } }, { \"action\": \"delete_on_value\", \"parameters\":{ \"column_id\":\"city\", \"value\": \"Columbia\" } } ]}";
    }

}
