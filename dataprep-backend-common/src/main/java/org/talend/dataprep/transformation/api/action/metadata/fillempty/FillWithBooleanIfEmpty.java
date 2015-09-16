package org.talend.dataprep.transformation.api.action.metadata.fillempty;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.SelectParameter;

@Component(FillWithBooleanIfEmpty.ACTION_BEAN_PREFIX + FillWithBooleanIfEmpty.FILL_EMPTY_ACTION_NAME)
public class FillWithBooleanIfEmpty extends AbstractFillIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultboolean"; //$NON-NLS-1$

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(SelectParameter.Builder.builder() //
                .name(DEFAULT_VALUE_PARAMETER) //
                .item("True", "True") //
                .item("False", "False") //
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
