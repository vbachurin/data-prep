package org.talend.dataprep.transformation.api.action.metadata;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public interface ActionMetadata {

    String getName();

    Type getType();

    String getCategory();

    Item[] getItems();

    String getValue();

    Parameter[] getParameters();

    Consumer<DataSetRow> create(Iterator<Map.Entry<String, JsonNode>> input);
}
