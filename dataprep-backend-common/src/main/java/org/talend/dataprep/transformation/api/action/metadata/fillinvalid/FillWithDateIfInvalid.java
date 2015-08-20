package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(value = FillWithDateIfInvalid.ACTION_BEAN_PREFIX + FillWithDateIfInvalid.FILL_INVALID_ACTION_NAME)
public class FillWithDateIfInvalid
    extends AbstractFillIfInvalid {

    public static final String FILL_INVALID_ACTION_NAME = "fillinvalidwithdefaultdate"; //$NON-NLS-1$

    @Override
    public String getName() {
        return FILL_INVALID_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, Type.DATE.getName(), "0")); //$NON-NLS-1$
        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        Type type = Type.get( column.getType() );

        return Type.DATE.equals( type );
    }

    public boolean isDate(){
        return true;
    }

}
