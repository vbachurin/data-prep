package org.talend.dataprep.transformation.api.transformer.type;

import static org.junit.Assert.fail;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.talend.dataprep.transformation.exception.TransformationErrorCodes.UNABLE_TO_PARSE_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.Application;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TypeTransformerSelectorTest {

    @Autowired
    private TypeTransformerSelector transformer;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private JsonGenerator generator;

    private StringWriter writer;

    private final Consumer<DataSetRow> identityAction = (row) -> {};
    private final Consumer<DataSetRow> changeLastnameAction = (row) -> {
        final String transformedLastname = row.get("lastname").toUpperCase();
        row.set("lastname", transformedLastname);
    };
    private final Consumer<DataSetRow> getChangeNameAndDeleteAction = (row) -> {
        final String transformedLastname = row.get("lastname").toUpperCase();
        final String transformedFirstname = row.get("firstname").toUpperCase();
        row.set("lastname", transformedLastname);
        row.set("firstname", transformedFirstname);
        row.setDeleted(row.get("city").equals("Columbia"));
    };

    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        generator = new JsonFactory().createGenerator(writer);
        generator.setCodec(builder.build());
    }

    @Test
    public void process_should_transform_records() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("nominal.json");
        final String expectedContent = IOUtils.toString(TypeTransformerSelectorTest.class
                .getResourceAsStream("nominal_result.json"));

        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        // when
        transformer.process(parser, generator, null, false, changeLastnameAction);

        // then
        assertEquals(writer.toString(), expectedContent, false);
    }

    @Test
    public void process_should_throw_exception_when_json_not_valid() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("not_valid_object.json");
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        // when
        try {
            transformer.process(parser, generator, null, false, changeLastnameAction);
            fail("should have thrown UserException because input json is not valid");
        }

        // then
        catch (Exception e) {
            Assert.assertEquals(UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void process_should_throw_exception_when_column_json_is_not_valid() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("not_valid_col.json");
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        // when
        try {
            transformer.process(parser, generator, null, false, changeLastnameAction);
            fail("should have thrown UserException because column json is not valid");
        }

        // then
        catch (Exception e) {
            Assert.assertEquals(UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void process_should_throw_exception_when_record_json_is_not_valid() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("not_valid_record.json");
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        // when
        try {
            transformer.process(parser, generator, null, false, changeLastnameAction);
            fail("should have thrown UserException because record json is not valid");
        }

        // then
        catch (Exception e) {
            Assert.assertEquals(UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void process_should_write_preview_for_the_given_indexes() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("preview.json");
        final String expectedContent = IOUtils.toString(TypeTransformerSelectorTest.class
                .getResourceAsStream("preview_result.json"));

        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        final List<Integer> indexes = new ArrayList<>(3);
        indexes.add(1);
        indexes.add(3);
        indexes.add(5);

        // when
        transformer.process(parser, generator, indexes, true, identityAction, getChangeNameAndDeleteAction);

        // then
        assertEquals(writer.toString(), expectedContent, false);
    }
}