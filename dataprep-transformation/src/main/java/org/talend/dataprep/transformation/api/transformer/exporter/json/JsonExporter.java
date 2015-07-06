package org.talend.dataprep.transformation.api.transformer.exporter.json;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.ExportConfiguration;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TransformerStepSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

@Component("transformer#json")
@Scope("request")
public class JsonExporter implements Transformer {

    private final ParsedActions actions;

    private final ExportConfiguration exportConfiguration;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private TransformerStepSelector typeStateSelector;

    public JsonExporter(final ParsedActions actions, final ExportConfiguration configuration) {
        this.actions = actions;
        this.exportConfiguration = configuration;
    }

    @Override
    public void transform(DataSet input, OutputStream output) {
        try {
            // Create configuration
            final TransformerConfiguration configuration = from(input) //
                    .output(JsonWriter.create(builder, output)) //
                    .recordActions(actions.asUniqueRowTransformer()) //
                    .columnActions(actions.asUniqueMetadataTransformer()) //
                    .build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

}
