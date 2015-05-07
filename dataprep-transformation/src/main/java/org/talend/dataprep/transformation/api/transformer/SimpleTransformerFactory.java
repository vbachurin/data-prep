package org.talend.dataprep.transformation.api.transformer;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.ActionParser;

@Component
public class SimpleTransformerFactory implements TransformerFactory {

    @Autowired
    private WebApplicationContext context;

    final ActionParser parser = new ActionParser();

    private Consumer<DataSetRow> action;

    @Override
    public Transformer get() {
        return context.getBean(SimpleTransformer.class, action);
    }

    @Override
    public TransformerFactory withActions(final String... actions) {
        if(actions.length != 1) {
            throw new IllegalArgumentException("SimpleTransformerFactory only take 1 action to perform");
        }

        action = parser.parse(actions[0]);
        return this;
    }

    @Override
    public TransformerFactory withIndexes(String indexes) {
        throw new UnsupportedOperationException("Indexes are not supported in SimpleTransformerFactory");
    }
}
