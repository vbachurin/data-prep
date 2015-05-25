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

@Component(UpperCase.ACTION_BEAN_PREFIX + UpperCase.UPPER_CASE_ACTION_NAME)
public class UpperCase extends SingleColumnAction {

    public static final String UPPER_CASE_ACTION_NAME = "uppercase"; //$NON-NLS-1$

    private UpperCase() {
    }

    @Override
    public String getName() {
        return UPPER_CASE_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "case"; //$NON-NLS-1$
    }

    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {
        return row -> {
            String columnName = parameters.get(COLUMN_NAME_PARAMETER_NAME);
            String value = row.get(columnName);
            if (value != null) {
                row.set(columnName, value.toUpperCase());
            }
        };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }
}
