package org.talend.dataprep.transformation.api.transformer.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvExporter;

import java.util.function.Consumer;

@Component
public class ExportFactory {

    @Autowired
    private ActionParser parser;

    public Transformer getExporter(final String type, final String actions) {
        final Consumer<DataSetRow> actionConsumer = parser.parse(actions);

        switch(type) {
            case "CSV" : return new CsvExporter(actionConsumer);
        }
        return null;
    }
}
