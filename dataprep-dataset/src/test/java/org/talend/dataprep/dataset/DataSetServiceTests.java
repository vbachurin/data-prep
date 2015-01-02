package org.talend.dataprep.dataset;

import com.jayway.restassured.RestAssured;
import org.apache.commons.io.IOUtils;
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
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.objects.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataSetServiceTests {

    @Value("${local.server.port}")
    public int        port;

    @Autowired
    JmsTemplate       jmsTemplate;

    @Autowired
    DataSetMetadataRepository dataSetMetadataRepository;

    private static void assertQueueMessages(String dataSetId, JmsTemplate template) throws JMSException {
        // Asserts on messages that should posted to queues after insert of a new data set.
        Message indexMessage = template.receive(Destinations.INDEXING_DESTINATION);
        assertThat(indexMessage, notNullValue());
        assertThat(indexMessage.getStringProperty("dataset.id"), is(dataSetId));
        Message schemaAnalysisMessage = template.receive(Destinations.SCHEMA_ANALYSIS_DESTINATION);
        assertThat(schemaAnalysisMessage, notNullValue());
        assertThat(schemaAnalysisMessage.getStringProperty("dataset.id"), is(dataSetId));
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @org.junit.After
    public void tearDown() {
        dataSetMetadataRepository.clear();
    }

    @Test
    public void testList() throws Exception {
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[]"));
        // Adds 1 data set to store
        String id1 = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(new DataSetMetadata(id1));
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[\"" + id1 + "\"]"));
        // Adds a new data set to store
        String id2 = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(new DataSetMetadata(id2));
        when().get("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids, hasItems(id1, id2));
    }

    @Test
    public void testCreate() throws Exception {
        int before = dataSetMetadataRepository.size();
        String dataSetId = given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
        int after = dataSetMetadataRepository.size();
        assertThat(after - before, is(1));
        assertQueueMessages(dataSetId, jmsTemplate);
    }

    @Test
    public void testGet() throws Exception {
        String expectedId = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(new DataSetMetadata(expectedId));
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        when().get("/datasets/{id}", expectedId).then().statusCode(HttpStatus.OK.value());
        String content = when().get("/datasets/{id}", expectedId).asString();
        assertThat(content, equalTo("[\"" + expectedId + "\"]"));
    }

    @Test
    public void testDelete() throws Exception {
        String expectedId = UUID.randomUUID().toString();
        dataSetMetadataRepository.add(new DataSetMetadata(expectedId));
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        int before = dataSetMetadataRepository.size();
        when().delete("/datasets/{id}", expectedId).then().statusCode(HttpStatus.OK.value());
        int after = dataSetMetadataRepository.size();
        assertThat(before - after, is(1));
    }

    @Test
    public void testUpdate() throws Exception {
        String dataSetId = "123456";
        when().put("/datasets/{id}", dataSetId).then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids, hasItem(dataSetId));
        assertQueueMessages(dataSetId, jmsTemplate);
    }
}
