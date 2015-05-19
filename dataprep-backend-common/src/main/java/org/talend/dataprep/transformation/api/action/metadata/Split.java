package org.talend.dataprep.transformation.api.action.metadata;

import java.util.*;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.Item.Value;

/**
 * Split a cell value on a separator.
 */
@Component(Split.ACTION_BEAN_PREFIX + Split.SPLIT_ACTION_NAME)
public class Split extends SingleColumnAction {

    /** The action name. */
    public static final String SPLIT_ACTION_NAME = "split"; //$NON-NLS-1$

    /** The split column appendix. */
    public static final String SPLIT_COLUMN_APPENDIX = "_split"; //$NON-NLS-1$

    /**
     * The separator shown to the user as a list. An item in this list is the value 'other', which allow the user to
     * manually enter its separator.
     */
    private static final String SEPARATOR_PARAMETER = "separator"; //$NON-NLS-1$

    /** The separator manually specified by the user. Should be used only if SEPARATOR_PARAMETER value is 'other'. */
    private static final String MANUAL_SEPARATOR_PARAMETER = "manual_separator"; //$NON-NLS-1$

    /**
     * Private constructor to ensure IoC use.
     */
    private Split() {
    }

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return SPLIT_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return "columns"; //$NON-NLS-1$
    }

    /**
     * @see ActionMetadata#getItems()@return
     */
    @Override
    public Item[] getItems() {
        Value[] values = new Value[] { //
        new Value(":", true), //
                new Value("@"), //
                new Value(" "), //
                new Value("other", new Parameter(MANUAL_SEPARATOR_PARAMETER, Type.STRING.getName(), StringUtils.EMPTY)) };
        return new Item[] { new Item(SEPARATOR_PARAMETER, "categ", values) };
    }

    /**
     * @see ActionMetadata#getCompatibleColumnTypes()
     */
    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.STRING);
    }

    /**
     * @param parameters the action parameters.
     * @return the searator to use according to the given parameters.
     */
    private String getSeparator(Map<String, String> parameters) {
        return (parameters.get(SEPARATOR_PARAMETER).equals("other") ? parameters.get(MANUAL_SEPARATOR_PARAMETER) : parameters
                .get(SEPARATOR_PARAMETER));
    }

    /**
     * Split the column for each row.
     *
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {

        return row -> {
            String columnName = parameters.get(COLUMN_NAME_PARAMETER_NAME);
            String realSeparator = getSeparator(parameters);

            String value = row.get(columnName);
            if (value != null) {
                int index = value.indexOf(realSeparator);
                if (index != -1) {
                    row.set(columnName, value.substring(0, index));
                    if (index < value.length()) {
                        row.set(columnName + SPLIT_COLUMN_APPENDIX, value.substring(index + 1));
                    } else {
                        row.set(columnName + SPLIT_COLUMN_APPENDIX, StringUtils.EMPTY);
                    }
                } else {
                    row.set(columnName, value);
                    row.set(columnName + SPLIT_COLUMN_APPENDIX, StringUtils.EMPTY);
                }
            }
        };
    }

    /**
     * Update row metadata.
     *
     * @see ActionMetadata#createMetadataClosure(Map)
     */
    @Override
    public Consumer<RowMetadata> createMetadataClosure(Map<String, String> parameters) {

        return rowMetadata -> {

            String columnName = parameters.get(COLUMN_NAME_PARAMETER_NAME);

            List<ColumnMetadata> newColumns = new ArrayList<>(rowMetadata.size() + 1);

            for (ColumnMetadata column : rowMetadata.getColumns()) {
                ColumnMetadata newColumnMetadata = ColumnMetadata.Builder.column().copy(column).build();
                newColumns.add(newColumnMetadata);

                // append the split column
                if (StringUtils.equals(columnName, column.getId())) {
                    newColumnMetadata = ColumnMetadata.Builder //
                            .column() //
                            .name(column.getId() + SPLIT_COLUMN_APPENDIX) //
                            .type(Type.get(column.getType())) //
                            .empty(column.getQuality().getEmpty()) //
                            .invalid(column.getQuality().getInvalid()) //
                            .valid(column.getQuality().getValid()) //
                            .headerSize(column.getHeaderSize()) //
                            .build();
                    newColumns.add(newColumnMetadata);
                }

            }

            // apply the new columns to the row metadata
            rowMetadata.setColumns(newColumns);
        };
    }
}
