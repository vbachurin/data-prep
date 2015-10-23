package org.talend.dataprep.transformation.api.action.parameters;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Parameter that should be displayed as a select box with columns in the UI.
 */
public class ColumnParameter extends Parameter {

    /** Filters */
    @JsonIgnore // will be part of the Parameter#configuration
    private List<String> filters;

    /** Allow multiple column selection. */
    @JsonIgnore // will be part of the Parameter#configuration
    private boolean allowMultiple;

    /**
     * Full constructor.
     *
     * @param name The parameter name.
     * @param defaultValue The parameter default value.
     * @param implicit True if the parameter is implicit.
     * @param canBeBlank True if the parameter can be blank.
     * @param filters List of filtered column types.
     * @param allowMultiple true if multiple columns can be selected.
     */
    public ColumnParameter(String name, String defaultValue, boolean implicit, boolean canBeBlank, List<String> filters,
            boolean allowMultiple) {
        super(name, ParameterType.COLUMN, defaultValue, implicit, canBeBlank);
        setFilters(filters);
        setAllowMultiple(allowMultiple);
    }

    /**
     * Simplified constructor.
     *
     * @param name The parameter name.
     * @param defaultValue The parameter default value.
     * @param implicit True if the parameter is implicit.
     * @param canBeBlank True if the parameter can be blank.
     * @param filters List of filtered column types.
     */
    public ColumnParameter(String name, String defaultValue, boolean implicit, boolean canBeBlank, List<String> filters) {
        this(name, defaultValue, implicit, canBeBlank, filters, false);
    }

    /**
     * @return the Filters
     */
    public List<String> getFilters() {
        return filters;
    }

    /**
     * @param filters the filters to set.
     */
    public void setFilters(List<String> filters) {
        this.filters = filters;
        addConfiguration("filters", filters);
    }

    /**
     * @return the AllowMultiple
     */
    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    /**
     * @param allowMultiple the allowMultiple to set.
     */
    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
        addConfiguration("allowMultiple", allowMultiple);
    }
}
