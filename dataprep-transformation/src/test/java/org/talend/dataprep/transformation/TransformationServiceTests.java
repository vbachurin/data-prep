package org.talend.dataprep.transformation;

import com.jayway.restassured.RestAssured;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TransformationServiceTests {

    @Value("${local.server.port}")
    public int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testCORSHeaders() throws Exception {
        when().post("/transform").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with");
    }

    @Test
    public void emptyTransformation() {
        when().post("/transform").then().statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testNoAction() throws Exception {
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String transformedContent = given().body(initialContent).queryParam("Content-Type", "text/json").when()
                .post("/transform").asString();
        assertEquals(initialContent, transformedContent, false);
    }

    @Test
    public void testAction1() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("action1.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1_action1.json"));
        String transformedContent = given().body(initialContent).queryParam("Content-Type", "text/json").when()
                .post("/transform?actions=" + actions).asString();
        assertEquals(expectedContent, transformedContent, false);
    }

    @Test
    public void testAction2() throws Exception {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("action2.json"));
        String initialContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1.json"));
        String expectedContent = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("test1_action2.json"));
        String transformedContent = given().body(initialContent).queryParam("Content-Type", "text/json").when()
                .post("/transform?actions=" + actions).asString();
        assertEquals(expectedContent, transformedContent, false);
    }
}
