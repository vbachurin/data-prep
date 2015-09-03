package org.talend.dataprep.api.type.json;

import static org.junit.Assert.assertThat;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.type.ExportType;

/**
 * Unit test for json serialization of ExportType.
 * 
 * @see org.talend.dataprep.api.type.ExportType
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ExportTypeJSONTest.class)
@ComponentScan(basePackages = "org.talend.dataprep")
public class ExportTypeJSONTest {

    /** Dataprep ready json builder. */
    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @Test
    public void csv() throws IOException {
        StringWriter writer = new StringWriter();
        builder.build().writer().writeValue(writer, ExportType.CSV);
        assertThat(writer.toString(), sameJSONAsFile(ExportTypeJSONTest.class.getResourceAsStream("csv.json")));
    }

    @Test
    public void xls() throws IOException {
        StringWriter writer = new StringWriter();
        builder.build().writer().writeValue(writer, ExportType.XLS);
        assertThat(writer.toString(), sameJSONAsFile(ExportTypeJSONTest.class.getResourceAsStream("xls.json")));
    }

    @Test
    public void tableau() throws IOException {
        StringWriter writer = new StringWriter();
        builder.build().writer().writeValue(writer, ExportType.TABLEAU);
        assertThat(writer.toString(), sameJSONAsFile(ExportTypeJSONTest.class.getResourceAsStream("tableau.json")));
    }

    @Test
    public void json() throws IOException {
        StringWriter writer = new StringWriter();
        builder.build().writer().writeValue(writer, ExportType.JSON);
        assertThat(writer.toString(), sameJSONAsFile(ExportTypeJSONTest.class.getResourceAsStream("json.json")));
    }

}
