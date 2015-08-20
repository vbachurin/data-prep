package org.talend.dataprep.transformation.api;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.http.ContentType.JSON;
import static junit.framework.TestCase.assertTrue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.dataprep.transformation.Application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;

/**
 * General integration tests on TransformationService.
 */
public class TransformationServiceTests extends TransformationServiceBaseTests {

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

    @Test
    public void previewDiff() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(Application.class.getResourceAsStream("preview/input.json"));
        final String expected = IOUtils.toString(Application.class.getResourceAsStream("preview/expected_output.json"));

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

    @Test
    public void testDynamicParams_should_return_textclustering_dynamic_params() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(Application.class.getResourceAsStream("parameters/dataset.json"));
        final String expectedParameters = IOUtils
                .toString(Application.class.getResourceAsStream("parameters/expected_cluster_params_soundex.json"));

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

    private String getSingleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"uppercase\",\"parameters\":{ \"column_id\": \"firstname\", \"scope\": \"column\" } }, { \"action\": \"delete_on_value\", \"parameters\":{ \"column_id\":\"city\", \"value\": \"Columbia\", \"scope\": \"column\" } } ]}";
    }

}
