package org.talend.dataprep.transformation.api.action.metadata;

/**
 * List all actions category.
 */
public enum ActionCategory {
    COLUMNS("columns"), //
    QUICKFIX("quickfix"), //
    MATH("math"), //
    CASE("case"), //
    DATE("date"), //
    BOOLEAN("boolean"), //
    CLEANSING("cleansing");

    /** The category display name. */
    private final String displayName;

    /**
     * Create an action category with the given display name.
     * 
     * @param displayName the action display name.
     */
    ActionCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the action display name.
     */
    public String getDisplayName() {
        return displayName;
    }
}
