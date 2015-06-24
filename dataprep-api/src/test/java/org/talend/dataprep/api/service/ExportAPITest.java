package org.talend.dataprep.api.service;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.Application;

import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ExportAPITest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    ConfigurableEnvironment environment;

    @Before
    public void initialize() {
        RestAssured.port = port;

        // Overrides connection information with random port value
        MockPropertySource connectionInformation = new MockPropertySource()
                .withProperty("dataset.service.url", "http://localhost:" + port)
                .withProperty("transformation.service.url", "http://localhost:" + port)
                .withProperty("preparation.service.url", "http://localhost:" + port);
        environment.getPropertySources().addFirst(connectionInformation);

    }

    @Test
    public void get_all_export_types() throws Exception {
        String actual = RestAssured.when().get("/api/export/types").asString();
        final String expectedContent = IOUtils.toString(this.getClass().getResourceAsStream("export_type.json"));
        JSONAssert.assertEquals(expectedContent, actual, false);
    }

}
