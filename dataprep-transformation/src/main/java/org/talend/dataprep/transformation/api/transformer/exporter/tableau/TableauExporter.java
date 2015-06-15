package org.talend.dataprep.transformation.api.transformer.exporter.tableau;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.ExportConfiguration;
import org.talend.dataprep.transformation.api.transformer.exporter.Exporter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TypeTransformerSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

@Component("transformer#tableau")
@Scope("request")
public class TableauExporter implements Transformer, Exporter {

    @Autowired
    private TypeTransformerSelector typeStateSelector;

    private final ParsedActions actions;

    private final ExportConfiguration exportConfiguration;

    public TableauExporter(final ParsedActions actions, final ExportConfiguration configuration) {
        this.actions = actions;
        this.exportConfiguration = configuration;
    }

    @Override
    public void transform(DataSet input, OutputStream output) {
        try {
            final TransformerConfiguration configuration = getDefaultConfiguration(input, output, null)
                    .output(new TableauWriter(output)).recordActions(actions.getRowTransformer())
                    .columnActions(actions.getMetadataTransformer()).build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @Override
    public ExportType getExportType() {
        return ExportType.TABLEAU;
    }
}
