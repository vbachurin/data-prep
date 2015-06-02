package org.talend.dataprep.transformation.api.transformer.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;

/**
 * Default implementation of the Transformer Factory.
 */
@Component
public class SimpleTransformerFactory implements TransformerFactory {

    /** The Spring web application context. */
    @Autowired
    private WebApplicationContext context;

    /** The component that parses actions out of json string. */
    @Autowired
    private ActionParser parser;

    /** The action to perform by the transformer. */
    private ParsedActions actions;

    /**
     * @see TransformerFactory#get()
     */
    @Override
    public Transformer get() {
        return context.getBean(SimpleTransformer.class, actions);
    }

    /**
     * @param actions
     * @return the tranformer factory for the given json encoded actions.
     */
    public TransformerFactory withActions(final String actions) {
        this.actions = parser.parse(actions);
        return this;
    }

}
