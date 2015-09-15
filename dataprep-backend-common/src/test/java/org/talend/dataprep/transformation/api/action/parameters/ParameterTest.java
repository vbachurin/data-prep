package org.talend.dataprep.transformation.api.action.parameters;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for the parameter class. Mostly check the equals and json de/serialization
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ParameterTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
public class ParameterTest {

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Test
    public void shouldSerializeToJson() throws IOException {
        // given
        Parameter expected = new Parameter("column_id", ParameterType.STRING.asString(), "0001", true, false);

        // when
        StringWriter out = new StringWriter();
        builder.build().writer().writeValue(out, expected);
        je suis ici
    }

}