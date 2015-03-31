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

    @Override
    public Transformer get(String actions) {
        ActionParser parser = new ActionParser();
        Consumer<DataSetRow> action = parser.parse(actions);
        return context.getBean(SimpleTransformer.class, action);
    }
}
