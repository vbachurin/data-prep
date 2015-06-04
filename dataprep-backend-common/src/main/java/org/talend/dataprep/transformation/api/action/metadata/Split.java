package org.talend.dataprep.transformation.api.action.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Item.Value;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Split a cell value on a separator.
 */
@Component(Split.ACTION_BEAN_PREFIX + Split.SPLIT_ACTION_NAME)
public class Split extends SingleColumnAction {

    /** The action name. */
    public static final String SPLIT_ACTION_NAME = "split"; //$NON-NLS-1$

    /** The split column appendix. */
    public static final String SPLIT_APPENDIX = "_split"; //$NON-NLS-1$

    /**
     * The separator shown to the user as a list. An item in this list is the value 'other', which allow the user to
     * manually enter its separator.
     */
    private static final String SEPARATOR_PARAMETER = "separator"; //$NON-NLS-1$

    /** The separator manually specified by the user. Should be used only if SEPARATOR_PARAMETER value is 'other'. */
    private static final String MANUAL_SEPARATOR_PARAMETER = "manual_separator"; //$NON-NLS-1$

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
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    /**
     * @param parameters the action parameters.
     * @return the searator to use according to the given parameters.
     */
    private String getSeparator(Map<String, String> parameters) {
        return ("other").equals(parameters.get(SEPARATOR_PARAMETER)) ? parameters.get(MANUAL_SEPARATOR_PARAMETER) : parameters
                .get(SEPARATOR_PARAMETER);
    }

    /**
     * Split the column for each row.
     *
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {

        return row -> {
            String columnName = parameters.get(COLUMN_ID);
            String realSeparator = getSeparator(parameters);

            String value = row.get(columnName);
            if (value != null) {
                int index = value.indexOf(realSeparator);
                if (index != -1) {
                    row.set(columnName, value.substring(0, index));
                    if (index < value.length()) {
                        row.set(columnName + SPLIT_APPENDIX, value.substring(index + 1));
                    } else {
                        row.set(columnName + SPLIT_APPENDIX, StringUtils.EMPTY);
                    }
                } else {
                    row.set(columnName, value);
                    row.set(columnName + SPLIT_APPENDIX, StringUtils.EMPTY);
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

            String columnId = parameters.get(COLUMN_ID);

            List<ColumnMetadata> newColumns = new ArrayList<>(rowMetadata.size() + 1);

            for (ColumnMetadata column : rowMetadata.getColumns()) {
                ColumnMetadata newColumnMetadata = ColumnMetadata.Builder.column().copy(column).build();
                newColumns.add(newColumnMetadata);

                // append the split column
                if (StringUtils.equals(columnId, column.getId())) {
                    newColumnMetadata = ColumnMetadata.Builder //
                            .column() //
                            .computedId(column.getId() + SPLIT_APPENDIX) //
                            .name(column.getName() + SPLIT_APPENDIX) //
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
