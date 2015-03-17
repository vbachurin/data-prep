package org.talend.dataprep.transformation.api.action.metadata;

import org.codehaus.jackson.JsonNode;
import org.talend.dataprep.api.DataSetRow;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public interface ActionMetadata {

    String getName();

    default String getLabel() {
        return getLabel(Locale.ENGLISH);
    }

    default String getDescription() {
        return getDescription(Locale.ENGLISH);
    }

    default String getLabel(Locale locale) {
        String i18nKey = "action." + getName() + ".label";
        return getTranslatedLabel(locale, i18nKey);
    }

    default String getDescription(Locale locale) {
        String i18nKey = "action." + getName() + ".desc";
        return getTranslatedLabel(locale, i18nKey);
    }

    default String getTranslatedLabel(Locale locale, String i18nKey) {
        try {
        return ResourceBundle.getBundle("Messages", locale).getString(i18nKey);
        } catch (MissingResourceException e) {
            return i18nKey;
        }
    }

    Type getType();

    String getCategory();

    Item[] getItems();

    String getValue();

    Parameter[] getParameters();

    Consumer<DataSetRow> create(Iterator<Map.Entry<String, JsonNode>> input);
}
