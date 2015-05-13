package org.talend.dataprep.transformation.api.transformer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.json.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public interface Transformer {
    void transform(InputStream input, OutputStream output);

    default TransformerConfiguration.Builder getDefaultConfiguration(InputStream input, OutputStream output, Jackson2ObjectMapperBuilder builder) throws IOException {
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(input);

        final JsonGenerator generator = factory.createGenerator(output);
        generator.setCodec(builder.build());
        final JsonWriter writer = new JsonWriter(generator);

        return TransformerConfiguration
                .builder()
                .parser(parser)
                .writer(writer);
    }
}
