package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.junit.Assert.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.*;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.Application;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataPreparationAPITest extends TestCase {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    private DataPreparationAPI apiService;

    @Autowired
    DataSetMetadataRepository dataSetMetadataRepository;

    @Autowired
    DataSetContentStore contentStore;

    @Before
    public void setUp() {
        RestAssured.port = port;
        apiService.setDataSetServiceURL("http://localhost:" + port + "/datasets");
        apiService.setTransformationServiceURL("http://localhost:" + port + "/transform");
    }

    @org.junit.After
    public void tearDown() {
        dataSetMetadataRepository.clear();
        contentStore.clear();
    }

    @Test
    public void testCORSHeaders() throws Exception {
        when().post("/transform").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600").header("Access-Control-Allow-Headers", "x-requested-with");
    }

    @Test
    public void testDataSetUpdate() throws Exception {
        given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().put("/api/datasets/123456789").asString();
        String dataSetContent = when().get("/api/datasets/123456789?metadata=false&columns=false").asString();
        assertNotNull(dataSetContent);
    }

    @Test
    public void testTransformNoOp() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        String expectedContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1_expected.json"));
        String transformed = when().post("/api/transform/" + dataSetId + "/").asString();
        assertEquals(expectedContent, transformed);
    }

    @Test
    public void testTransformOneAction() throws Exception {
        String actions = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action1.json"));
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test2.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        assertFalse(dataSetId.equals(StringUtils.EMPTY));
        String expectedContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test2_expected.json"));
        String transformed = when().post("/api/transform/" + dataSetId + "/?actions=" + actions).asString();
        assertEquals(expectedContent, transformed);
    }

    @Test
    public void testTransformTwoActions() throws Exception {
        String actions = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("action2.json"));
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test3.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        String expectedContent = IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test3_expected.json"));
        String transformed = when().post("/api/transform/" + dataSetId + "/?actions=" + actions).asString();
        assertEquals(expectedContent, transformed);
    }

    @Test
    public void testDataSetList() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        String list = when().get("/api/datasets").asString();
        assertTrue(list.contains(dataSetId));
    }

    @Test
    public void testDataSetDelete() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("test1.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets").asString();
        assertNotNull(dataSetId);
        String list = when().get("/api/datasets").asString();
        assertTrue(list.contains(dataSetId));
        when().delete("/api/datasets/" + dataSetId).asString();
        list = when().get("/api/datasets").asString();
        assertEquals("[]", list);
    }

    /**
     * Utilities method to assert that an expected json contained in a file matches a result.
     * 
     * @param fileNameExpected the name of the file that contains the expected json, must be in this package ressources
     * @return a SameJSONAs to use like in assertThat(contentAsString, sameJSONAsFile("t-shirt_100.csv.expected.json"));
     */
    private SameJSONAs<? super String> sameJSONAsFile(String fileNameExpected) throws Exception {
        InputStream expected = DataPreparationAPITest.class.getResourceAsStream(fileNameExpected);
        assertNotNull(expected);
        return sameJSONAs(IOUtils.toString(expected)).allowingExtraUnexpectedFields().allowingAnyArrayOrdering();
    }

    @Test
    public void testDataSetCreate() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataPreparationAPITest.class.getResourceAsStream("testCreate.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/api/datasets?name={name}", "tagada").asString();
        InputStream content = when().get("/api/datasets/{id}?metadata=true&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        assertThat(contentAsString, sameJSONAsFile("testCreate_expected.json"));
    }

}