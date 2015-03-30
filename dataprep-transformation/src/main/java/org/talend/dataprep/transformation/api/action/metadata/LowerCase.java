package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

@Component(LowerCase.ACTION_BEAN_PREFIX + LowerCase.LOWER_CASE_ACTION_NAME)
public class LowerCase extends SingleColumnAction {

    public static final String LOWER_CASE_ACTION_NAME = "lowercase"; //$NON-NLS-1$

    @Override
    public String getName() {
        return LOWER_CASE_ACTION_NAME;
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
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_NAME_PARAMETER };
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER_NAME);
            String value = row.get(columnName);
            if (value != null) {
                row.set(columnName, value.toLowerCase());
            }
        };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }
}
