package org.talend.dataprep.transformation.actions.common.new_actions_api;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.transformation.actions.category.ActionScope;
import org.talend.dataprep.transformation.actions.category.ScopeCategory;
import org.talend.dataprep.transformation.actions.common.ActionCompileException;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapt legacy ActionMetadata structure to possible new one. It should help keep the same level of capability and be
 * retrocompatible if needed.
 */
public class UserActionAdapter implements ActionMetadata {

    private ActionMetdatadaV2 delegate;

    public UserActionAdapter(ActionMetdatadaV2 delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        // TODO
        return false;
    }

    @Override
    public List<String> getActionScope() {
        return delegate.getScopes().stream().map(ActionScope::getDisplayName).collect(Collectors.toList());
    }

    @Override
    public void compile(org.talend.dataprep.transformation.api.action.context.ActionContext actionContext)
            throws ActionCompileException {
        delegate.compile(ActionContext.fromInternal(actionContext));
    }

    @Override
    public List<Parameter> getParameters() {
        return delegate.getParameters(null);
    }

    @Override
    public boolean acceptScope(ScopeCategory scope) {
        return false;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getCategory() {
        return delegate.getCategory();
    }

    @Override
    public String getLabel() {
        return delegate.getLabel();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public String getDocUrl() {
        return delegate.getDocUrl();
    }

    @Override
    public Set<Behavior> getBehavior() {
        return delegate.getBehavior();
    }

    @Override
    @Deprecated
    public boolean isDynamic() {
        delegate.getRowFilter(java.util.function.Predicate.);

        return false;
    }

    @Override
    @Deprecated
    public ActionMetadata adapt(org.talend.dataprep.api.dataset.ColumnMetadata column) {
        return this;
    }

    @Override
    @Deprecated
    public ActionMetadata adapt(ScopeCategory scope) {
        return this;
    }

    @Override
    @Deprecated
    public boolean implicitFilter() {
        return false;
    }
}
