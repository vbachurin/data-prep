package org.talend.dataprep.api.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.Application;
import org.talend.dataprep.api.type.ExportType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ExportAPITest {

    final Logger logger = LoggerFactory.getLogger(getClass());

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
        String json = RestAssured.when().get("/api/export/types").asString();

        logger.debug("json: '{}'", json);

        ObjectMapper objectMapper = new ObjectMapper();

        List<ExportType> exportTypes = new ArrayList<>();

        JsonParser jsonParser = objectMapper.getFactory().createParser(json);

        JsonToken jsonToken = jsonParser.nextToken();

        while (jsonToken != null) {
            if (jsonToken == JsonToken.FIELD_NAME) {
                if (StringUtils.equals(jsonParser.getCurrentName(), "id")) {
                    jsonToken = jsonParser.nextToken();
                    String id = jsonParser.getText();
                    exportTypes.add(ExportType.valueOf(id));
                }
            }
            jsonToken = jsonParser.nextToken();
        }

        Assertions.assertThat(exportTypes).isNotNull().isNotEmpty().hasSize(3);

    }

}
