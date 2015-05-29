package org.talend.dataprep.transformation.api.transformer.exporter.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.ExportConfiguration;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TypeTransformerSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

@Component
@Scope("request")
public class CsvExporter implements Transformer {

    @Autowired
    private TypeTransformerSelector typeStateSelector;

    private final ParsedActions actions;

    private final ExportConfiguration exportConfiguration;

    public CsvExporter(final ParsedActions actions, final ExportConfiguration configuration) {
        this.actions = actions;
        this.exportConfiguration = configuration;
    }

    @Override
    public void transform(InputStream input, OutputStream output) {
        try {

            final TransformerConfiguration configuration = getDefaultConfiguration(input, output, null)
                    .output(new CsvWriter(output, (char) exportConfiguration.getArguments().get("csvSeparator")))
                    .actions(DataSetRow.class, actions.getRowTransformer())
                    .actions(RowMetadata.class, actions.getMetadataTransformer()).build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
