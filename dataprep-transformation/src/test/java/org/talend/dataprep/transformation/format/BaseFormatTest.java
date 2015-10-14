package org.talend.dataprep.transformation.format;

import static org.junit.Assert.assertThat;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for all format tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BaseFormatTest.class)
@ComponentScan(basePackages = "org.talend.dataprep")
@Configuration
public abstract class BaseFormatTest {

    /** Dataprep ready json builder. */
    @Autowired
    protected Jackson2ObjectMapperBuilder builder;

    /** Spring application context. */
    @Autowired
    protected ApplicationContext context;

    protected void testFormat(ExportFormat format, String expectedJson) throws IOException {
        StringWriter writer = new StringWriter();
        builder.build().writer().writeValue(writer, format);
        assertThat(writer.toString(), sameJSONAsFile(BaseFormatTest.class.getResourceAsStream(expectedJson)));
    }

}
