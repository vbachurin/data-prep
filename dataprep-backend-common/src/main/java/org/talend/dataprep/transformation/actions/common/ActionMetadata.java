// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.transformation.actions.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.transformation.actions.category.ActionCategory;
import org.talend.dataprep.transformation.actions.category.ActionScope;
import org.talend.dataprep.transformation.actions.category.ScopeCategory;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import java.util.List;
import java.util.Set;

/**
 * Model an action to perform on a dataset.
 */
public interface ActionMetadata {

    enum Behavior {
        /**
         * Action changes all values in row (e.g. deleting a lines).
         */
        VALUES_ALL,
        /**
         * Action change only the type of the column (not its data) like changing type.
         */
        METADATA_CHANGE_TYPE,
        /**
         * Action change only the name of the column (not its data) like column renaming.
         */
        METADATA_CHANGE_NAME,
        /**
         * Action creates new columns (like splitting).
         */
        METADATA_CREATE_COLUMNS,
        /**
         * Action creates new columns & value but based on an original column.
         */
        METADATA_COPY_COLUMNS,
        /**
         * Action deletes column.
         */
        METADATA_DELETE_COLUMNS,
        /**
         * Action modifies values in this working column.
         */
        VALUES_COLUMN,
        /**
         * Action modifies values in this working column <b>and</b> in all columns used in the action's parameters.
         */
        VALUES_MULTIPLE_COLUMNS,
        /**
         * Action requires up-to-date statistics before it can be executed.
         */
        NEED_STATISTICS
    }

    /**
     * <p>
     * Adapts the current action metadata to the column. This method may return <code>this</code> if no action specific
     * change should be done. It may return a different instance with information from column (like a default value
     * inferred from column's name).
     * </p>
     * <p>
     * Implementations are also expected to return <code>this</code> if {@link #acceptColumn(ColumnMetadata)} returns
     * <code>false</code>.
     * </p>
     *
     * @param column A {@link ColumnMetadata column} information.
     * @return <code>this</code> if any of the following is true:
     * <ul>
     * <li>no change is required.</li>
     * <li>column type is not {@link #acceptColumn(ColumnMetadata) accepted} for current action.</li>
     * </ul>
     * OR a new action metadata with information extracted from <code>column</code>.
     */
    ActionMetadata adapt(ColumnMetadata column);

    /**
     * <p>
     * Adapts the current action metadata to the scope. This method may return <code>this</code> if no action specific
     * change should be done. It may return a different instance with information from scope (like a different label).
     * </p>
     *
     * @param scope A {@link ScopeCategory scope}.
     * @return <code>this</code> if no change is required. OR a new action metadata with information extracted from
     * <code>scope</code>.
     */
    ActionMetadata adapt(final ScopeCategory scope);

    /**
     * Unique identifier of the action.
     *
     * @return A unique name used to identify action.
     */
    String getName();

    /**
     * @return A 'category' for the action used to group similar actions (eg. 'math', 'repair'...).
     * @see ActionCategory
     */
    String getCategory();

    /**
     * Return true if the action can be applied to the given column metadata.
     *
     * @param column the column metadata to transform.
     * @return true if the action can be applied to the given column metadata.
     */
    boolean acceptColumn(final ColumnMetadata column);

    /**
     * @return The label of the action, translated in the user locale.
     */
    String getLabel();

    /**
     * @return The description of the action, translated in the user locale.
     */
    String getDescription();

    /**
     * @return The url of the optionnal help page.
     */
    String getDocUrl();

    /**
     * Defines the list of scopes this action belong to.
     * <p>
     * Scope scope is a concept that allow us to describe on which scope(s) each action can be applied.
     *
     * @return list of scopes of this action
     * @see ActionScope
     */
    List<ActionScope> getActionScope();

    /**
     * TODO: a correct description.
     *
     * @return True if the action is dynamic (i.e the parameters depends on the context
     * (dataset/preparation/previous_actions)
     */
    // Only here for JSON serialization purposes.
    boolean isDynamic();

    /**
     * Return true if the action can be applied to the given scope.
     *
     * @param scope the scope to test
     * @return true if the action can be applied to the given scope.
     */
    boolean acceptScope(final ScopeCategory scope);

    /**
     * Called by transformation process <b>before</b> the first transformation occurs. This method allows action
     * implementation to compute reusable objects in actual transformation execution. Implementations may also indicate
     * that action is not applicable and should be discarded (
     * {@link org.talend.dataprep.transformation.api.action.context.ActionContext.ActionStatus#CANCELED}.
     *
     * @param actionContext The action context that contains the parameters and allows compile step to change action
     *                      status.
     */
    void compile(ActionContext actionContext) throws ActionCompileException;

    /**
     * @return <code>true</code> if there should be an implicit filtering before the action gets executed. Actions that
     * don't want to take care of filtering should return <code>true</code> (default). Implementations may override this
     * method and return <code>false</code> if they want to handle themselves filtering.
     */
    boolean implicitFilter();

    /**
     * @return The list of parameters required for this Action to be executed.
     **/
    List<Parameter> getParameters();

    /**
     * Get the behavior of this action to help optimize flow based on what the action needs and modify.
     *
     * @return The list of this action behaviors
     */
    @JsonIgnore
    Set<Behavior> getBehavior();
}
