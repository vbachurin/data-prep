package org.talend.dataprep.transformation.api.action.metadata;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

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

    /** Number of items produces by the split */
    private static final String LIMIT = "limit"; //$NON-NLS-1$

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

    @Override
    @Nonnull
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER, new Parameter(LIMIT, Type.INTEGER.getName(), "2") };
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
        String columnName = parameters.get(COLUMN_ID);
        String realSeparator = getSeparator(parameters);
        int limit = Integer.parseInt(parameters.get(LIMIT));

        return row -> {
            String originalValue = row.get(columnName);
            if (originalValue != null) {
                String[] split = originalValue.split(realSeparator, limit);
                for (int i = 1; i <= limit; i++) {
                    String newValue = (i <= split.length ? split[i - 1] : StringUtils.EMPTY);
                    row.set(columnName + SPLIT_APPENDIX + "_" + i, newValue);
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
                    for (int i = 1; i <= Integer.parseInt(parameters.get(LIMIT)); i++) {
                        newColumnMetadata = ColumnMetadata.Builder //
                                .column() //
                                .computedId(column.getId() + SPLIT_APPENDIX + "_" + i) //
                                .name(column.getName() + SPLIT_APPENDIX + "_" + i) //
                                .type(Type.get(column.getType())) //
                                .empty(column.getQuality().getEmpty()) //
                                .invalid(column.getQuality().getInvalid()) //
                                .valid(column.getQuality().getValid()) //
                                .headerSize(column.getHeaderSize()) //
                                .build();
                        newColumns.add(newColumnMetadata);
                    }
                }

            }

            // apply the new columns to the row metadata
            rowMetadata.setColumns(newColumns);
        };
    }
}
