package org.talend.dataprep.transformation.api.transformer.type;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.talend.dataprep.transformation.exception.TransformationErrorCodes.UNABLE_TO_PARSE_JSON;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.exporter.json.JsonWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext
public class TransformerStepSelectorTest {

    @Autowired
    private TransformerStepSelector transformer;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private StringWriter writer;

    private TransformerWriter transformerWriter;

    //@formatter:off
    private final ParsedActions identityAction = new ParsedActions((row, context) -> {}, (rowMetadata, context) -> {});
    //@formatter:on

    //@formatter:off
    private final ParsedActions changeLastnameAction = new ParsedActions(
            (row, context) -> {
                final String transformedLastname = row.get("lastname").toUpperCase();
                row.set("lastname", transformedLastname);
            },
            (rowMetadata, context) -> {}
    );
    //@Formatter:on

    //@formatter:off
    private final ParsedActions getChangeNameAndDeleteAction = new ParsedActions(
            (row, context) -> {
                final String transformedLastname = row.get("lastname").toUpperCase();
                final String transformedFirstname = row.get("firstname").toUpperCase();
                row.set("lastname", transformedLastname);
                row.set("firstname", transformedFirstname);
                row.setDeleted(row.get("city").equals("Columbia"));
            },
            (rowMetadata, context) -> {}
    );
    //@formatter:on

    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        final JsonGenerator generator = new JsonFactory().createGenerator(writer);
        generator.setCodec(builder.build());
        transformerWriter = new JsonWriter(generator);
    }

    @Test
    public void process_should_transform_records() throws Exception {
        // given
        final InputStream inputStream = TransformerStepSelectorTest.class.getResourceAsStream("nominal.json");
        final String expectedContent = IOUtils.toString(TransformerStepSelectorTest.class
                .getResourceAsStream("nominal_result.json"));

        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            //@formatter:off
            final TransformerConfiguration configuration = TransformerConfiguration.builder()
                    .input(dataSet)
                    .output(transformerWriter)
                    .preview(false)
                    .recordActions(changeLastnameAction.asUniqueRowTransformer())
                    .columnActions(changeLastnameAction.asUniqueMetadataTransformer())
                    .build();
            //@formatter:on

            // when
            transformer.process(configuration);

            // then
            assertThat(writer.toString(), sameJSONAs(expectedContent).allowingAnyArrayOrdering().allowingExtraUnexpectedFields());
        }
    }

    @Test
    public void process_should_throw_exception_when_json_not_valid() throws Exception {
        // given
        final InputStream inputStream = TransformerStepSelectorTest.class.getResourceAsStream("not_valid_object.json");

        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            // when
            try {
                mapper.reader(DataSet.class).readValue(parser);
                fail("should have thrown UserException because input json is not valid");
            }

            // then
            catch (JsonMappingException e) {
                // Expected
            }
        }
    }

    @Test
    public void process_should_throw_exception_when_column_json_is_not_valid() throws Exception {
        // given
        final InputStream inputStream = TransformerStepSelectorTest.class.getResourceAsStream("not_valid_col.json");

        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            // when
            try {
                mapper.reader(DataSet.class).readValue(parser);
                fail("should have thrown UserException because column json is not valid");
            }

            // then
            catch (JsonMappingException e) {
                // Expected
            }
        }
    }

    @Test
    public void process_should_throw_exception_when_record_json_is_not_valid() throws Exception {
        // given
        final InputStream inputStream = TransformerStepSelectorTest.class.getResourceAsStream("not_valid_record.json");

        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            //@formatter:off
            final TransformerConfiguration configuration = TransformerConfiguration.builder()
                    .input(dataSet)
                    .output(transformerWriter)
                    .preview(false)
                    .recordActions(changeLastnameAction.asUniqueRowTransformer())
                    .columnActions(changeLastnameAction.asUniqueMetadataTransformer())
                    .build();
            //@formatter:on

            // when
            try {
                transformer.process(configuration);
                fail("should have thrown UserException because record json is not valid");
            }

            // then
            catch (Exception e) {
                Assert.assertEquals(UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
            }
        }
    }

    @Test
    public void process_should_write_preview_for_the_given_indexes() throws Exception {
        // given
        final InputStream inputStream = TransformerStepSelectorTest.class.getResourceAsStream("preview.json");
        final String expectedContent = IOUtils.toString(TransformerStepSelectorTest.class
                .getResourceAsStream("preview_result.json"));

        final List<Integer> indexes = new ArrayList<>(3);
        indexes.add(1);
        indexes.add(3);
        indexes.add(5);

        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            final TransformerConfiguration configuration = TransformerConfiguration.builder().input(dataSet) //
                    .output(transformerWriter) //
                    .indexes(indexes) //
                    .preview(true) //
                    .recordActions(identityAction.asUniqueRowTransformer()) //
                    .recordActions(getChangeNameAndDeleteAction.asUniqueRowTransformer()) //
                    .build();

            // when
            transformer.process(configuration);

            // then
            assertEquals(expectedContent, writer.toString(), false);
        }
    }
}