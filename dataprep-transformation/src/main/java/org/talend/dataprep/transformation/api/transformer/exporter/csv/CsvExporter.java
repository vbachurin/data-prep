package org.talend.dataprep.transformation.api.transformer.exporter.csv;

import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.type.TypeTransformerSelector;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

public class CsvExporter implements Transformer {
    @Autowired
    private TypeTransformerSelector typeStateSelector;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private Consumer<DataSetRow> actions;

    public CsvExporter(final Consumer<DataSetRow> actions) {
        this.actions = actions;
    }

    @Override
    public void transform(InputStream input, OutputStream output) {
        try {
            final TransformerConfiguration configuration = getDefaultConfiguration(input, output, builder)
                    .writer(new CsvWriter(output, DEFAULT_SEPARATOR ))
                    .actions(DataSetRow.class, actions)
                    .build();
            typeStateSelector.process(configuration);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
