package org.talend.dataprep.transformation.api.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.transformation.api.transformer.type.TypeTransformerSelector;
import org.talend.dataprep.transformation.exception.TransformationMessages;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

@Component
@Scope("request")
class DiffTransformer implements Transformer {

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private TypeTransformerSelector typeStateSelector;

    private final Consumer<DataSetRow> oldAction;
    private final Consumer<DataSetRow> newAction;
    private final List<Integer> indexes;

    DiffTransformer(final List<Integer> indexes, final Consumer<DataSetRow> oldAction, final Consumer<DataSetRow> newAction) {
        this.oldAction = oldAction;
        this.newAction = newAction;
        this.indexes = indexes == null ? null : new ArrayList<>(indexes);
    }

    @Override
    public void transform(InputStream input, OutputStream output) {
        try {
            if (input == null) {
                throw new IllegalArgumentException("Input cannot be null.");
            }
            if (output == null) {
                throw new IllegalArgumentException("Output cannot be null.");
            }

            final JsonFactory factory = new JsonFactory();
            final JsonParser parser = factory.createParser(input);
            final JsonGenerator generator = factory.createGenerator(output);
            generator.setCodec(builder.build());

            typeStateSelector.process(parser, generator, indexes, true, oldAction, newAction);
            output.flush();
        } catch (IOException e) {
            throw Exceptions.User(TransformationMessages.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
