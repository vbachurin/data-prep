package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Item.Value;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(Substring.ACTION_BEAN_PREFIX + Substring.SUBSTRING_ACTION_NAME)
public class Substring extends SingleColumnAction {

    /** The action name. */
    public static final String SUBSTRING_ACTION_NAME = "substring"; //$NON-NLS-1$

    /** The column appendix. */
    public static final String APPENDIX = "_substring"; //$NON-NLS-1$

    public static final String FROM_MODE_PARAMETER = "from_mode"; //$NON-NLS-1$

    public static final String FROM_INDEX_PARAMETER = "from_index"; //$NON-NLS-1$

    public static final String TO_MODE_PARAMETER = "to_mode"; //$NON-NLS-1$

    public static final String TO_INDEX_PARAMETER = "to_index"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return SUBSTRING_ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#getItems()@return
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        Value[] valuesFrom = new Value[] { //
        new Value("From beginning", true), //
                new Value("From index", new Parameter(FROM_INDEX_PARAMETER, Type.INTEGER.getName(), "0")) };

        Value[] valuesTo = new Value[] { //
        new Value("To end"), //
                new Value("To index", true, new Parameter(TO_INDEX_PARAMETER, Type.INTEGER.getName(), "5")) };

        return new Item[] { new Item(FROM_MODE_PARAMETER, "categ", valuesFrom), new Item(TO_MODE_PARAMETER, "categ", valuesTo) };
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Action create(Map<String, String> parameters) {
        return builder().withRow((row, context) -> {
            String columnId = parameters.get(COLUMN_ID);
            final RowMetadata rowMetadata = row.getRowMetadata();
            final ColumnMetadata column = rowMetadata.getById(columnId);
            // create the new column
            ColumnMetadata newColumnMetadata = ColumnMetadata.Builder //
                    .column() //
                    .name(column.getName() + APPENDIX) //
                    .type(Type.get(column.getType())) //
                    .empty(column.getQuality().getEmpty()) //
                    .invalid(column.getQuality().getInvalid()) //
                    .valid(column.getQuality().getValid()) //
                    .headerSize(column.getHeaderSize()) //
                    .build();
            // add the new column after the current one
            final String substringColumn = rowMetadata.insertAfter(columnId, newColumnMetadata);
            // Perform substring
            String value = row.get(columnId);
            if (value == null) {
                return row;
            }
            String fromMode = parameters.get(FROM_MODE_PARAMETER);
            String toMode = parameters.get(TO_MODE_PARAMETER);
            int fromIndex = (fromMode.equals("From beginning") ? 0 : Integer.parseInt(parameters.get(FROM_INDEX_PARAMETER)));
            int realFromIndex = Math.min(fromIndex, value.length());
            int realToIndex = (toMode.equals("To end") ? value.length() : Math.min(
                    Integer.parseInt(parameters.get(TO_INDEX_PARAMETER)), value.length()));
            String newValue = (realFromIndex < realToIndex ? value.substring(realFromIndex, realToIndex) : "");
            row.set(substringColumn, newValue);
            return row;
        }).build();
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }
}
