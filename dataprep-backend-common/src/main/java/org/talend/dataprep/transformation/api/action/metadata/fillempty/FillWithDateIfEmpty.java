package org.talend.dataprep.transformation.api.action.metadata.fillempty;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(FillWithDateIfEmpty.ACTION_BEAN_PREFIX + FillWithDateIfEmpty.FILL_EMPTY_ACTION_NAME)
public class FillWithDateIfEmpty extends AbstractFillIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultdate"; //$NON-NLS-1$

    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final String DEFAULT_DATE_VALUE = DEFAULT_FORMATTER.format(LocalDateTime.of(1970, Month.JANUARY, 1, 10, 0));

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, Type.DATE.getName(), DEFAULT_DATE_VALUE, false, false));
        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.DATE.equals(Type.get(column.getType()));
    }

    /**
     * Note: this method is used for JSON serialization and used in web UI, please consider this when making changes.
     * 
     * @return <code>true</code> if this action is date related so the datetimepicker can be display
     */
    public boolean isDate() {
        return true;
    }

}
