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

    /**
     * After TDP-1184 fix, there is a problem on preview (regression?).
     * Use case is:
     * - delete a column (lastname here)
     * - add new columns (with split on city here)
     * - preview an action on the first new column (uppercase on 0000 here)
     *
     * -> lastname is still on the preview data for lines 4 & 6. it is absent (which is what we expect) only for the first line!
     */
    @Test
    public void test_TDP_1184() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(this.getClass().getResourceAsStream("../preview/input.json"));
        final String expected = IOUtils.toString(this.getClass().getResourceAsStream("../preview/expected_output_TDP_1184.json"));

        final String oldActions = getTransformation_TDP_1184_step_1();
        final String newActions = getTransformation_TDP_1184_step_2();
        final String indexes = "[1,4,6]";

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
        assertEquals(expected, response, true);
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
        assertEquals("{\"createdColumns\":[\"0000\"]}", response, false);
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

    private String getTransformation_TDP_1184_step_1() {
        return "{\"actions\": [ { \"action\": \"delete_column\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"split\", \"parameters\":{ \"column_id\": \"city\", \"scope\": \"column\", \"separator\":\" \", \"limit\":\"2\" } } ]}";
    }

    private String getTransformation_TDP_1184_step_2() {
        return "{\"actions\": [ { \"action\": \"delete_column\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"split\", \"parameters\":{ \"column_id\": \"city\", \"scope\": \"column\", \"separator\":\" \", \"limit\":\"2\" } }, { \"action\": \"uppercase\",\"parameters\":{ \"column_id\": \"0000\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformationWithNewColumn() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"copy\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformationWithoutNewColumn() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"lowercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"uppercase\",\"parameters\":{ \"column_id\": \"firstname\", \"scope\": \"column\" } }, { \"action\": \"delete_on_value\", \"parameters\":{ \"column_id\":\"city\", \"value\": {\"token\":\"Columbia\", \"operator\":\"equals\"}, \"scope\": \"column\" } } ]}";
    }

}
