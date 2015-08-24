package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(value = FillWithDateIfInvalid.ACTION_BEAN_PREFIX + FillWithDateIfInvalid.FILL_INVALID_ACTION_NAME)
public class FillWithDateIfInvalid extends AbstractFillIfInvalid {

    public static final String FILL_INVALID_ACTION_NAME = "fillinvalidwithdefaultdate"; //$NON-NLS-1$

    /**
     * Name of the new date parameter.
     */
    protected static final String NEW_DATE = "new_pattern"; //$NON-NLS-1$

    private static final String DATE_PATTERN = "DD/MM/YYYY hh:mm";

    private static final String DEFAULT_DATE_VALUE = new SimpleDateFormat(DATE_PATTERN).format( new Date( 0 ) );

    @Override
    public String getName() {
        return FILL_INVALID_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, Type.DATE.getName(), DEFAULT_DATE_VALUE));
        return parameters;
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get( columnId );
        final ColumnMetadata colMetadata = row.getRowMetadata().getById(columnId);
        final Set<String> invalidValues = colMetadata.getQuality().getInvalidValues();
        if (StringUtils.isEmpty( value ) || invalidValues.contains(value)) {
            // we assume all controls have been made in the ui.
            String newDateValue = parameters.get( DEFAULT_VALUE_PARAMETER );
            row.set(columnId, newDateValue);
        }
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        Type type = Type.get(column.getType());
        return Type.DATE.equals(type);
    }

    public boolean isDate() {
        return true;
    }

}
