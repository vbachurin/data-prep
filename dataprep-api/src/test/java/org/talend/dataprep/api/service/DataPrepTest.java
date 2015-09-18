package org.talend.dataprep.api.service;

import com.jayway.restassured.http.ContentType;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class DataPrepTest {

    protected String createDataset(final String file, final String name, final String type) throws IOException {
        final String datasetContent = IOUtils.toString(PreparationAPITest.class.getResourceAsStream(file));
        final String dataSetId = given().contentType(ContentType.JSON).body(datasetContent).queryParam("Content-Type", type)
                .when().post("/api/datasets?name={name}", name).asString();
        assertNotNull(dataSetId);
        assertThat(dataSetId, not(""));

        return dataSetId;
    }

    protected String createPreparationFromDataset(final String dataSetId, final String name) throws IOException {
        final String preparationId = given().contentType(ContentType.JSON)
                .body("{ \"name\": \"" + name + "\", \"dataSetId\": \"" + dataSetId + "\"}").when().post("/api/preparations")
                .asString();
        assertThat(preparationId, notNullValue());
        assertThat(preparationId, not(""));

        return preparationId;
    }

    protected String createPreparationFromFile(final String file, final String name, final String type) throws IOException {
        final String dataSetId = createDataset(file, "testDataset", type);
        return createPreparationFromDataset(dataSetId, name);
    }

    protected void applyActionFromFile(final String preparationId, final String actionFile) throws IOException {
        final String action = IOUtils.toString(PreparationAPITest.class.getResourceAsStream(actionFile));
        applyAction(preparationId, action);
    }

    protected void applyAction(final String preparationId, final String action) throws IOException {
        given().contentType(ContentType.JSON).body(action).when().post("/api/preparations/{id}/actions", preparationId).then()
                .statusCode(is(200));
    }
}
