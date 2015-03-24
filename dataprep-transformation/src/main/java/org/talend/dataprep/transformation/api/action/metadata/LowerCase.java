package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.api.type.Type;

@Component(LowerCase.ACTION_BEAN_PREFIX + LowerCase.LOWER_CASE_ACTION_NAME)
public class LowerCase implements ActionMetadata {

    public static final String COLUMN_NAME_PARAMETER = "column_name"; //$NON-NLS-1$

    public static final String LOWER_CASE_ACTION_NAME = "lowercase"; //$NON-NLS-1$

    public static LowerCase INSTANCE = new LowerCase();

    // Please do not instanciate this class, it is spring Bean automatically instanciated.
    public LowerCase() {
    }

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
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY) };
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER);
            String value = row.get(columnName);
            if (value != null) {
                row.set(columnName, value.toLowerCase());
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
