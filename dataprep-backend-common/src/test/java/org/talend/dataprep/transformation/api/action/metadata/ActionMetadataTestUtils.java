package org.talend.dataprep.transformation.api.action.metadata;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;

/**
 * Utility class for action metadata unit tests.
 */
public class ActionMetadataTestUtils {

    /**
     *
     * @param type the wanted column type.
     * @return a new column that matches the given type.
     */
    public static ColumnMetadata getColumn(Type type) {
        return ColumnMetadata.Builder.column().id(0).name("name").type(type).build();
    }

}
