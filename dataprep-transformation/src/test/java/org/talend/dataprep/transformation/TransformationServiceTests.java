package org.talend.dataprep.transformation;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TransformationServiceTests {

    @Value("${local.server.port}")
    public int port;

    private static String encode(String actions) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(actions.getBytes("UTF-8"));
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void CORSHeaders() throws Exception {
        when().post("/transform").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600").header("Access-Control-Allow-Headers", "x-requested-with");
        when().post("/suggest/column").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600").header("Access-Control-Allow-Headers", "x-requested-with");
    }

    @Test
    public void emptyTransformation() {
        when().post("/transform").then().statusCode(HttpStatus.OK.value());
    }

    @Test
    public void noAction() throws Exception {
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform").asString();
        assertEquals(initialContent, transformedContent, false);
    }

    @Test
    public void action1() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("action1.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1_action1.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void action2() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("action2.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1_action2.json"));
        String transformedContent = given().contentType(ContentType.JSON).body(initialContent).when()
                .post("/transform?actions=" + encode(actions)).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void action3() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("action1.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test2.json"));
        given().contentType(ContentType.JSON).body(initialContent).when().post("/transform?actions=" + encode(actions))
                .asString();
    }

    @Test
    public void emptyColumnSuggest() throws Exception {
        String response = given().contentType(ContentType.JSON).body("").when().post("/suggest/column").asString();
        assertEquals("[]", response, false);
    }

    @Test
    public void stringColumnSuggest() throws Exception {
        String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("column1.json"));
        String expectedSuggestions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("suggest1.json"));
        Response post =  given().contentType(ContentType.JSON).body(columnMetadata).when().post("/suggest/column");
        String response = post.asString();
        assertEquals(expectedSuggestions, response, false);
    }

    @Test
    public void numericColumnSuggest() throws Exception {
        String columnMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("column2.json"));
        String response = given().contentType(ContentType.JSON).body(columnMetadata).when().post("/suggest/column").asString();
        assertEquals("[]", response, false);
    }

    @Test
    public void dataSetSuggest() throws Exception {
        String dataSetMetadata = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("metadata1.json"));
        String response = given().contentType(ContentType.JSON).body(dataSetMetadata).when().post("/suggest/dataset").asString();
        assertEquals("[]", response, false);
    }

}
