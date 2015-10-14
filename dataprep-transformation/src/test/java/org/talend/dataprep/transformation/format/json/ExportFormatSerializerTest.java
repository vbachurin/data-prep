package org.talend.dataprep.transformation.format.json;

import static org.junit.Assert.assertThat;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.talend.dataprep.transformation.format.BaseFormatTest;
import org.talend.dataprep.transformation.format.ExportFormat;

/**
 * Unit test for json serialization of ExportFormat.
 */
public class ExportFormatSerializerTest extends BaseFormatTest {

    @Test
    public void csv() throws IOException {
        StringWriter writer = new StringWriter();

        ExportFormat format = new ExportFormat("TOTO", "text/toto", ".toto", true, false,
                Collections.singletonList(new ExportFormat.Parameter("totoSeparator", "CHOOSE_SEPARATOR", "radio",
                        new ExportFormat.ParameterValue("|", "SEPARATOR_PIPE"),
                        Arrays.asList(new ExportFormat.ParameterValue("\u0009", "SEPARATOR_TAB"), // &#09;
                                new ExportFormat.ParameterValue(":", "SEPARATOR_COLUMN"),
                                new ExportFormat.ParameterValue(".", "SEPARATOR_DOT")))));

        builder.build().writer().writeValue(writer, format);
        assertThat(writer.toString(), sameJSONAsFile(ExportFormatSerializerTest.class.getResourceAsStream("toto.json")));
    }

}
