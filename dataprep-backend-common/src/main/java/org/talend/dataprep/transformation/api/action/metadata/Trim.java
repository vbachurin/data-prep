package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

@Component(Trim.ACTION_BEAN_PREFIX + Trim.TRIM_ACTION_NAME)
public class Trim extends SingleColumnAction {

    public static final Logger LOGGER           = LoggerFactory.getLogger(Trim.class);

    public static final String TRIM_ACTION_NAME = "trim";                             //$NON-NLS-1$

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
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER_NAME);
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
