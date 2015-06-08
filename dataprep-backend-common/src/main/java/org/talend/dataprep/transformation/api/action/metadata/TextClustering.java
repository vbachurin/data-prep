package org.talend.dataprep.transformation.api.action.metadata;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

@Component(TextClustering.ACTION_BEAN_PREFIX + TextClustering.TEXT_CLUSTERING)
public class TextClustering extends AbstractDynamicAction {

    /** The action name. */
    public static final String TEXT_CLUSTERING = "textclustering";

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return TEXT_CLUSTERING;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return "quickfix";
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public Consumer<DataSetRow> create(Map<String, String> parameters) {
        return row -> {
            final String columnName = parameters.get(COLUMN_ID);
            final String value = row.get(columnName);

            // replace only the value if present in parameters
            final String replaceValue = parameters.get(value);
            if (replaceValue != null) {
                row.set(columnName, replaceValue);
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
