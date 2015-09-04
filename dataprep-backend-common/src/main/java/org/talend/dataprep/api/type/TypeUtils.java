package org.talend.dataprep.api.type;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.datascience.common.inference.semantic.SemanticType;
import org.talend.datascience.common.inference.type.DataType;

public class TypeUtils {

    private TypeUtils() {
    }

    /**
     * Compute the dataset metadata columns valid/invalid, empty/count values.
     *
     * @return the dataset column types in DQ libraries.
     * @param columns The Data Prep {@link ColumnMetadata columns} to convert to DQ library's types.
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
            default:
                types[i] = DataType.Type.STRING;
            }
        }
        return types;
    }

    /**
     * @param semanticType A {@link SemanticType semantic type} as returned by the DQ's {@link org.talend.datascience.common.inference.semantic.SemanticAnalyzer}.
     * @return A display name for the semantic type's suggested category or empty string if none found.
     */
    public static String getDomainLabel(SemanticType semanticType) {
        return getDomainLabel(semanticType.getSuggestedCategory());
    }

    /**
     * @param categoryId A category id from supported {@link SemanticCategoryEnum categories}.
     * @return A display name for the category id or empty string if none found.
     * @see SemanticCategoryEnum
     */
    public static String getDomainLabel(String categoryId) {
        final SemanticCategoryEnum category = SemanticCategoryEnum.getCategoryById(categoryId.toUpperCase());
        return category == null ? StringUtils.EMPTY : category.getDisplayName();
    }
}
