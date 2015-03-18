package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.transformation.i18n.MessagesBundle;

public interface ActionMetadata {

    String getName();

    default String getLabel() {
        return getLabel(Locale.ENGLISH);
    }

    default String getDescription() {
        return getDescription(Locale.ENGLISH);
    }

    default String getLabel(Locale locale) {
        return MessagesBundle.getString(locale, "action." + getName() + ".label");
    }

    default String getDescription(Locale locale) {
        return MessagesBundle.getString(locale, "action." + getName() + ".desc");
    }

    Type getType();

    String getCategory();

    Item[] getItems();

    String getValue();

    Parameter[] getParameters();

    Consumer<DataSetRow> create(Iterator<Map.Entry<String, JsonNode>> input);
}
