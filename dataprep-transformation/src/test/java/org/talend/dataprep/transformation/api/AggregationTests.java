package org.talend.dataprep.transformation.api;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.dataprep.transformation.Application;

import com.jayway.restassured.response.Response;

/**
 * Integration tests for aggregation.
 */
public class AggregationTests extends TransformationServiceBaseTests {

    @Test
    public void invalidOperation() throws IOException {
        // given
        String invalidOperation = "hqsmkghfqg";

        // when
        final Response response = given()//
                .multiPart("parameters", invalidOperation)//
                .multiPart("content", "test to write")//
                .when().post("/aggregate");

        // then
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    public void shouldAggregate() throws IOException {

        // given
        final String actions = IOUtils.toString(Application.class.getResourceAsStream("aggregation/aggregate_actions.json"));

        // when
        final String actual = given()//
                .multiPart("parameters", actions)//
                .multiPart("content", "test to write")//
                .when().post("/aggregate")//
                .asString();

        // then
        assertEquals(actual, "TDD development");
    }

}
