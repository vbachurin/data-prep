package org.talend.dataprep.transformation.service;

import static com.jayway.restassured.RestAssured.given;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.response.Response;

/**
 * Integration tests on actions.
 */
public class NewTransformTests extends TransformationServiceBaseTests {

    @Test
    public void noAction() throws Exception {
        // given
        String dataSetId = createDataset("input_dataset.csv", "uppercase", "text/csv");
        String preparationId = createEmptyPreparationFromDataset(dataSetId, "uppercase prep");

        // when
        String transformedContent = given() //
                .when() //
                .get("/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", preparationId, dataSetId, "JSON") //
                .asString();

        // then
        String expectedContent = IOUtils.toString(this.getClass().getResourceAsStream("no_action_expected.json"));
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void testUnknownFormat() throws Exception {
        // given
        String dataSetId = createDataset("input_dataset.csv", "unknown format", "text/csv");

        // when
        final Response response = given() //
                .when() //
                .get("/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", //
                        "no need for preparation id", //
                        dataSetId, //
                        "Gloubi-boulga"); // Casimir rules !

        // then
        Assert.assertEquals(500, response.getStatusCode());
        Assert.assertTrue(response.asString().contains("OUTPUT_TYPE_NOT_SUPPORTED"));
    }


    @Test
    public void testUnknownDataSet() throws Exception {
        // when
        final Response response = given() //
                .when() //
                .get("/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", "no need for preparation id",
                        "unknown_dataset_id", "JSON");

        // then
        Assert.assertEquals(500, response.getStatusCode());
        Assert.assertTrue(response.asString().contains("UNABLE_TO_READ_DATASET_CONTENT"));
    }

    @Test
    public void testUnknownPreparation() throws Exception {
        // given
        String dataSetId = createDataset("input_dataset.csv", "no preparation for this one", "text/csv");

        // when
        final Response response = given() //
                .when() //
                .get("/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", "no_preparation_id", dataSetId, "JSON");

        // then
        Assert.assertEquals(500, response.getStatusCode());
        Assert.assertTrue(response.asString().contains("UNABLE_TO_READ_PREPARATION"));
    }

    @Test
    public void uppercaseAction() throws Exception {
        // given
        String dataSetId = createDataset("input_dataset.csv", "uppercase", "text/csv");
        String preparationId = createEmptyPreparationFromDataset(dataSetId, "uppercase prep");
        applyActionFromFile(preparationId, "uppercase_action.json");

        // when
        String transformedContent = given() //
                .expect().statusCode(200).log().ifError()//
                .when() //
                .get("/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", preparationId, dataSetId, "JSON") //
                .asString();

        // then
        String expectedContent = IOUtils.toString(this.getClass().getResourceAsStream("uppercase_expected.json"));
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void lowercaseActionWithFilter() throws Exception {
        // given
        String dataSetId = createDataset("input_dataset.csv", "lowercase", "text/csv");
        String preparationId = createEmptyPreparationFromDataset(dataSetId, "lowercase prep");
        applyActionFromFile(preparationId, "lowercase_filtered_action.json");

        // when
        String transformedContent = given() //
                .expect().statusCode(200).log().ifError()//
                .when() //
                .get("/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", preparationId, dataSetId, "JSON") //
                .asString();

        // then
        String expectedContent = IOUtils.toString(this.getClass().getResourceAsStream("lowercase_filtered_expected.json"));
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void uppercaseActionWithSample() throws Exception {
        // given
        String dataSetId = createDataset("input_dataset.csv", "uppercase", "text/csv");
        String preparationId = createEmptyPreparationFromDataset(dataSetId, "uppercase prep");
        applyActionFromFile(preparationId, "uppercase_action.json");

        // when
        String transformedContent = given() //
                .expect().statusCode(200).log().ifError()//
                .when() //
                .get("/apply/preparation/{preparationId}/dataset/{datasetId}/{format}?sample={sample}", preparationId, dataSetId,
                        "JSON", 6) //
                .asString();

        // then
        String expectedContent = IOUtils.toString(this.getClass().getResourceAsStream("uppercase_expected_with_sample.json"));
        assertEquals(expectedContent, transformedContent, false);
    }

}
