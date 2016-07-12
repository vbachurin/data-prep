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
import com.google.common.base.CaseFormat;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.i18n.AbstractBundle;
import org.talend.dataprep.i18n.DataprepBundle;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.transformation.actions.category.ScopeCategory;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Adapter for {@link ActionMetadata} to have default implementation and behavior for actions. Every dataprep actions
 * derive from it but it is not an obligation.
 */
public abstract class AbstractActionMetadata implements ActionMetadata {

    public static final String ACTION_BEAN_PREFIX = "action#"; //$NON-NLS-1$

    private final String defaultActionName = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE)
            .convert(getClass().getSimpleName());

    /**
     * Default implementation that returns {@code this}.
     * {@inheritDoc}
     */
    @Override
    public AbstractActionMetadata adapt(ColumnMetadata column) {
        return this;
    }

    /**
     * Default implementation that returns {@code this}.
     * {@inheritDoc}
     */
    @Override
    public AbstractActionMetadata adapt(final ScopeCategory scope) {
        return this;
    }

    /**
     * Default implementation of {@link ActionMetadata#getName()} that uses the action implementation class name in lower
     * underscore case  as unique action name.
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return defaultActionName;
    }

    /**
     * @return The label of the action, translated in the user locale.
     */
    @Override
    public String getLabel() {
        return DataprepBundle.message("action." + getName() + ".label");
    }

    /**
     * @return The description of the action, translated in the user locale.
     */
    @Override
    public String getDescription() {
        return DataprepBundle.message("action." + getName() + ".desc");
    }

    /**
     * @return The url of the optionnal help page.
     */
    @Override
    public String getDocUrl() {
        return DataprepBundle.message("action." + getName() + ".url");
    }

    /**
     * Default implementation that returns the {@link Collections#emptyList() emptyList()}.
     */
    @Override
    public List<String> getActionScope() {
        return Collections.emptyList();
    }

    /**
     * TODO Only here for JSON serialization purposes.
     *
     * @return True if the action is dynamic (i.e the parameters depends on the context
     * (dataset/preparation/previous_actions)
     */
    @Override
    public boolean isDynamic() {
        return false;
    }

    /**
     * Return true if the action can be applied to the given scope.
     *
     * @param scope the scope to test
     * @return true if the action can be applied to the given scope.
     */
    @Override
    public final boolean acceptScope(final ScopeCategory scope) {
        switch (scope) {
        case CELL:
            return this instanceof CellAction;
        case LINE:
            return this instanceof RowAction;
        case COLUMN:
            return this instanceof ColumnAction;
        case DATASET:
            return this instanceof DataSetAction;
        default:
            return false;
        }
    }

    /**
     * Called by transformation process <b>before</b> the first transformation occurs. This method allows action
     * implementation to compute reusable objects in actual transformation execution. Implementations may also indicate
     * that action is not applicable and should be discarded (
     * {@link ActionContext.ActionStatus#CANCELED}.
     *
     * @param actionContext The action context that contains the parameters and allows compile step to change action
     *                      status.
     * @see ActionContext#setActionStatus(ActionContext.ActionStatus)
     */
    @Override
    public void compile(ActionContext actionContext) {
        final RowMetadata input = actionContext.getRowMetadata();
        final ScopeCategory scope = actionContext.getScope();
        if (scope != null) {
            switch (scope) {
            case CELL:
            case COLUMN:
                // Stop action if: there's actually column information in input AND column is not found
                if (input != null && !input.getColumns().isEmpty() && input.getById(actionContext.getColumnId()) == null) {
                    actionContext.setActionStatus(ActionContext.ActionStatus.CANCELED);
                    return;
                }
                break;
            case LINE:
            case DATASET:
            default:
                break;
            }
        }
        actionContext.setActionStatus(ActionContext.ActionStatus.OK);
    }

    /**
     * @return <code>true</code> if there should be an implicit filtering before the action gets executed. Actions that
     * don't want to take care of filtering should return <code>true</code> (default). Implementations may override this
     * method and return <code>false</code> if they want to handle themselves filtering.
     */
    @Override
    public boolean implicitFilter() {
        return true;
    }

    /**
     * @return The list of parameters required for this Action to be executed.
     **/
    @Override
    public List<Parameter> getParameters() {
        return ImplicitParameters.getParameters();
    }

    @JsonIgnore
    @Override
    public abstract Set<ActionMetadata.Behavior> getBehavior();

    /**
     * Default implmeentation that returns null to let the rules do the suggestion for this action.
     * <p>{@inheritDoc}
     */
    @Override
    public SuggestionLevel getSuggestionScore(ColumnMetadata column) {
        return null;
    }

    @JsonIgnore
    protected AbstractBundle getMessagesBundle() {
        return DataprepBundle.getDataprepBundle();
    }
}
