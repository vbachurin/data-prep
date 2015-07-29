package org.talend.dataprep.transformation.api.action.metadata.fillempty;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import javax.annotation.Nonnull;
import java.util.List;

@Component(FillWithFillIfEmpty.ACTION_BEAN_PREFIX + FillWithFillIfEmpty.FILL_EMPTY_ACTION_NAME)
public class FillWithFillIfEmpty extends AbstractFillIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefault"; //$NON-NLS-1$

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY));
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
