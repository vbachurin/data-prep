package org.talend.dataprep.transformation.api.action.metadata.date;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import javax.annotation.Nonnull;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.talend.dataprep.api.type.Type.BOOLEAN;
import static org.talend.dataprep.api.type.Type.DATE;

/**
 * Change the date pattern on a 'date' column.
 */
@Component(ExtractDateTokens.ACTION_BEAN_PREFIX + ExtractDateTokens.ACTION_NAME)
public class ExtractDateTokens extends AbstractActionMetadata implements IColumnAction {

    /**
     * Action name.
     */
    public static final String ACTION_NAME = "extract_date_tokens"; //$NON-NLS-1$

    private static final String SEPARATOR = "_";

    private static final String YEAR = "YEAR";

    private static final String MONTH = "MONTH";

    private static final String DAY = "DAY";

    private static final String HOUR_12 = "HOUR_12";

    private static final String HOUR_24 = "HOUR_24";

    private static final String MINUTE = "MINUTE";

    private static final String AM_PM = "AM_PM";

    private static final String SECOND = "SECOND";

    private static final String DAY_OF_WEEK = "DAY_OF_WEEK";

    private static final String DAY_OF_YEAR = "DAY_OF_YEAR";

    private static final String WEEK_OF_YEAR = "WEEK_OF_YEAR";

    private static final DateFieldMappingBean[] DATE_FIELDS = new DateFieldMappingBean[]{//
            new DateFieldMappingBean(YEAR, ChronoField.YEAR),//
            new DateFieldMappingBean(MONTH, ChronoField.MONTH_OF_YEAR),//
            new DateFieldMappingBean(DAY, ChronoField.DAY_OF_MONTH), //
            new DateFieldMappingBean(HOUR_12, ChronoField.HOUR_OF_AMPM), //
            new DateFieldMappingBean(AM_PM, ChronoField.AMPM_OF_DAY), //
            new DateFieldMappingBean(HOUR_24, ChronoField.HOUR_OF_DAY), //
            new DateFieldMappingBean(MINUTE, ChronoField.MINUTE_OF_HOUR), //
            new DateFieldMappingBean(SECOND, ChronoField.SECOND_OF_MINUTE), //
            new DateFieldMappingBean(DAY_OF_WEEK, ChronoField.DAY_OF_WEEK), //
            new DateFieldMappingBean(DAY_OF_YEAR, ChronoField.DAY_OF_YEAR), //
            new DateFieldMappingBean(WEEK_OF_YEAR, ChronoField.ALIGNED_WEEK_OF_YEAR), //
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractDateTokens.class);

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(YEAR, BOOLEAN.getName(), "true"));
        parameters.add(new Parameter(MONTH, BOOLEAN.getName(), "true"));
        parameters.add(new Parameter(DAY, BOOLEAN.getName(), "true"));
        parameters.add(new Parameter(HOUR_12, BOOLEAN.getName(), "false"));
        parameters.add(new Parameter(AM_PM, BOOLEAN.getName(), "false"));
        parameters.add(new Parameter(HOUR_24, BOOLEAN.getName(), "true"));
        parameters.add(new Parameter(MINUTE, BOOLEAN.getName(), "true"));
        parameters.add(new Parameter(SECOND, BOOLEAN.getName(), "false"));
        parameters.add(new Parameter(DAY_OF_WEEK, BOOLEAN.getName(), "false"));
        parameters.add(new Parameter(DAY_OF_YEAR, BOOLEAN.getName(), "false"));
        parameters.add(new Parameter(WEEK_OF_YEAR, BOOLEAN.getName(), "false"));
        return parameters;
    }

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.DATE.getDisplayName();
    }

    /**
     * Only works on 'date' columns.
     *
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return DATE.equals(Type.get(column.getType()));
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);

        // Create new columns for date tokens
        final Map<String, String> dateFieldColumns = new HashMap<>();
        for (DateFieldMappingBean date_field : DATE_FIELDS) {
            if (Boolean.valueOf(parameters.get(date_field.key))) {
                final String newColumn = rowMetadata.insertAfter(columnId, createNewColumn(column, date_field.key));
                dateFieldColumns.put(date_field.key, newColumn);
            }
        }

        // Get the most used pattern formatter and parse the date
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }
        final DateTimeFormatter dtf = getMostUsedPatternFormatter(column);
        TemporalAccessor temporalAccessor = null;
        try {
            temporalAccessor = dtf.parse(value, new ParsePosition(0));
        } catch (DateTimeParseException e) {
            // temporalAccessor is left null, this will be used bellow to set empty new value for all fields
            LOGGER.debug("Unable to parse date {}.", value, e);
        }

        // insert new extracted values
        for (final DateFieldMappingBean date_field : DATE_FIELDS) {
            if (Boolean.valueOf(parameters.get(date_field.key))) {
                String newValue = StringUtils.EMPTY;
                if (temporalAccessor != null && // may occurs if date can not be parsed with pattern
                        temporalAccessor.isSupported(date_field.field)) {
                    newValue = String.valueOf(temporalAccessor.get(date_field.field));
                }
                row.set(dateFieldColumns.get(date_field.key), newValue);
            }
        }
    }

    /**
     * Create a new column to host the computed extracted data
     *
     * @param column the original column metadata
     * @return the new column metadata
     */
    private ColumnMetadata createNewColumn(final ColumnMetadata column, final String suffix) {
        return ColumnMetadata.Builder //
                .column() //
                .name(column.getName() + SEPARATOR + suffix) //
                .type(Type.INTEGER) //
                .empty(column.getQuality().getEmpty()) //
                .invalid(column.getQuality().getInvalid()) //
                .valid(column.getQuality().getValid()) //
                .headerSize(column.getHeaderSize()) //
                .build();
    }

    /**
     * Get the current date pattern
     *
     * @param column the column metadata
     * @return a new date formatter that fit the current pattern
     */
    private DateTimeFormatter getMostUsedPatternFormatter(final ColumnMetadata column) {
        final JsonFactory jsonFactory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final String datePattern = rootNode.get("patternFrequencyTable") //$NON-NLS-1$
                .get(0) //
                .get("pattern") //$NON-NLS-1$
                .asText();
        return DateTimeFormatter.ofPattern(datePattern);
    }


    private static class DateFieldMappingBean {
        private final String key;
        private final ChronoField field;

        public DateFieldMappingBean(String key, ChronoField field) {
            super();
            this.key = key;
            this.field = field;
        }
    }
}
