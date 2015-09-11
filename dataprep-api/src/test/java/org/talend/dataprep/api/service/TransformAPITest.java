package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.jayway.restassured.response.Response;

/**
 * Unit test for Transformation API.
 */
public class TransformAPITest extends ApiServiceTestBase {



    @Test
    public void testSuggestActionParams_should_return_dynamic_params_with_dataset() throws Exception {
        // given
        final String dataSetId = createDataset("transformation/cluster_dataset.csv", "testClustering", "text/csv");
        final String expectedClusterParameters = IOUtils.toString(PreparationAPITest.class
                .getResourceAsStream("transformation/expected_cluster_params_soundex.json"));

        // when
        final String actualClusterParameters = given().formParam("datasetId", dataSetId).formParam("columnId", "0001")
                .when().get("/api/transform/suggest/textclustering/params").asString();

        // then
        assertThat(actualClusterParameters, sameJSONAs(expectedClusterParameters).allowingAnyArrayOrdering());
    }

    @Test
    public void testSuggestActionParams_should_return_dynamic_params_with_preparation_head() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("transformation/cluster_dataset.csv", "testClustering", "text/csv");
        final String expectedClusterParameters = IOUtils.toString(PreparationAPITest.class
                .getResourceAsStream("transformation/expected_cluster_params_soundex.json"));

        // when
        final String actualClusterParameters = given().formParam("preparationId", preparationId)
                .formParam("columnId", "0001").when().get("/api/transform/suggest/textclustering/params").asString();

        // then
        assertThat(actualClusterParameters, sameJSONAs(expectedClusterParameters).allowingAnyArrayOrdering());
    }

    @Test
    public void testSuggestActionParams_should_return_dynamic_params_with_preparation_step() throws Exception {
        // given
        final String preparationId = createPreparationFromFile("transformation/cluster_dataset.csv", "testClustering", "text/csv");
        applyActionFromFile(preparationId, "export/upper_case_firstname.json");
        applyActionFromFile(preparationId, "export/upper_case_lastname.json");

        final List<String> steps = given().get("/api/preparations/{preparation}/details", preparationId).jsonPath()
                .getList("steps");

        final String expectedClusterParameters = IOUtils.toString(PreparationAPITest.class
                .getResourceAsStream("transformation/expected_cluster_params_with_steps.json"));

        // when
        final String actualClusterParameters = given()
                .formParam("preparationId", preparationId)
                .formParam("stepId", steps.get(1))
                .formParam("columnId", "0001")
                .when()
                .get("/api/transform/suggest/textclustering/params")
                .asString();

        // then (actions have normalized all cluster values, so no more clusters to be returned).
        assertThat(actualClusterParameters, sameJSONAs(expectedClusterParameters).allowingAnyArrayOrdering());
    }

    @Test
    public void testSuggestActionParams_should_return_400_with_no_preparationId_and_no_datasetId() throws Exception {
        // when
        final Response response = given().formParam("columnId", "0001").when()
                .get("/api/transform/suggest/textclustering/params");

        // then
        response.then().statusCode(400);
    }

}
