package org.talend.dataprep.transformation.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.transformation.Application;

import com.jayway.restassured.RestAssured;

/**
 * Base class for TransformationService integration tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public abstract class TransformationServiceBaseTests {

    @Value("${local.server.port}")
    public int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

}
