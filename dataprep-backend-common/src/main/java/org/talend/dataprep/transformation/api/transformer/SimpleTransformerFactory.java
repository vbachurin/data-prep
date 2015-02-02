package org.talend.dataprep.transformation.api.transformer;

import org.talend.dataprep.transformation.api.action.Action;
import org.talend.dataprep.transformation.api.action.ActionParser;

public class SimpleTransformerFactory implements TransformerFactory {

    @Override
    public Transformer get(String actions) {
        ActionParser parser = new ActionParser();
        Action action = parser.parse(actions);
        return new SimpleTransformer(action);
    }
}
