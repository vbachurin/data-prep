package org.talend.dataprep.transformation.api.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;

@Component
public class SimpleTransformerFactory implements TransformerFactory {

    @Autowired
    private WebApplicationContext context;

    final ActionParser parser = new ActionParser();

    private ParsedActions parsedActions;

    @Override
    public Transformer get() {
        return context.getBean(SimpleTransformer.class, parsedActions);
    }

    @Override
    public TransformerFactory withActions(final String... actions) {
        if (actions.length != 1) {
            throw new IllegalArgumentException("SimpleTransformerFactory only take 1 action to perform");
        }

        parsedActions = parser.parse(actions[0]);
        return this;
    }

    @Override
    public TransformerFactory withIndexes(String indexes) {
        throw new UnsupportedOperationException("Indexes are not supported in SimpleTransformerFactory");
    }
}
