package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.api.type.Types;

@Configuration
public class Cut implements ActionMetadata {

    public static final String COLUMN_NAME_PARAMETER = "column_name"; //$NON-NLS-1$

    public static final String PATTERN_PARAMETER = "pattern"; //$NON-NLS-1$

    public static final String CUT_ACTION_NAME = "cut"; //$NON-NLS-1$

    public static final ActionMetadata INSTANCE = new Cut();

    @Bean(name = ACTION_BEAN_PREFIX + CUT_ACTION_NAME)
    public ActionMetadata createInstance() {
        return new Cut();
    }

    // Please do not instanciate this class, it is spring Bean automatically instanciated.
    public Cut() {
    }

    @Override
    public String getName() {
        return CUT_ACTION_NAME;
    }

    @Override
    public Type getType() {
        return Type.OPERATION;
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
    public String getValue() {
        return StringUtils.EMPTY;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter(COLUMN_NAME_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY),
                new Parameter(PATTERN_PARAMETER, Types.STRING.getName(), StringUtils.EMPTY) };
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

}
