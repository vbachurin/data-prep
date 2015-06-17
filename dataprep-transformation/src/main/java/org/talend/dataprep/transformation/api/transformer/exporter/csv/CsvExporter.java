package org.talend.dataprep.transformation.api.transformer.exporter.csv;

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

@Component("transformer#csv")
@Scope("request")
public class CsvExporter implements Transformer, Exporter {

    @Autowired
    private TransformerStepSelector typeStateSelector;

    private final ParsedActions actions;

    private final ExportConfiguration exportConfiguration;

    public CsvExporter(final ParsedActions actions, final ExportConfiguration configuration) {
        this.actions = actions;
        this.exportConfiguration = configuration;
    }

    @Override
    public void transform(DataSet input, OutputStream output) {
        try {
            final TransformerConfiguration configuration = from(input) //
                    .output(new CsvWriter(output, (char) exportConfiguration.getArguments().get("csvSeparator"))) //
                    .columnActions(actions.getMetadataTransformer()) //
                    .recordActions(actions.getRowTransformer()) //
                    .build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @Override
    public ExportType getExportType() {
        return ExportType.CSV;
    }
}
