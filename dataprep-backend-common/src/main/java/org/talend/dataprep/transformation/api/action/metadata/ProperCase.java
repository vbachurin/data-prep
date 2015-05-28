package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;

@Component(ProperCase.ACTION_BEAN_PREFIX + ProperCase.PROPER_CASE_ACTION_NAME)
public class ProperCase extends SingleColumnAction {

    public static final String PROPER_CASE_ACTION_NAME = "propercase"; //$NON-NLS-1$

    @Override
    public String getName() {
        return PROPER_CASE_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "case";
    }

    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_ID);
            String value = row.get(columnName);
            if (value != null) {
                row.set(columnName, WordUtils.capitalizeFully(value));
            }
        };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }
}
