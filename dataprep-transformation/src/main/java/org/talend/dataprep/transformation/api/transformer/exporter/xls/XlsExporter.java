package org.talend.dataprep.transformation.api.transformer.exporter.xls;

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
import org.talend.dataprep.transformation.api.transformer.type.TransformerStepSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

@Component("transformer#xls")
@Scope("request")
public class XlsExporter implements Transformer, Exporter {

    private final ParsedActions actions;

    private final ExportConfiguration exportConfiguration;

    @Autowired
    private TransformerStepSelector typeStateSelector;

    public XlsExporter(final ParsedActions actions, final ExportConfiguration configuration) {
        this.actions = actions;
        this.exportConfiguration = configuration;
    }

    @Override
    public void transform(DataSet input, OutputStream output) {
        try {
            final TransformerConfiguration configuration = from(input) //
                    .output(new XlsWriter(output)) //
                    .recordActions(actions.getRowTransformer()) //
                    .columnActions(actions.getMetadataTransformer()) //
                    .build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @Override
    public ExportType getExportType() {
        return ExportType.XLS;
    }
}
