package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

@Component(ProperCase.ACTION_BEAN_PREFIX + ProperCase.PROPER_CASE_ACTION_NAME)
public class ProperCase extends SingleColumnAction {

    public static final String PROPER_CASE_ACTION_NAME = "propercase"; //$NON-NLS-1$

    @Override
    public String getName() {
        return PROPER_CASE_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "case";
    }

    @Override
    public BiConsumer<DataSetRow, TransformationContext> create(Map<String, String> parameters) {
        return (row, context) -> {
            String columnName = parameters.get(COLUMN_ID);
            String value = row.get(columnName);
            if (value != null) {
                row.set(columnName, WordUtils.capitalizeFully(value));
            }
        };
    }

    /**
     * @see ActionMetadata#accept(ColumnMetadata)
     */
    @Override
    public boolean accept(ColumnMetadata column) {
        return Type.STRING.equals(Type.get(column.getType()));
    }
}
