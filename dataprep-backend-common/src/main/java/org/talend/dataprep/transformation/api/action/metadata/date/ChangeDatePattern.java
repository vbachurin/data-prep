package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;
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
     * @see ActionMetadata#getItems()@return
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        return getItemsForDatePattern();
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, TransformationContext, Map, String)
     */
    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {

        String newPattern = getNewPattern(parameters);
        DateTimeFormatter newDateFormat = getDateFormat(newPattern);

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
        // loop on the existing pattern to see if the new one is already present or not:
        for (PatternFrequency patternFrequency : statistics.getPatternFrequencies()) {
            if (patternFrequency.getPattern().equals(newPattern)) {
                isNewPatternRegistered = true;
            }
        }
        // if the new pattern is not yet present (ie: we're probably working on the first line)
        if (!isNewPatternRegistered) {
            statistics.getPatternFrequencies().add(new PatternFrequency(newPattern, 1));
            column.setStatistics(statistics);
        }
        // Change the date pattern
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }
        try {
            final LocalDateTime date = superParse(value, row, columnId);
            row.set(columnId, newDateFormat.format(date));
        } catch (DateTimeException e) {
            // cannot parse the date, let's leave it as is
        }
    }

}
