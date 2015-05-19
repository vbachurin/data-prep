package org.talend.dataprep.transformation.api.transformer.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvExportConfiguration;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvExporter;

@Component
public class ExportFactory {

    @Autowired
    private ActionParser parser;

    @Autowired
    private WebApplicationContext context;

    public Transformer getExporter(final ExportConfiguration configuration) {
        final ParsedActions actionConsumer = parser.parse(configuration.getActions());

        switch(configuration.getFormat()) {
            case CSV : return get(CsvExporter.class, actionConsumer, configuration);
            case XLS : throw new UnsupportedOperationException(configuration.getFormat() + "export not implemented yet");
            case TABLEAU : throw new UnsupportedOperationException(configuration.getFormat() + "export not implemented yet");
        }
        throw new UnsupportedOperationException("Unknown export type : " + configuration.getFormat());
    }

    public Transformer get(final Class<? extends Transformer> transformerClass, final ParsedActions actionConsumer, final ExportConfiguration configuration) {
        return context.getBean(transformerClass, actionConsumer, configuration);
    }
}
