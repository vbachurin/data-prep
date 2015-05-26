package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;

@Component(Negate.ACTION_BEAN_PREFIX + Negate.NEGATE_ACTION_NAME)
public class Negate extends SingleColumnAction {

    public static final String NEGATE_ACTION_NAME = "negate"; //$NON-NLS-1$

    private Negate() {
    }

    @Override
    public String getName() {
        return NEGATE_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "boolean"; //$NON-NLS-1$
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

            if (value != null && (value.trim().equalsIgnoreCase("true") || value.trim().equalsIgnoreCase("false"))) { //$NON-NLS-1$ //$NON-NLS-2$
                Boolean boolValue = Boolean.valueOf(value);
                row.set(columnName, WordUtils.capitalizeFully("" + !boolValue)); //$NON-NLS-1$
            }
        };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.BOOLEAN);
    }

}
