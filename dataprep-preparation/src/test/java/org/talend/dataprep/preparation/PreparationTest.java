package org.talend.dataprep.preparation;

import com.jayway.restassured.RestAssured;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.api.DataSetMetadata.Builder.metadata;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PreparationTest {

    @Value("${local.server.port}")
    public int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @org.junit.Test
    public void testList() throws Exception {
        when().get("/preparations").then().statusCode(HttpStatus.OK.value()).body(equalTo("[]"));
    }
}
