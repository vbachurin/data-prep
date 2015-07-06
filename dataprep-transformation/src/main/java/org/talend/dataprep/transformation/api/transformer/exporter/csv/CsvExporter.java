package org.talend.dataprep.transformation.api.transformer.exporter.csv;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.ExportConfiguration;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TransformerStepSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

@Component("transformer#csv")
@Scope("request")
public class CsvExporter implements Transformer {

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

        char csvSeparator = exportConfiguration.getArguments().containsKey( "exportParameters.csvSeparator" ) ? //
            ((String)exportConfiguration.getArguments().get("exportParameters.csvSeparator")).charAt( 0 ) : //
            au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;

        try {
            final TransformerConfiguration configuration = from(input) //
                    .output(new CsvWriter(output, csvSeparator )) //
                    .columnActions(actions.asUniqueMetadataTransformer() ) //
                .recordActions(actions.asUniqueRowTransformer()) //
                    .build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

}
