package org.talend.dataprep.transformation.api.action.metadata.fillempty;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.date.DateParser;
import org.talend.dataprep.transformation.api.action.metadata.date.DatePattern;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.ParameterType;

@Component(FillWithDateIfEmpty.ACTION_BEAN_PREFIX + FillWithDateIfEmpty.FILL_EMPTY_ACTION_NAME)
public class FillWithDateIfEmpty extends AbstractFillIfEmpty {

    public static final String FILL_EMPTY_ACTION_NAME = "fillemptywithdefaultdate"; //$NON-NLS-1$

    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final String DEFAULT_DATE_VALUE = DEFAULT_FORMATTER.format(LocalDateTime.of(1970, Month.JANUARY, 1, 10, 0));

    /**
     * Component that parses dates.
     */
    private DateParser dateParser = new DateParser(); // TODO investigate why this instantiation is required, should be auto with Autowired

    @Override
    public String getName() {
        return FILL_EMPTY_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, ParameterType.DATE, DEFAULT_DATE_VALUE, false, false));
        return parameters;
    }

    @Override
    protected String getDefaultValue(DataSetRow row, Map<String, String> parameters, String columnId) {
        final ColumnMetadata columnMetadata = row.getRowMetadata().getById(columnId);
        final LocalDateTime date = dateParser.parse(parameters.get(DEFAULT_VALUE_PARAMETER), columnMetadata);
        final DatePattern mostFrequentPattern = dateParser.getMostFrequentPattern(columnMetadata);

        DateTimeFormatter ourNiceFormatter = (mostFrequentPattern == null ? DEFAULT_FORMATTER : mostFrequentPattern
                .getFormatter());

        return ourNiceFormatter.format(date);
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.DATE.equals(Type.get(column.getType()));
    }

}

