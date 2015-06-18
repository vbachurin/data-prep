package org.talend.dataprep.api.type;

import java.util.List;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.datascience.common.inference.type.DataType;

public class TypeUtils {

    private TypeUtils() {
    }

    /**
     * Compute the dataset metadata columns valid/invalid, empty/count values.
     *
     * @return the dataset column types in DQ libraries.
     * @param columns
     */
    public static DataType.Type[] convert(List<ColumnMetadata> columns) {
        DataType.Type[] types = new DataType.Type[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            final String type = columns.get(i).getType();
            switch (Type.get(type)) {
            case ANY:
            case STRING:
                types[i] = DataType.Type.STRING;
                break;
            case NUMERIC:
                types[i] = DataType.Type.INTEGER;
                break;
            case INTEGER:
                types[i] = DataType.Type.INTEGER;
                break;
            case DOUBLE:
            case FLOAT:
                types[i] = DataType.Type.DOUBLE;
                break;
            case BOOLEAN:
                types[i] = DataType.Type.BOOLEAN;
                break;
            case DATE:
                types[i] = DataType.Type.DATE;
                break;
            case CHAR:
                types[i] = DataType.Type.CHAR;
                break;
            default:
                types[i] = DataType.Type.STRING;
            }
        }
        return types;
    }
}
