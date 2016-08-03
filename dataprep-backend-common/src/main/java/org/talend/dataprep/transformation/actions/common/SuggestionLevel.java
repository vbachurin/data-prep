package org.talend.dataprep.transformation.actions.common;

/**
 * Suggestion level that a rule can use to associate an {@link ActionMetadata} with user selection conditions on a dataset. The rule
 * can increase the suggestion level of this Action (with {@link #LOW}, {@link #MEDIUM}, {@link #HIGH}, {@link #EMPTY_MGT} or
 * {@link #INVALID_MGT}) or lower it with {@link #NEGATIVE}.
 */
public final class SuggestionLevel {

    private SuggestionLevel() {
    }

    public static final int LOW = 10;

    public static final int MEDIUM = 20;

    public static final int HIGH = 30;

    public static final int EMPTY_MGT = 40;

    public static final int INVALID_MGT = 50;

    public static final int NEGATIVE = -1;

    public static final int NON_APPLICABLE = 0;

}
