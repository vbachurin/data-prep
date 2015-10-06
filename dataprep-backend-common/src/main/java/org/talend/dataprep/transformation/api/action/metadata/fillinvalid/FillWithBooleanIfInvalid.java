package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import java.util.List;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.SelectParameter;

@Component(FillWithBooleanIfInvalid.ACTION_BEAN_PREFIX + FillWithBooleanIfInvalid.FILL_EMPTY_ACTION_NAME)
public class FillWithBooleanIfInvalid extends AbstractFillIfInvalid {

    public static final String FILL_EMPTY_ACTION_NAME = "fillinvalidwithdefaultboolean"; //$NON-NLS-1$

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public List<Parameter> getParameters() {
        List<Parameter> parameters = ImplicitParameters.getParameters();

        parameters.add(SelectParameter.Builder.builder() //
                .name(DEFAULT_VALUE_PARAMETER) //
                .item("True") //
                .item("False") //
                .defaultValue("True") //
                .build());

        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.BOOLEAN.equals(Type.get(column.getType()));
    }

}
