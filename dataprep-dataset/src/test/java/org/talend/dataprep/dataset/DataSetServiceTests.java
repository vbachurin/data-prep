package org.talend.dataprep.dataset;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.path.json.JsonPath.from;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.DataSetMetadata.Builder.metadata;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.spark.SparkContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetGovernance.Certification;
import org.talend.dataprep.api.dataset.DataSetLifecycle;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.schema.CSVFormatGuess;
import org.talend.dataprep.schema.Separator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataSetServiceTests {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    DataSetMetadataRepository dataSetMetadataRepository;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    DataSetContentStore contentStore;

    @Autowired(required = false)
    SparkContext sparkContext;

    private void assertQueueMessages(String dataSetId) throws Exception {
        // Wait for Spark jobs to finish
        if (sparkContext != null) {
            while (!sparkContext.jobProgressListener().activeJobs().isEmpty()) {
                // TODO Is there a better way to wait for all Spark jobs to complete?
                Thread.sleep(200);
            }
        }
        // Wait for queue messages
        waitForQueue(Destinations.CONTENT_ANALYSIS, dataSetId);
        waitForQueue(Destinations.QUALITY_ANALYSIS, dataSetId);
        waitForQueue(Destinations.SCHEMA_ANALYSIS, dataSetId);
        waitForQueue(Destinations.FORMAT_ANALYSIS, dataSetId);
        waitForQueue(Destinations.STATISTICS_ANALYSIS, dataSetId);
        // Asserts on metadata status
        DataSetMetadata metadata = dataSetMetadataRepository.get(dataSetId);
        DataSetLifecycle lifecycle = metadata.getLifecycle();
        assertThat(lifecycle.contentIndexed(), is(true));
        assertThat(lifecycle.schemaAnalyzed(), is(true));
        assertThat(lifecycle.qualityAnalyzed(), is(true));
    }

    private void waitForQueue(String queueName, String dataSetId) {
        // Wait for potential update still in progress
        final DistributedLock lock = dataSetMetadataRepository.createDatasetMetadataLock(dataSetId);
        lock.lock();
        lock.unlock();
        // Ensure queues are empty
        try {
            boolean isEmpty = false;
            while (!isEmpty) {
                isEmpty = jmsTemplate.browse(queueName, (session, browser) -> !browser.getEnumeration().hasMoreElements());
                if (!isEmpty) {
                    Thread.sleep(200);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
        dataSetMetadataRepository.clear();
        contentStore.clear();
    }

    @org.junit.After
    public void tearDown() {
        dataSetMetadataRepository.clear();
        contentStore.clear();
    }

    @Test
    public void CORSHeaders() throws Exception {
        when().get("/datasets").then().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Max-Age", "3600")
                .header("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
    }

    @Test
    public void list() throws Exception {
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[]"));
        // Adds 1 data set to store
        String id1 = UUID.randomUUID().toString();
        final DataSetMetadata metadata = metadata().id(id1).name("name1").author("anonymous").created(0)
                .formatGuessId(new CSVFormatGuess().getBeanId()).build();

        metadata.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER, Character.toString(new Separator().separator));
        dataSetMetadataRepository.add(metadata);

        String expected = "[{\"id\":\""
                + id1
                + "\",\"name\":\"name1\",\"records\":0,\"author\":\"anonymous\",\"nbLinesHeader\":0,\"nbLinesFooter\":0,\"created\":\"01-01-1970 00:00\"}]";

        InputStream content = when().get("/datasets").asInputStream();
        String contentAsString = IOUtils.toString(content);

        assertThat(contentAsString, sameJSONAs(expected).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());

        // Adds a new data set to store
        String id2 = UUID.randomUUID().toString();
        DataSetMetadata metadata2 = metadata().id(id2).name("name2").author("anonymous").created(0)
                .formatGuessId(new CSVFormatGuess().getBeanId()).build();
        metadata2.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER, Character.toString(new Separator().separator));
        dataSetMetadataRepository.add(metadata2);
        when().get("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("id");
        assertThat(ids, hasItems(id1, id2));
    }

    @Test
    public void create() throws Exception {
        int before = dataSetMetadataRepository.size();
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        int after = dataSetMetadataRepository.size();
        assertThat(after - before, is(1));
        assertQueueMessages(dataSetId);
    }

    @Test
    public void get() throws Exception {
        String expectedId = UUID.randomUUID().toString();
        DataSetMetadata dataSetMetadata = metadata().id(expectedId).formatGuessId(new CSVFormatGuess().getBeanId()).build();
        dataSetMetadata.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER,
                Character.toString(new Separator().separator));
        dataSetMetadataRepository.add(dataSetMetadata);
        contentStore.storeAsRaw(dataSetMetadata, new ByteArrayInputStream(new byte[0]));
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        int statusCode = when().get("/datasets/{id}/content", expectedId).getStatusCode();
        assertTrue(statusCode == HttpStatus.ACCEPTED.value() || statusCode == HttpStatus.OK.value());
    }

    @Test
    public void delete() throws Exception {
        String expectedId = UUID.randomUUID().toString();

        DataSetMetadata dataSetMetadata = metadata().id(expectedId).formatGuessId(new CSVFormatGuess().getBeanId()).build();

        dataSetMetadata.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER,
                Character.toString(new Separator().separator));
        dataSetMetadataRepository.add(dataSetMetadata);

        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        int before = dataSetMetadataRepository.size();
        when().delete("/datasets/{id}", expectedId).then().statusCode(HttpStatus.OK.value());
        int after = dataSetMetadataRepository.size();
        assertThat(before - after, is(1));
    }

    @Test
    public void update() throws Exception {
        String dataSetId = "123456";
        given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv"))).when()
                .put("/datasets/{id}/raw", dataSetId).then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("id");
        assertThat(ids, hasItem(dataSetId));
        assertQueueMessages(dataSetId);
    }

    @Test
    public void test1() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test1.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void test2() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada2.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test1.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void test3() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        // Update content
        given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada3.csv")))
                .queryParam("Content-Type", "text/csv").when().put("/datasets/" + dataSetId + "/raw");
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test2.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
        // Update name
        String expectedName = "testOfADataSetName";
        given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada3.csv")))
                .queryParam("Content-Type", "text/csv").when().put("/datasets/" + dataSetId + "/raw?name=" + expectedName);
        assertThat(dataSetMetadataRepository.get(dataSetId).getName(), is(expectedName));
    }

    /**
     * Test the import of a csv file with a really low separator coefficient variation.
     * 
     * @see org.talend.dataprep.schema.LineBasedFormatGuesser
     */
    @Test
    public void testLowSeparatorOccurencesInCSV() throws Exception {

        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("avengers.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();

        assertQueueMessages(dataSetId);

        InputStream expected = DataSetServiceTests.class.getResourceAsStream("avengers_expected.json");
        String datasetContent = given().when().get("/datasets/{id}/content?metadata=false&columns=true", dataSetId).asString();

        assertThat(datasetContent, sameJSONAsFile(expected));
    }

    @Test
    public void testQuotes() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("bands_quotes.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test_quotes.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void testQuotesAndCarriageReturn() throws Exception {
        String dataSetId = given()
                .body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("bands_quotes_and_carriage_return.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=false&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        InputStream expected = DataSetServiceTests.class.getResourceAsStream("test_quotes_and_carriage_return.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void nbLines() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=true&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        assertThat(contentAsString, sameJSONAs("{\"metadata\":{\"records\":2,\"nbLinesHeader\":1,\"nbLinesFooter\":0}}")
                .allowingExtraUnexpectedFields().allowingAnyArrayOrdering());
    }

    @Test
    public void nbLines2() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("t-shirt_100.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=true&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        InputStream expected = DataSetServiceTests.class.getResourceAsStream("t-shirt_100.csv.expected.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void nbLinesUpdate() throws Exception {
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        assertQueueMessages(dataSetId);
        InputStream content = when().get("/datasets/{id}/content?metadata=true&columns=false", dataSetId).asInputStream();
        String contentAsString = IOUtils.toString(content);

        assertThat(contentAsString, sameJSONAs("{\"metadata\":{\"records\":2,\"nbLinesHeader\":1,\"nbLinesFooter\":0}}")
                .allowingExtraUnexpectedFields().allowingAnyArrayOrdering());

        given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("t-shirt_100.csv")))
                .queryParam("Content-Type", "text/csv").when().put("/datasets/{id}/raw", dataSetId).asString();

        assertQueueMessages(dataSetId);

        content = when().get("/datasets/{id}/content?metadata=true&columns=false", dataSetId).asInputStream();
        contentAsString = IOUtils.toString(content);

        InputStream expected = DataSetServiceTests.class.getResourceAsStream("t-shirt_100.csv.expected.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void getMetadata() throws Exception {
        DataSetMetadata.Builder builder = DataSetMetadata.Builder.metadata().id("1234");
        builder.row(ColumnMetadata.Builder.column().empty(0).invalid(0).valid(0).name("id").type(Type.STRING))//
                .created(0)//
                .name("name")//
                .author("author")//
                .footerSize(0) //
                .headerSize(1) //
                .qualityAnalyzed(true) //
                .schemaAnalyzed(true) //
                .formatGuessId(new CSVFormatGuess().getBeanId()) //
                .mediaType("text/csv");

        DataSetMetadata metadata = builder.build();
        metadata.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER, Character.toString(new Separator().separator));

        dataSetMetadataRepository.add(metadata);
        String contentAsString = when().get("/datasets/{id}/metadata", "1234").asString();
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("metadata1.json");
        assertThat(contentAsString, sameJSONAsFile(expected));
    }

    @Test
    public void getEmptyMetadata() throws Exception {
        DataSetMetadata metadata = dataSetMetadataRepository.get("9876");
        assertNull(metadata);
        int statusCode = when().get("/datasets/{id}/metadata", "9876").statusCode();
        assertThat(statusCode, is(HttpServletResponse.SC_NO_CONTENT));
    }

    /**
     * Check that the error listing service returns a list parsable of error codes. The content is not checked
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldListErrors() throws Exception {
        String errors = when().get("/datasets/errors").asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(errors);

        assertTrue(rootNode.isArray());
        assertTrue(rootNode.size() > 0);
        for (final JsonNode errorCode : rootNode) {
            assertTrue(errorCode.has("code"));
            assertTrue(errorCode.has("http-status-code"));
        }
    }

    @Test
    public void testAskCertification() throws Exception {
        int before = dataSetMetadataRepository.size();
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        int after = dataSetMetadataRepository.size();
        assertThat(after - before, is(1));
        assertQueueMessages(dataSetId);

        DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        int originalNbLines = dataSetMetadata.getContent().getNbRecords(); // to check later if no modified
        assertEquals(Certification.NONE, dataSetMetadata.getGovernance().getCertificationStep());

        when().put("/datasets/{id}/processcertification", dataSetId).then().statusCode(HttpStatus.OK.value());
        dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        assertEquals(Certification.PENDING, dataSetMetadata.getGovernance().getCertificationStep());
        assertEquals(originalNbLines, dataSetMetadata.getContent().getNbRecords());
    }

    @Test
    public void testCertify() throws Exception {
        int before = dataSetMetadataRepository.size();
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        int after = dataSetMetadataRepository.size();
        assertThat(after - before, is(1));
        assertQueueMessages(dataSetId);

        DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        int originalNbLines = dataSetMetadata.getContent().getNbRecords(); // to check later if no modified
        assertEquals(Certification.NONE, dataSetMetadata.getGovernance().getCertificationStep());

        when().put("/datasets/{id}/processcertification", dataSetId).then().statusCode(HttpStatus.OK.value());
        when().put("/datasets/{id}/processcertification", dataSetId).then().statusCode(HttpStatus.OK.value());
        dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        assertEquals(Certification.CERTIFIED, dataSetMetadata.getGovernance().getCertificationStep());
        assertEquals(originalNbLines, dataSetMetadata.getContent().getNbRecords());
    }

}
