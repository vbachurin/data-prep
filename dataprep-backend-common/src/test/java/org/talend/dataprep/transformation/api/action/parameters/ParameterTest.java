package org.talend.dataprep.transformation.api.action.parameters;

import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Unit test for the parameter class. Mostly check the equals and json de/serialization
 */
public class ParameterTest extends ParameterBaseTest {

    @Test
    public void shouldSerializeToJsonWithoutEmptyConfiguration() throws IOException {
        // given
        Parameter expected = new Parameter("column_id", ParameterType.STRING.asString(), "0001", true, false);

        // when
        StringWriter out = new StringWriter();
        builder.build().writer().writeValue(out, expected);

        // then
        assertThat(out.toString(), sameJSONAs(IOUtils.toString(this.getClass().getResourceAsStream("textParameter.json"))));
    }

}