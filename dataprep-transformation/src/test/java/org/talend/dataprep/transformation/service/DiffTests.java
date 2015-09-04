package org.talend.dataprep.transformation.service;

import static com.jayway.restassured.RestAssured.given;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.jayway.restassured.response.Response;

/**
 * Diff integration tests.
 */
public class DiffTests extends TransformationServiceBaseTests {

    @Test
    public void previewDiff() throws Exception {
        // given
        final String datasetContent = IOUtils.toString(this.getClass().getResourceAsStream("../preview/input.json"));
        final String expected = IOUtils.toString(this.getClass().getResourceAsStream("../preview/expected_output.json"));

        final String oldActions = getSingleTransformation();
        final String newActions = getMultipleTransformation();
        final String indexes = "[2,4,6]";

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

    private String getSingleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } } ]}";
    }

    private String getMultipleTransformation() {
        return "{\"actions\": [ { \"action\": \"uppercase\", \"parameters\":{ \"column_id\": \"lastname\", \"scope\": \"column\" } }, { \"action\": \"uppercase\",\"parameters\":{ \"column_id\": \"firstname\", \"scope\": \"column\" } }, { \"action\": \"delete_on_value\", \"parameters\":{ \"column_id\":\"city\", \"value\": \"Columbia\", \"scope\": \"column\" } } ]}";
    }

}
