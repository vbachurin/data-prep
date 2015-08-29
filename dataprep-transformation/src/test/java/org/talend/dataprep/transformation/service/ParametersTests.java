package org.talend.dataprep.transformation.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.response.Response;

/**
 * Parameters integration tests.
 */
public class ParametersTests extends TransformationServiceBaseTests {

    @Test
    public void testDynamicParams_should_return_textclustering_dynamic_params() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(this.getClass().getResourceAsStream("../parameters/dataset.json"));
        final String expectedParameters = IOUtils
                .toString(this.getClass().getResourceAsStream("../parameters/expected_cluster_params_soundex.json"));

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

    @Test
    public void testDynamicParams_should_throw_error() throws Exception {
        // when
        final Response post = given() //
                .contentType(JSON) //
                .body("json") //
                .when() //
                .post("/transform/suggest/{action}/params?columnId=uglystate", "unkownaction");

        Assert.assertEquals(404, post.getStatusCode());

    }

}
