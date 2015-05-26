package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(Cut.ACTION_BEAN_PREFIX + Cut.CUT_ACTION_NAME)
public class Cut extends SingleColumnAction {

    public static final String PATTERN_PARAMETER = "pattern"; //$NON-NLS-1$

    public static final String CUT_ACTION_NAME = "cut"; //$NON-NLS-1$

    private Cut() {
    }

    @Override
    public String getName() {
        return CUT_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "quickfix"; //$NON-NLS-1$
    }

    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_NAME_PARAMETER,
                new Parameter(PATTERN_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {
        return row -> {
            String columnName = parameters.get(COLUMN_NAME_PARAMETER_NAME);
            String value = row.get(columnName);
            if (value != null) {
                row.set(columnName, value.replace(parameters.get(PATTERN_PARAMETER), "")); //$NON-NLS-1$
            }
        };
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }

}
