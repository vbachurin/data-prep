package org.talend.dataprep.transformation.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.dataprep.transformation.Application;

/**
 * Integration tests on suggestions.
 */
public class SuggestionTests extends TransformationServiceBaseTests {

    @Test
    public void dataSetSuggest() throws Exception {
        // given
        final String dataSetMetadata = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/dataset_metadata.json"));

        // when
        final String response = given() //
                .contentType(JSON) //
                .body(dataSetMetadata) //
                .when() //
                .post("/suggest/dataset") //
                .asString();

        // then
        assertEquals("[]", response, false);
    }

    @Test
    public void emptyColumnSuggest() throws Exception {
        // when
        final String response = given() //
                .contentType(JSON) //
                .body("") //
                .when() //
                .post("/suggest/column") //
                .asString();

        // then
        assertEquals("[]", response, false);
    }

    @Test
    public void stringColumnSuggest() throws Exception {
        // given
        final String columnMetadata = IOUtils.toString(Application.class.getResourceAsStream("suggestions/string_column.json"));
        final String expectedSuggestions = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/string_col" + "umn_suggestions.json"));

        // when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        // then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void floatColumnSuggest() throws Exception {
        // given
        final String columnMetadata = IOUtils.toString(Application.class.getResourceAsStream("suggestions/float_column.json"));
        final String expectedSuggestions = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/float_column_suggestions.json"));

        // when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        // then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void integerColumnSuggest() throws Exception {
        // given
        final String columnMetadata = IOUtils.toString(Application.class.getResourceAsStream("suggestions/integer_column.json"));
        final String expectedSuggestions = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/integer_column_suggestions.json"));

        // when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        // then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void booleanColumnSuggest() throws Exception {
        // given
        final String columnMetadata = IOUtils.toString(Application.class.getResourceAsStream("suggestions/boolean_column.json"));
        final String expectedSuggestions = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/boolean_column_suggestions.json"));

        // when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        // then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void dateColumnSuggest() throws Exception {
        // given
        final String columnMetadata = IOUtils.toString(Application.class.getResourceAsStream("suggestions/date_column.json"));
        final String expectedSuggestions = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/date_column_suggestions.json"));

        // when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        // then
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void dateColumnSuggestWithStringType() throws Exception {
        // given
        final String columnMetadata = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/date_column_string_type.json"));
        final String expectedSuggestions = IOUtils
                .toString(Application.class.getResourceAsStream("suggestions/date_column_string_type_suggestions.json"));

        // when
        final String response = given() //
                .contentType(JSON) //
                .body(columnMetadata) //
                .when() //
                .post("/suggest/column") //
                .asString();

        // then
        assertEquals(expectedSuggestions, response, false);
    }

}
