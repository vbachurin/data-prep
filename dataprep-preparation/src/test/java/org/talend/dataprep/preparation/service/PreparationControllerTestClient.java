package org.talend.dataprep.preparation.service;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import org.talend.dataprep.api.preparation.AppendStep;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Collections.singletonList;

public class PreparationControllerTestClient {

    static {
        RestAssured.defaultParser = Parser.JSON;
    }

    public static void appendStepsToPrep(final String preparationId, AppendStep stepToAppend) {
        given().body(singletonList(stepToAppend))
                .contentType(ContentType.JSON)
                .when()
                .post("/preparations/{id}/actions", preparationId)
                .then()
                .statusCode(200)
                .log().ifError();
    }
}
