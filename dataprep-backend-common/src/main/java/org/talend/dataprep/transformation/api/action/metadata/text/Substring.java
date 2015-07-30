package org.talend.dataprep.transformation.api.action.metadata.text;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.IColumnAction;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Item.Value;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import javax.annotation.Nonnull;
import java.util.Map;

import static org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory.QUICKFIX;

@Component(Substring.ACTION_BEAN_PREFIX + Substring.SUBSTRING_ACTION_NAME)
public class Substring extends AbstractActionMetadata implements IColumnAction {

    /**
     * The action name.
     */
    public static final String SUBSTRING_ACTION_NAME = "substring"; //$NON-NLS-1$

    /**
     * The column appendix.
     */
    private static final String APPENDIX = "_substring"; //$NON-NLS-1$

    private static final String FROM_BEGINNING = "From beginning"; //$NON-NLS-1$
    private static final String TO_END = "To end"; //$NON-NLS-1$

    protected static final String FROM_MODE_PARAMETER = "from_mode"; //$NON-NLS-1$
    protected static final String FROM_INDEX_PARAMETER = "from_index"; //$NON-NLS-1$

    protected static final String TO_MODE_PARAMETER = "to_mode"; //$NON-NLS-1$
    protected static final String TO_INDEX_PARAMETER = "to_index"; //$NON-NLS-1$

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
        return QUICKFIX.getDisplayName();
    }

    /**
     * @see ActionMetadata#getItems()@return
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        final Value[] valuesFrom = new Value[]{ //
                new Value(FROM_BEGINNING, true), //
                new Value("From index", new Parameter(FROM_INDEX_PARAMETER, Type.INTEGER.getName(), "0"))};

        final Value[] valuesTo = new Value[]{ //
                new Value("To end"), //
                new Value("To index", true, new Parameter(TO_INDEX_PARAMETER, Type.INTEGER.getName(), "5"))};

        return new Item[]{new Item(FROM_MODE_PARAMETER, "categ", valuesFrom), new Item(TO_MODE_PARAMETER, "categ", valuesTo)};
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }

    @Override
    protected void beforeApply(Map<String, String> parameters) {
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        // create the new column
        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        final ColumnMetadata newColumnMetadata = createNewColumn(column);
        final String substringColumn = rowMetadata.insertAfter(columnId, newColumnMetadata);

        // Perform substring
        final String value = row.get(columnId);
        if (value == null) {
            return;
        }
        final int realFromIndex = getStartIndex(parameters, value);
        final int realToIndex = getEndIndex(parameters, value);
        final String newValue = (realFromIndex < realToIndex ? value.substring(realFromIndex, realToIndex) : "");
        row.set(substringColumn, newValue);
    }

    /**
     * Compute the end index. This won't be more than the value length
     *
     * @param parameters the parameters
     * @param value      the value to substring
     * @return the end index
     */
    private int getEndIndex(final Map<String, String> parameters, final String value) {
        final String toMode = parameters.get(TO_MODE_PARAMETER);
        if (toMode.equals(TO_END)) {
            return value.length();
        }
        return Math.min(Integer.parseInt(parameters.get(TO_INDEX_PARAMETER)), value.length());
    }

    /**
     * Compute the start index. This won't be more than the value length
     *
     * @param parameters the parameters
     * @param value      the value to substring
     * @return the start index
     */
    private int getStartIndex(final Map<String, String> parameters, String value) {
        final String fromMode = parameters.get(FROM_MODE_PARAMETER);
        final int fromIndex = (fromMode.equals(FROM_BEGINNING) ? 0 : Integer.parseInt(parameters.get(FROM_INDEX_PARAMETER)));
        return Math.min(fromIndex, value.length());
    }

    /**
     * Create a new column
     *
     * @param column the current column metadata
     * @return the new column
     */
    private ColumnMetadata createNewColumn(final ColumnMetadata column) {
        return ColumnMetadata.Builder //
                .column() //
                .name(column.getName() + APPENDIX) //
                .type(Type.get(column.getType())) //
                .empty(column.getQuality().getEmpty()) //
                .invalid(column.getQuality().getInvalid()) //
                .valid(column.getQuality().getValid()) //
                .headerSize(column.getHeaderSize()) //
                .build();
    }
}
