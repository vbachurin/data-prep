package org.talend.dataprep.transformation.api.action.metadata.date;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;

import javax.annotation.Nonnull;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.Map;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.talend.dataprep.api.type.Type.INTEGER;

@Component(ComputeTimeSince.ACTION_BEAN_PREFIX + ComputeTimeSince.TIME_SINCE_ACTION_NAME)
public class ComputeTimeSince extends AbstractActionMetadata implements IColumnAction {

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
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.DATE.getDisplayName();
    }

    /**
     * @see ActionMetadata#getItems()@return
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        Item.Value[] values = new Item.Value[]{ //
                new Item.Value(ChronoUnit.YEARS.name(), true), //
                new Item.Value(ChronoUnit.MONTHS.name()), //
                new Item.Value(ChronoUnit.DAYS.name()), //
                new Item.Value(HOURS.name())};
        return new Item[]{new Item(TIME_UNIT_PARAMETER, "categ", values)};
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
        unit = ChronoUnit.valueOf(parameters.get(TIME_UNIT_PARAMETER).toUpperCase());
        now = (unit == HOURS ? LocalDateTime.now() : LocalDate.now());
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final ColumnMetadata column = row.getRowMetadata().getById(columnId);

        // create the new column and add the new column after the current one
        final ColumnMetadata newColumnMetadata = createNewColumn(column);
        row.getRowMetadata().insertAfter(columnId, newColumnMetadata);

        // parse the date
        final DateTimeFormatter dtf = getCurrentDatePatternFormatter(column);
        final String value = row.get(columnId);
        try {
            final TemporalAccessor temporalAccessor = dtf.parse(value, new ParsePosition(0));
            final Temporal valueAsDate = (unit == HOURS ? LocalDateTime.from(temporalAccessor) : LocalDate.from(temporalAccessor));
            final long newValue = unit.between(valueAsDate, now);
            row.set(newColumnMetadata.getId(), newValue + "");
        } catch (DateTimeParseException e) {
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

    /**
     * Get the current date pattern
     *
     * @param column the column metadata
     * @return a new date formatter that fit the current pattern
     */
    private DateTimeFormatter getCurrentDatePatternFormatter(final ColumnMetadata column) {
        //json reader to parse statistics as JSON format
        final JsonFactory jsonFactory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        //get date pattern from statistics
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final JsonNode mostUsedPatternNode = rootNode.get("patternFrequencyTable").get(0); //$NON-NLS-1$
        final String datePattern = mostUsedPatternNode.get("pattern").asText(); //$NON-NLS-1$

        return DateTimeFormatter.ofPattern(datePattern);
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.DATE.equals(Type.get(column.getType()));
    }
}
