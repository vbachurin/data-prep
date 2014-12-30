package org.talend.dataprep.dataset;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.dataset.store.DataSet;
import org.talend.dataprep.dataset.store.DataSetRepository;

import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class DataSetServiceTests {

    @Autowired
    DataSetRepository dataSetRepository;

    @Value("${local.server.port}")
    public int                    port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @org.junit.After
    public void tearDown() {
        dataSetRepository.clear();
    }

    @Test
    public void testList() {
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[]"));
        // Adds 1 data set to store
        String id1 = UUID.randomUUID().toString();
        dataSetRepository.add(new DataSet(id1));
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[\"" + id1 + "\"]"));
        // Adds a new data set to store
        String id2 = UUID.randomUUID().toString();
        dataSetRepository.add(new DataSet(id2));
        when().get("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids, hasItems(id1, id2));
    }

    @Test
    public void testCreate() {
        int before = dataSetRepository.size();
        when().post("/datasets").then().statusCode(HttpStatus.OK.value());
        int after = dataSetRepository.size();
        assertThat(after - before, is(1));
    }

    @Test
    public void testGet() {
        when().post("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        String expectedId = ids.get(0);
        when().get("/datasets/{id}", expectedId).then().statusCode(HttpStatus.OK.value());
        String content = when().get("/datasets/{id}", expectedId).asString();
        assertThat(content, equalTo("[\"" + expectedId + "\"]"));
    }

    @Test
    public void testDelete() throws Exception {
        when().post("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids.size(), is(1));
        String expectedId = ids.get(0);
        int before = dataSetRepository.size();
        when().delete("/datasets/{id}", expectedId).then().statusCode(HttpStatus.OK.value());
        int after = dataSetRepository.size();
        assertThat(before - after, is(1));
    }

    @Test
    public void testUpdate() throws Exception {
        when().put("/datasets/{id}", "123456").then().statusCode(HttpStatus.OK.value());
        when().post("/datasets").then().statusCode(HttpStatus.OK.value());
        List<String> ids = from(when().get("/datasets").asString()).get("");
        assertThat(ids, hasItem("123456"));
    }
}
