package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;

@Component(Trim.ACTION_BEAN_PREFIX + Trim.TRIM_ACTION_NAME)
public class Trim extends SingleColumnAction {

    public static final String TRIM_ACTION_NAME = "trim"; //$NON-NLS-1$

    private Trim() {
    }

    @Override
    public String getName() {
        return TRIM_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "quickfix"; //$NON-NLS-1$
    }

    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_ID_PARAMETER_NAME);
            String value = row.get(columnName);

            if (value != null) {
                String newValue = value.trim();
                row.set(columnName, newValue);
            }
        };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }

}
