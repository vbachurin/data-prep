package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import java.util.function.Predicate;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;

/**
 * Implements a set of basic rules (see specifications in <a href="https://jira.talendforge.org/browse/TDP-393">Jira</a>
 * ).
 */
public class BasicRules {

    /**
     * A helper to filter {@link Type#NUMERIC} columns.
     */
    protected static final Predicate<ColumnMetadata> IS_NUMERIC = columnMetadata -> {
        final Type type = Type.get(columnMetadata.getType());
        return Type.NUMERIC.isAssignableFrom(type);
    };

    /**
     * A helper to filter {@link Type#STRING} columns.
     */
    protected static final Predicate<ColumnMetadata> IS_STRING = columnMetadata -> {
        final Type type = Type.get(columnMetadata.getType());
        return Type.STRING.isAssignableFrom(type);
    };

}
