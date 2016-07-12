package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

import java.util.function.Predicate;

/**
 * Basic predicates to help building suggestions.
 */
public final class ColumnPredicates {

    private static final Predicate<ColumnMetadata> IS_NUMERIC = columnMetadata -> {
        final Type type = Type.get(columnMetadata.getType());
        return Type.NUMERIC.isAssignableFrom(type);
    };

    private static final Predicate<ColumnMetadata> IS_STRING = columnMetadata -> {
        final Type type = Type.get(columnMetadata.getType());
        return Type.STRING.equals(type);
    };

    private static final Predicate<ColumnMetadata> IS_DATE = columnMetadata -> {
        final Type type = Type.get(columnMetadata.getType());
        return Type.DATE.isAssignableFrom(type) || "date".equalsIgnoreCase(columnMetadata.getDomain());
    };

    private static final Predicate<ColumnMetadata> IS_EMAIL = columnMetadata -> SemanticCategoryEnum.EMAIL.getId()
            .equalsIgnoreCase(columnMetadata.getDomain());

    private static final Predicate<ColumnMetadata> IS_URL = columnMetadata -> SemanticCategoryEnum.URL.getId()
            .equalsIgnoreCase(columnMetadata.getDomain());

    private static final Predicate<ColumnMetadata> IS_PHONE = columnMetadata -> columnMetadata.getDomain().toLowerCase()
            .endsWith("phone");

    private ColumnPredicates() {
    }

    /**
     * A helper to filter {@link Type#NUMERIC} columns.
     */
    public static Predicate<ColumnMetadata> isNumeric() {
        return IS_NUMERIC;
    }

    /**
     * A helper to filter {@link Type#STRING} columns.
     */
    public static Predicate<ColumnMetadata> isString() {
        return IS_STRING;
    }

    /**
     * A helper to filter {@link Type#DATE} columns (including column where semantic domain is 'date').
     */
    public static Predicate<ColumnMetadata> isDate() {
        return IS_DATE;
    }

    /**
     * A helper to filter columns where semantic domain=email.
     */
    public static Predicate<ColumnMetadata> isEmail() {
        return IS_EMAIL;
    }

    /**
     * A helper to filter columns where semantic domain=url.
     */
    public static Predicate<ColumnMetadata> isUrl() {
        return IS_URL;
    }

    /**
     * A helper to filter columns where semantic domain=phone.
     */
    public static Predicate<ColumnMetadata> isPhone() {
        return IS_PHONE;
    }
}
