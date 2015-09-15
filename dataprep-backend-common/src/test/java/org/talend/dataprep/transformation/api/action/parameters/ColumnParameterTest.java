package org.talend.dataprep.transformation.api.action.parameters;

import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Unit test for the ColumnParameter bean. Mostly test the json serialization.
 * 
 * @see ColumnParameter
 */
public class ColumnParameterTest extends ParameterBaseTest {

    @Test
    public void shouldSerializeToJson() throws IOException {
        // given
        ColumnParameter expected = new ColumnParameter("column_id", "0001", false, false, Arrays.asList("numeric", "string"));

        // when
        StringWriter out = new StringWriter();
        builder.build().writer().writeValue(out, expected);

        // then
        assertThat(out.toString(), sameJSONAs(IOUtils.toString(this.getClass().getResourceAsStream("columnParameter.json"))));
    }
}