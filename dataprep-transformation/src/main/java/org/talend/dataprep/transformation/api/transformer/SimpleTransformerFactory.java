package org.talend.dataprep.transformation.api.transformer;

import java.util.function.Consumer;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.ActionParser;

public class SimpleTransformerFactory implements TransformerFactory {

    @Override
    public Transformer get(String actions) {
        ActionParser parser = new ActionParser();
        Consumer<DataSetRow> action = parser.parse(actions);
        return new SimpleTransformer(action);
    }
}
