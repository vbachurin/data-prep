package org.talend.dataprep.transformation.api.action.metadata.date;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.talend.dataprep.api.type.Type.INTEGER;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;

@Component(ComputeTimeSince.ACTION_BEAN_PREFIX + ComputeTimeSince.TIME_SINCE_ACTION_NAME)
public class ComputeTimeSince extends AbstractDate implements ColumnAction {

    /**
     * The action name.
     */
    public static final String TIME_SINCE_ACTION_NAME = "compute_time_since"; //$NON-NLS-1$

    /**
     * The column prefix.
     */
    public static final String PREFIX = "since_"; //$NON-NLS-1$

    /**
     * The column suffix.
     */
    public static final String SUFFIX = "_in_"; //$NON-NLS-1$

    /**
     * The unit in which show the period.
     */
    public static final String TIME_UNIT_PARAMETER = "time_unit"; //$NON-NLS-1$

    /**
     * This class' logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeTimeSince.class);

    /**
     * Temporal unit to use. This must be set before transformation
     */
    private TemporalUnit unit;

    /**
     * Actual time. This must be set before transformation
     */
    private Temporal now;

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return TIME_SINCE_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getItems()@return
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        Item.Value[] values = new Item.Value[] { //
                new Item.Value(ChronoUnit.YEARS.name(), true), //
                new Item.Value(ChronoUnit.MONTHS.name()), //
                new Item.Value(ChronoUnit.DAYS.name()), //
                new Item.Value(HOURS.name()) };
        return new Item[] { new Item(TIME_UNIT_PARAMETER, "categ", values) };
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
        unit = ChronoUnit.valueOf(parameters.get(TIME_UNIT_PARAMETER).toUpperCase());
        now = (unit == HOURS ? LocalDateTime.now() : LocalDate.now());
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, TransformationContext, Map, String)
     */
    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final ColumnMetadata column = row.getRowMetadata().getById(columnId);

        // read the list of patterns from columns metadata:
        List<DateTimeFormatter> patterns = readPatternFromJson(row, columnId);

        // create the new column and add the new column after the current one
        final ColumnMetadata newColumnMetadata = createNewColumn(column);
        row.getRowMetadata().insertAfter(columnId, newColumnMetadata);

        // parse the date
        final String value = row.get(columnId);
        try {
            final TemporalAccessor temporalAccessor = superParse(value, patterns);
            final Temporal valueAsDate = unit == HOURS ? LocalDateTime.from(temporalAccessor) : LocalDate.from(temporalAccessor);
            final long newValue = unit.between(valueAsDate, now);
            row.set(newColumnMetadata.getId(), newValue + "");
        } catch (DateTimeException e) {
            // Nothing to do: in this case, temporalAccessor is left null
            LOGGER.debug("Unable to parse date {}.", value, e);
            row.set(newColumnMetadata.getId(), "");
        }
    }

    /**
     * Create a new column to host the computed time
     *
     * @param column the original column metadata
     * @return the new column metadata
     */
    private ColumnMetadata createNewColumn(ColumnMetadata column) {
        return ColumnMetadata.Builder //
                .column() //
                .copy(column)//
                .name(PREFIX + column.getName() + SUFFIX + unit.toString().toLowerCase()) //
                .computedId(null) // remove the id
                .statistics("{}") // clear the statistics
                .type(INTEGER)//
                .build();
    }

}
