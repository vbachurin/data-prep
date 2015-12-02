package org.talend.dataprep.transformation.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static junit.framework.TestCase.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base tests for transformation service.
 */
public class BaseTests extends TransformationServiceBaseTests {

    @Test
    public void CORSHeaders() throws Exception {
        given().header("Origin", "fake.host.to.trigger.cors")
                .when()
                .post("/transform/JSON").then().header("Access-Control-Allow-Origin", "fake.host.to.trigger.cors");
        given().header("Origin", "fake.host.to.trigger.cors")
                .when()
                .post("/suggest/column").then().header("Access-Control-Allow-Origin", "fake.host.to.trigger.cors");
    }

    @Test
    public void shouldListExportTypes() throws Exception {
        String json = when().get("/export/formats").asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode types = mapper.readTree(json);

        assertTrue(types.isArray());

        List<String> actual = new ArrayList<>(types.size());
        for (int i = 0; i < types.size(); i++) {
            actual.add(types.get(i).get("id").asText());
        }

        List<String> expected = Arrays.asList("XLS", "CSV");
        Assertions.assertThat(actual).isNotNull().isNotEmpty().containsAll( expected );

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
