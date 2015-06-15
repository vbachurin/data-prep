package org.talend.dataprep.transformation.api.transformer;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.json.JsonWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Base interface used to transform (apply preparations to) dataset content.
 */
public interface Transformer {

    /**
     * Transform (apply preparations to) data content.
     *  @param input the dataset content.
     * @param output where to output the transformation.
     */
    void transform(DataSet input, OutputStream output);

    default TransformerConfiguration.Builder getDefaultConfiguration(DataSet input, OutputStream output,
            Jackson2ObjectMapperBuilder builder) throws IOException {

        final JsonFactory factory = new JsonFactory();

        JsonWriter writer = null;
        if (builder != null) {
            final JsonGenerator generator = factory.createGenerator(output);
            generator.setCodec(builder.build());
            writer = new JsonWriter(generator);
        }

        return TransformerConfiguration.builder().input(input).output(writer);
    }
}
