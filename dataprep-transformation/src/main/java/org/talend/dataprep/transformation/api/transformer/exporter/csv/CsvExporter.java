package org.talend.dataprep.transformation.api.transformer.exporter.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TypeTransformerSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

@Component
@Scope("request")
public class CsvExporter implements Transformer {

    @Autowired
    private TypeTransformerSelector typeStateSelector;

    private ParsedActions actions;
    private Character separator;

    public CsvExporter(final ParsedActions actions, final Character separator) {
        this.actions = actions;
        this.separator = separator;
    }

    @Override
    public void transform(InputStream input, OutputStream output) {
        try {
            final TransformerConfiguration configuration = getDefaultConfiguration(input, output, null)
                    .writer(new CsvWriter(output, separator))
                    .actions(DataSetRow.class, actions.getRowTransformer())
                    .columnActions(actions.getMetadataTransformers())
                    .build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
