package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TypeTransformerSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

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

    private final ParsedActions oldActions;

    private final ParsedActions newActions;

    private final List<Integer> indexes;

    DiffTransformer(final List<Integer> indexes, final ParsedActions oldActions, final ParsedActions newActions) {
        this.oldActions = oldActions;
        this.newActions = newActions;
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

            //TODO merge ajouter les parsed actions à la configuration
            final TransformerConfiguration configuration = getDefaultConfiguration(input, output, builder)
                    .indexes(indexes)
                    .preview(true)
                    .actions(DataSetRow.class, oldAction)
                    .actions(DataSetRow.class, newAction)
                    .build();

            typeStateSelector.process(configuration);
            output.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
