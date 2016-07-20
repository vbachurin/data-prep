package org.talend.dataprep.transformation.actions.common;

/**
 * Suggestion level that a rule can use to associate an {@link ActionMetadata} with user selection conditions on a dataset. The rule
 * can increase the suggestion level of this Action (with {@link #LOW}, {@link #MEDIUM}, {@link #HIGH}, {@link #EMPTY_MGT} or
 * {@link #INVALID_MGT}) or lower it with {@link #NEGATIVE}.
 */
public enum SuggestionLevel {
    LOW(10),
    MEDIUM(20),
    HIGH(30),
    EMPTY_MGT(40),
    INVALID_MGT(50),
    NEGATIVE(-10),
    NON_APPLICABLE(0);

    private final int score;

    SuggestionLevel(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
