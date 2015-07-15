package org.talend.dataprep.transformation.api.action.metadata;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

@Component(Substring.ACTION_BEAN_PREFIX + Substring.SUBSTRING_ACTION_NAME)
public class Substring extends SingleColumnAction {

    /** The action name. */
    public static final String SUBSTRING_ACTION_NAME = "substring"; //$NON-NLS-1$

    /** The column appendix. */
    public static final String APPENDIX = "_substring"; //$NON-NLS-1$

    public static final String FROM_PARAMETER = "from"; //$NON-NLS-1$

    public static final String TO_PARAMETER = "to"; //$NON-NLS-1$

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
     * @see ActionMetadata#getParameters()
     */
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER, COLUMN_NAME_PARAMETER,
                new Parameter(FROM_PARAMETER, Type.NUMERIC.getName(), StringUtils.EMPTY),
                new Parameter(TO_PARAMETER, Type.NUMERIC.getName(), StringUtils.EMPTY) };
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Action create(Map<String, String> parameters) {
        String columnId = parameters.get(COLUMN_ID);
        int fromIndex = Integer.parseInt(parameters.get(FROM_PARAMETER));
        int toIndex = Integer.parseInt(parameters.get(TO_PARAMETER));

        return builder().withRow((row, context) -> {
            String value = row.get(columnId);
            if (value != null) {
                String newValue = value.substring(fromIndex, toIndex);

                List<String> rowIds = row.values().keySet().stream().collect(Collectors.toList());
                Integer nextSplitIndex = getNextAvailableSplitIndex(rowIds, columnId);
                row.set(columnId + APPENDIX + "_" + nextSplitIndex, newValue);
            }
        }).withMetadata((rowMetadata, context) -> {
                // go through the columns to be able to 'insert' the new columns just after the one needed.
                for (int i = 0; i < rowMetadata.getColumns().size(); i++) {
                    ColumnMetadata column = rowMetadata.getColumns().get(i);
                    if (!StringUtils.equals(column.getId(), columnId)) {
                        continue;
                    }
                        // get the new column id
                        List<String> columnIds = new ArrayList<>(rowMetadata.size());
                        rowMetadata.getColumns().forEach(columnMetadata -> columnIds.add(columnMetadata.getId()));

                        Integer nextAvailableSplitIndex = getNextAvailableSplitIndex(columnIds, column.getId());
                        if (nextAvailableSplitIndex == null) {
                            // if this happen, let's not break anything
                            break;
                        }
                        // create the new column
                        ColumnMetadata newColumnMetadata = ColumnMetadata.Builder //
                                .column() //
                            .computedId(column.getId() + APPENDIX + '_' + nextAvailableSplitIndex) //
                            .name(column.getName() + APPENDIX + '_' + nextAvailableSplitIndex) //
                                .type(Type.get(column.getType())) //
                                .empty(column.getQuality().getEmpty()) //
                                .invalid(column.getQuality().getInvalid()) //
                                .valid(column.getQuality().getValid()) //
                                .headerSize(column.getHeaderSize()) //
                                .build();
                        // add the new column after the current one
                    rowMetadata.getColumns().add(i + 1, newColumnMetadata);
                }
            }).build();
    }

    /**
     * Copied from Split TODO refactor to reduce code duplication
     */
    private Integer getNextAvailableSplitIndex(List<String> columnsId, String columnId) {

        for (int i = 1; i < 1000; i++) {
            String temp = columnId + APPENDIX + '_' + i;
            if (!columnsId.contains(temp)) {
                return i;
            }
        }

        return null;
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }
}
