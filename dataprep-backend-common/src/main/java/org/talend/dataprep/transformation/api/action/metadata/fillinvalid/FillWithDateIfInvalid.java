package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadataUtils;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import javax.annotation.Nonnull;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Component(value = FillWithDateIfInvalid.ACTION_BEAN_PREFIX + FillWithDateIfInvalid.FILL_INVALID_ACTION_NAME)
public class FillWithDateIfInvalid extends AbstractFillIfInvalid {

    private static final Logger LOGGER = LoggerFactory.getLogger(FillWithDateIfInvalid.class);

    public static final String FILL_INVALID_ACTION_NAME = "fillinvalidwithdefaultdate"; //$NON-NLS-1$

    /**
     * if changing pattern you must change the pattern in the ui as well
     * dataprep-webapp/src/components/transformation/params/date/transformation-date-params.html
     * Yup as usual those bloody Javascript hipsters reinvented the wheel and didn't want to use
     * same pattern as the old school Java guys!!
     */
    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final String DEFAULT_DATE_VALUE = DEFAULT_FORMATTER.format( LocalDateTime.of( 1970, Month.JANUARY, 1, 10, 0 ) );

    @Override
    public String getName() {
        return FILL_INVALID_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, Type.DATE.getName(), DEFAULT_DATE_VALUE, false, false));
        return parameters;
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);

        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        if (column == null) {
            return;
        }

        try {
            if (StringUtils.isEmpty(value) || ActionMetadataUtils.checkInvalidValue(column, value)) {
                // we assume all controls have been made in the ui.
                String newDateStr = parameters.get(DEFAULT_VALUE_PARAMETER);

                // get the most used pattern
                String mostUsedPattern = findMostUsedDatePattern(column);

                // parse the date
                TemporalAccessor temp = parseDateTime(newDateStr, mostUsedPattern);

                // format the result
                String newDateWithFormat = DEFAULT_FORMATTER.format(temp);

                // set the result
                row.set(columnId, newDateWithFormat);

                // update invalid values to prevent future unnecessary analysis
                if (!StringUtils.isEmpty(value)) {
                    final Set<String> invalidValues = column.getQuality().getInvalidValues();
                    invalidValues.add(value);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("skip error parsing date", e);
        }
    }

    /**
     * Return the parsed dated of the given date value.
     *
     * @param dateTimeOrDate the string to parse.
     * @param pattern the pattern to use to parse the date.
     * @return the parsed dated of the given date value.
     */
    private TemporalAccessor parseDateTime(String dateTimeOrDate, String pattern) {

        // first try the LocalDateTime
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDateTime.parse(dateTimeOrDate, formatter);
        } catch (DateTimeException e) {
            // if it fails, let's try the LocalDate
            try {
                LocalDate temp = LocalDate.parse(dateTimeOrDate, formatter);
                return temp.atStartOfDay();
            } catch (DateTimeException e2) {
                // nothing to do here, just throw the exception...
                throw e2;
            }
        }
    }

    protected String findMostUsedDatePattern(final ColumnMetadata column) {
        // get the date pattern to write the date with
        // register the new pattern in column stats, to be able to process date action later
        final Statistics statistics = column.getStatistics();
        final Stream<PatternFrequency> stream = statistics.getPatternFrequencies().stream();
        final Optional<PatternFrequency> mostUsed = stream.sorted((pf1, pf2) -> {
            if (pf1.getOccurrences() - pf2.getOccurrences() == 0) {
                return 0;
            } else if(pf1.getOccurrences() - pf2.getOccurrences() > 0) {
                return -1;
            } else {
                return 1;
            }
        }).findFirst();
        return mostUsed.get().getPattern();
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        Type type = Type.get(column.getType());
        return Type.DATE.equals(type);
    }

    /**
     *
     * @return <code>true</code> if this action is date related so the datetimepicker can be display
     */
    public boolean isDate() {
        return true;
    }

}
