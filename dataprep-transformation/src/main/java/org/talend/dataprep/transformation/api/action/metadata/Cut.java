package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

@Component(Cut.ACTION_BEAN_PREFIX + Cut.CUT_ACTION_NAME)
public class Cut implements ActionMetadata {

    public static final String COLUMN_NAME_PARAMETER = "column_name"; //$NON-NLS-1$

    public static final String PATTERN_PARAMETER = "pattern"; //$NON-NLS-1$

    public static final String CUT_ACTION_NAME = "cut"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new Cut();

    // Please do not instantiate this class, it is spring Bean automatically instantiated.
    public Cut() {
    }

    @Override
    public String getName() {
        return CUT_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "repair"; //$NON-NLS-1$
    }

    @Override
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY),
                new Parameter(PATTERN_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (value != null) {
                row.set(columnName, value.replace(parsedParameters.get(PATTERN_PARAMETER), "")); //$NON-NLS-1$
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprep.transformation.api.action.metadata.ActionMetadata#getCompatibleColumnTypes()
     */
    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }

}
