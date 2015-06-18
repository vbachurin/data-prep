package org.talend.dataprep.transformation.api.transformer.exporter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.csv.CsvExporter;
import org.talend.dataprep.transformation.api.transformer.exporter.json.JsonExporter;
import org.talend.dataprep.transformation.api.transformer.exporter.tableau.TableauExporter;
import org.talend.dataprep.transformation.api.transformer.exporter.xls.XlsExporter;

@Component
public class ExportFactory {

    @Autowired
    private ActionParser parser;

    @Autowired
    private ApplicationContext context;

    public Transformer getExporter(final ExportConfiguration configuration) {
        final ParsedActions actionConsumer = parser.parse(configuration.getActions());

        switch (configuration.getFormat()) {
        case JSON:
            return get(JsonExporter.class, actionConsumer, configuration);
        case CSV:
            return get(CsvExporter.class, actionConsumer, configuration);
        case XLS:
            return get(XlsExporter.class, actionConsumer, configuration);
        case TABLEAU:
            return get(TableauExporter.class, actionConsumer, configuration);
        default:
            throw new UnsupportedOperationException("Unknown export type : " + configuration.getFormat());
        }
    }

    public List<ExportType> getExportTypes() {

        String[] beansNames = context.getBeanNamesForType(Exporter.class);

        List<ExportType> exportTypes = new ArrayList<>(beansNames.length);

        for (String beanName : beansNames) {
            exportTypes.add( ((Exporter) context.getBean(beanName, null, null)).getExportType());
        }

        return exportTypes;

    }

    public Transformer get(final Class<? extends Transformer> transformerClass, final ParsedActions actionConsumer,
            final ExportConfiguration configuration) {
        return context.getBean(transformerClass, actionConsumer, configuration);
    }
}
