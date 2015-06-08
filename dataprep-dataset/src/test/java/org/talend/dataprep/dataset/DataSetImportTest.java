package org.talend.dataprep.dataset;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.service.analysis.SynchronousDataSetAnalyzer;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataSetImportTest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    DataSetMetadataRepository dataSetMetadataRepository;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    ConfigurableApplicationContext context;

    @BeforeClass
    public static void enter() {
        // Set pause in analysis
        System.setProperty("DataSetImportTest.PausedAnalyzer", "1");
    }

    @AfterClass
    public static void leave() {
        // Overrides connection information with random port value
        System.setProperty("DataSetImportTest.PausedAnalyzer", "0");
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
        dataSetMetadataRepository.clear();
    }

    @Test
    public void testImportStatus() throws Exception {
        int before = dataSetMetadataRepository.size();
        // Create a data set (asynchronously)
        Runnable creation = () -> {
            try {
                given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                        .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
                int after = dataSetMetadataRepository.size();
                assertThat(after - before, is(1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        Thread creationThread = new Thread(creation);
        creationThread.start();
        // Wait for creation of data set object
        while (!dataSetMetadataRepository.list().iterator().hasNext()) {
            TimeUnit.MILLISECONDS.sleep(20);
        }
        // Data set should show as importing
        final Iterable<DataSetMetadata> list = dataSetMetadataRepository.list();
        final Iterator<DataSetMetadata> iterator = list.iterator();
        assertThat(iterator.hasNext(), is(true));
        final DataSetMetadata next = iterator.next();
        assertThat(next.getLifecycle().importing(), is(true));
        // Asserts when import is done
        creationThread.join(); // Wait until creation is done (i.e. end of thread since creation is a blocking
                               // operation).
        assertThat(next.getLifecycle().importing(), is(false));
        assertThat(next.getLifecycle().schemaAnalyzed(), is(true));
    }

    @Test
    public void testListImported() throws Exception {
        int before = dataSetMetadataRepository.size();
        // Create a data set (asynchronously)
        Runnable creation = () -> {
            try {
                given().body(IOUtils.toString(DataSetServiceTests.class.getResourceAsStream("tagada.csv")))
                        .queryParam("Content-Type", "text/csv").when().post("/datasets").asString();
                int after = dataSetMetadataRepository.size();
                assertThat(after - before, is(1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        Thread creationThread = new Thread(creation);
        creationThread.start();
        // Wait for creation of data set object
        while (!dataSetMetadataRepository.list().iterator().hasNext()) {
            TimeUnit.MILLISECONDS.sleep(20);
        }
        // Find data set being imported...
        final Iterable<DataSetMetadata> list = dataSetMetadataRepository.list();
        final Iterator<DataSetMetadata> iterator = list.iterator();
        assertThat(iterator.hasNext(), is(true));
        final DataSetMetadata next = iterator.next();
        assertThat(next.getLifecycle().importing(), is(true));
        // ... list operation should *not* return data set being imported...
        when().get("/datasets").then().statusCode(HttpStatus.OK.value()).body(equalTo("[]"));
        // Asserts when import is done
        creationThread.join(); // Wait until creation is done (i.e. end of thread since creation is a blocking
                               // operation).
        String expected = "[{\"id\":\"" + next.getId() + "\"}]";
        when().get("/datasets").then().statusCode(HttpStatus.OK.value())
                .body(sameJSONAs(expected).allowingExtraUnexpectedFields());
    }

    @Component
    public static class PausedAnalyzer implements SynchronousDataSetAnalyzer {

        private static final Logger LOGGER = LoggerFactory.getLogger(PausedAnalyzer.class);

        @Override
        public int order() {
            return Integer.MAX_VALUE - 1;
        }

        @Override
        public void analyze(String dataSetId) {
            try {
                String timeToPause = System.getProperty("DataSetImportTest.PausedAnalyzer");
                if (!StringUtils.isEmpty(timeToPause)) {
                    LOGGER.info("Pausing import (for {} second(s))...", timeToPause);
                    TimeUnit.SECONDS.sleep(Integer.parseInt(timeToPause));
                    LOGGER.info("Pause done.");
                } else {
                    LOGGER.info("No pause.");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to pause import.", e);
            }
        }
    }
}
