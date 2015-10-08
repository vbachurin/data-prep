package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.ParameterType;

@Component(FillWithStringIfInvalid.ACTION_BEAN_PREFIX + FillWithStringIfInvalid.FILL_INVALID_ACTION_NAME)
public class FillWithStringIfInvalid extends AbstractFillIfInvalid {

    public static final String FILL_INVALID_ACTION_NAME = "fillinvalidwithdefault"; //$NON-NLS-1$

    @Override
    public String getName() {
        return FILL_INVALID_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, ParameterType.STRING, StringUtils.EMPTY, false, false));
        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

}
