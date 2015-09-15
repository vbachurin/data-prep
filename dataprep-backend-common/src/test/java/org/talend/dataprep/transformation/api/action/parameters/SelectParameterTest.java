package org.talend.dataprep.transformation.api.action.parameters;

import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Unit test for the SelectParameter bean. Mostly test the json serialization.
 * 
 * @see SelectParameter
 */
public class SelectParameterTest extends ParameterBaseTest {

    @Test
    public void shouldSerializeToJson() throws IOException {
        // given
        SelectParameter expected = SelectParameter.Builder.builder() //
                .name("column_id") //
                .defaultValue("") //
                .implicit(false) //
                .canBeBlank(false) //
                .item("first choice", "1", null) //
                .item("second choice", "2", null) //
                .item("your choice", "your choice", new Parameter("limit", ParameterType.INTEGER.asString(), "", false, false)) //
                .build();

        // when
        StringWriter out = new StringWriter();
        builder.build().writer().writeValue(out, expected);

        // then
        assertThat(out.toString(), sameJSONAs(IOUtils.toString(this.getClass().getResourceAsStream("selectParameter.json"))));
    }
}