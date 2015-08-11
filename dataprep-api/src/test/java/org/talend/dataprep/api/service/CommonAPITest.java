package org.talend.dataprep.api.service;

import com.jayway.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class CommonAPITest
{

    @Value("${local.server.port}")
    public int port;

    @Autowired
    ConfigurableEnvironment environment;

    @Before
    public void setUp() {
        RestAssured.port = port;

        // Overrides connection information with random port value
        MockPropertySource connectionInformation = new MockPropertySource()
            .withProperty("dataset.service.url", "http://localhost:" + port)
            .withProperty("transformation.service.url", "http://localhost:" + port)
            .withProperty("preparation.service.url", "http://localhost:" + port);
        environment.getPropertySources().addFirst(connectionInformation);
    }

    @After
    public void tearDown() {
        // no op
    }

    @Test
    public void get_all_types() throws Exception {
        String json = RestAssured.when().get("/api/types").asString();
        System.out.println("json:"+ json);

    }

}
