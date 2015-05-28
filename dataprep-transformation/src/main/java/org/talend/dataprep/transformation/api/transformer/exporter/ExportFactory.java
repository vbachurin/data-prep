package org.talend.dataprep.transformation.api.transformer.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvExporter;
import org.talend.dataprep.transformation.api.transformer.exporter.xls.XlsExporter;
import org.talend.dataprep.transformation.api.transformer.exporter.tableau.TableauExporter;


@Component
public class ExportFactory {

    @Autowired
    private ActionParser parser;

    @Autowired
    private ApplicationContext context;

    public Transformer getExporter(final ExportConfiguration configuration) {
        final ParsedActions actionConsumer = parser.parse(configuration.getActions());

        switch (configuration.getFormat()) {
        case CSV:
            return get(CsvExporter.class, actionConsumer, configuration);
        case XLS:
            return get(XlsExporter.class, actionConsumer, configuration);
        case TABLEAU:
            return get(TableauExporter.class, actionConsumer, configuration);
        }
        throw new UnsupportedOperationException("Unknown export type : " + configuration.getFormat());
    }

    public Transformer get(final Class<? extends Transformer> transformerClass, final ParsedActions actionConsumer,
            final ExportConfiguration configuration) {
        return context.getBean(transformerClass, actionConsumer, configuration);
    }
}
