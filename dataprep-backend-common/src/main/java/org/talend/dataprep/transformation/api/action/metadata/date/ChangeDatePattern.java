package org.talend.dataprep.transformation.api.action.metadata.date;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Change the date pattern on a 'date' column.
 */
@Component(ChangeDatePattern.ACTION_BEAN_PREFIX + ChangeDatePattern.ACTION_NAME)
public class ChangeDatePattern extends AbstractDate implements ColumnAction, DatePatternParamModel {

    /** Action name. */
    public static final String ACTION_NAME = "change_date_pattern"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ACTION_NAME;
    }


    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public List<Parameter> getParameters() {
        List<Parameter> parameters = super.getParameters();
        parameters.addAll(getParametersForDatePattern());
        return parameters;
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, ActionContext)
     */
    @Override
    public void applyOnColumn(DataSetRow row, ActionContext context) {
        final String columnId = context.getColumnId();
        final Map<String, String> parameters = context.getParameters();

        DatePattern newPattern = getDateFormat(parameters);

        // checks for fail fast
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        if (column == null) {
            return;
        }

        // parse and checks the new date pattern
        // register the new pattern in column stats, to be able to process date action later
        final Statistics statistics = column.getStatistics();
        boolean isNewPatternRegistered = false;
        // loop on the existing patterns to see if the new one is already present or not:
        for (PatternFrequency patternFrequency : statistics.getPatternFrequencies()) {
            if (StringUtils.equals(patternFrequency.getPattern(), newPattern.getPattern())) {
                isNewPatternRegistered = true;
                break;
            }
        }
        // if the new pattern is not yet present (ie: we're probably working on the first line)
        if (!isNewPatternRegistered) {
            long mostUsedPatternCount = getMostUsedPatternCount(column);
            mostUsedPatternCount += 1; // make sure the new pattern is the most important one
            statistics.getPatternFrequencies().add(new PatternFrequency(newPattern.getPattern(), mostUsedPatternCount));
            column.setStatistics(statistics);
        }
        // Change the date pattern
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }
        try {
            final LocalDateTime date = dateParser.parse(value, row.getRowMetadata().getById(columnId));
            row.set(columnId, newPattern.getFormatter().format(date));
        } catch (DateTimeException e) {
            // cannot parse the date, let's leave it as is
        }
    }

    /**
     * Return the count of the most used pattern.
     *
     * @param column the column to work on.
     * @return the count of the most used pattern.
     */
    private long getMostUsedPatternCount(ColumnMetadata column) {
        final List<PatternFrequency> patternFrequencies = column.getStatistics().getPatternFrequencies();
        if (patternFrequencies.isEmpty()) {
            return 1;
        }
        patternFrequencies.sort((p1, p2) -> Long.compare(p2.getOccurrences(), p1.getOccurrences()));
        return patternFrequencies.get(0).getOccurrences();
    }

}
