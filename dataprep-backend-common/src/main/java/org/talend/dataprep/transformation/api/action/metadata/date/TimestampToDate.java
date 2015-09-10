package org.talend.dataprep.transformation.api.action.metadata.date;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import javax.annotation.Nonnull;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.api.type.Type.STRING;

@Component(TimestampToDate.ACTION_BEAN_PREFIX + TimestampToDate.ACTION_NAME)
public class TimestampToDate extends AbstractActionMetadata implements ColumnAction {

    /**
     * The action name.
     */
    public static final String ACTION_NAME = "timestamp_to_date"; //$NON-NLS-1$

    /**
     * The column appendix.
     */
    public static final String APPENDIX = "_as_date"; //$NON-NLS-1$

    /** Name of the new date pattern parameter. */
    protected static final String NEW_PATTERN = "new_pattern"; //$NON-NLS-1$

    /** The parameter object for the custom new pattern. */
    private static final String CUSTOM_PATTERN = "custom_date_pattern"; //$NON-NLS-1$

    /** The parameter object for the custom new pattern. */
    private static final Parameter CUSTOM_PATTERN_PARAMETER = new Parameter(CUSTOM_PATTERN, STRING.getName(), EMPTY);

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ACTION_NAME;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.INTEGER.equals(Type.get(column.getType()));
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

        ResourceBundle patterns = ResourceBundle.getBundle(
                "org.talend.dataprep.transformation.api.action.metadata.date.date_patterns", Locale.ENGLISH);
        Enumeration<String> keys = patterns.getKeys();
        List<Item.Value> values = new ArrayList<>();
        while (keys.hasMoreElements()) {
            Item.Value currentValue = new Item.Value(patterns.getString(keys.nextElement()));
            values.add(currentValue);
        }

        values.add(new Item.Value("custom", CUSTOM_PATTERN_PARAMETER));
        values.get(0).setDefault(true);

        return new Item[] { new Item(NEW_PATTERN, "patterns", values.toArray(new Item.Value[values.size()])) };
    }

    /**
     * @see ColumnAction#applyOnColumn(DataSetRow, TransformationContext, Map, String)
     */
    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {

        String newPattern = getNewPattern(parameters);
        DateTimeFormatter newDateFormat = getDateFormat(newPattern);

        // create new column and append it after current column
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        final String newColumn = rowMetadata.insertAfter(columnId, createNewColumn(column));

        final String value = row.get(columnId);
        row.set(newColumn, apply(value, newDateFormat));
    }

    protected String apply(String from, DateTimeFormatter dateTimeFormatter) {
        try {
            LocalDateTime date = LocalDateTime.ofEpochSecond(Long.parseLong(from), 0, ZoneOffset.UTC);
            final String to = dateTimeFormatter.format(date);
            return to;
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * Create the new "string length" column
     *
     * @param column the current column metadata
     * @return the new column metadata
     */
    private ColumnMetadata createNewColumn(final ColumnMetadata column) {
        return ColumnMetadata.Builder //
                .column() //
                .name(column.getName() + APPENDIX) //
                .type(Type.DATE) //
                .headerSize(column.getHeaderSize()) //
                .build();
    }

    /**
     * @param pattern the date pattern.
     * @return the simple date format out of the parameters.
     */
    private DateTimeFormatter getDateFormat(String pattern) {
        try {
            if (StringUtils.isEmpty(pattern)) {
                throw new IllegalArgumentException();
            }
            return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("pattern '" + pattern + "' is not a valid date pattern", iae);
        }

    }

    /**
     * Get the new pattern from parameters
     *
     * @param parameters the parameters map
     * @return the new date pattern
     */
    private String getNewPattern(Map<String, String> parameters) {
        return "custom".equals(parameters.get(NEW_PATTERN)) ? parameters.get(CUSTOM_PATTERN) : parameters.get(NEW_PATTERN);
    }

}
