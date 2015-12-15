package org.talend.dataprep.transformation.service;

import static com.jayway.restassured.RestAssured.given;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Diff integration tests.
 */
public class DiffTests extends TransformationServiceBaseTests {

    @Test
    public void should_return_preview() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(this.getClass().getResourceAsStream("../preview/input.json"));
        final String expected = IOUtils.toString(this.getClass().getResourceAsStream("../preview/expected_output.json"));

        final String oldActions = getSingleTransformation();
        final String newActions = getMultipleTransformation();
        final String indexes = "[2,4,6]";

        // when
        final String response = given() //
                .multiPart("oldActions", oldActions) //
                .multiPart("newActions", newActions) //
                .multiPart("indexes", indexes) //
                .multiPart("content", datasetContent) //
                .when() //
                .post("/transform/preview")
                .asString();

        // then
        assertEquals(expected, response, false);
    }

    @Test
    public void should_return_created_columns() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(this.getClass().getResourceAsStream("../preview/input.json"));

        final String parentAction = getSingleTransformation();
        final String newStepAction = getMultipleTransformationWithNewColumn();

        // when
        final String response = given() //
                .multiPart("referenceActions", parentAction) //
                .multiPart("diffActions", newStepAction) //
                .multiPart("content", datasetContent) //
                .when() //
                .post("/transform/diff/metadata")
                .asString();

        // then
        assertEquals("{\"createdColumns\":[\"0003\"]}", response, false);
    }

    @Test
    public void should_return_empty_array_when_step_doesnt_create_columns() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(this.getClass().getResourceAsStream("../preview/input.json"));

        final String parentAction = getSingleTransformation();
        final String newStepAction = getMultipleTransformationWithoutNewColumn();

        // when
        final String response = given() //
                .multiPart("referenceActions", parentAction) //
                .multiPart("diffActions", newStepAction) //
                .multiPart("content", datasetContent) //
                .when() //
                .post("/transform/diff/metadata")
                .asString();

        // then
        assertEquals("{\"createdColumns\":[]}", response, false);
    }

    private String getSingleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformationWithNewColumn() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"copy\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformationWithoutNewColumn() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"lowercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"uppercase\",\"parameters\":{ \"column_id\": \"firstname\", \"scope\": \"column\" } }, { \"action\": \"delete_on_value\", \"parameters\":{ \"column_id\":\"city\", \"value\": \"Columbia\", \"scope\": \"column\" } } ]}";
    }

}
