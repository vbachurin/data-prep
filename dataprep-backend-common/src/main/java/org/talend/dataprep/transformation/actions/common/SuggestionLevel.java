package org.talend.dataprep.transformation.actions.common;

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
