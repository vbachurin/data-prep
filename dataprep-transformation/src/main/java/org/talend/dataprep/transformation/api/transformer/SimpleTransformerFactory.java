package org.talend.dataprep.transformation.api.transformer;

import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.transformation.api.action.ActionParser;

import java.util.function.Consumer;

public class SimpleTransformerFactory implements TransformerFactory {

    @Override
    public Transformer get(String actions) {
        ActionParser parser = new ActionParser();
        Consumer<DataSetRow> action = parser.parse(actions);
        return new SimpleTransformer(action);
    }
}
