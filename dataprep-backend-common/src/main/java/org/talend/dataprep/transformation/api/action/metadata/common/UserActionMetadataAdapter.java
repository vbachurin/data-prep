package org.talend.dataprep.transformation.api.action.metadata.common;

import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to ease the implementation of an action by a third-party. It will be deleted when adapters are unified after i18n system
 * is refactored.
 */
public abstract class UserActionMetadataAdapter implements ActionMetadata {

    public static final String USER_ACTIONS_CATEGORY = "user defined";

    private MessageSource messageSource;

    protected UserActionMetadataAdapter() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename(getActionCode());
        resourceBundleMessageSource.setFallbackToSystemLocale(false);
        resourceBundleMessageSource.setDefaultEncoding(Charsets.UTF_8.name());
        messageSource = resourceBundleMessageSource;
    }

    @Override
    public UserActionMetadataAdapter adapt(ColumnMetadata column) {
        return this;
    }

    @Override
    public UserActionMetadataAdapter adapt(final ScopeCategory scope) {
        return this;
    }

    @Override
    public String getCategory() {
        return USER_ACTIONS_CATEGORY;
    }

    /**
     * Returns the code used in resource bundle entries for this action.
     * entries are by default: action.{action_code}.{label|desc|url}.
     * <p>
     * It is recommended to override it to avoid using the action name as action code.
     */
    protected String getActionCode() {
        return getName();
    }

    @Override
    public String getLabel() {
        return getLocalizedActionMessage(getActionCode() + ".label");
    }

    @Override
    public String getDescription() {
        return getLocalizedActionMessage(getActionCode() + ".desc");
    }

    @Override
    public String getDocUrl() {
        return getLocalizedActionMessage(getActionCode() + ".url", StringUtils.EMPTY);
    }

    @Override
    public List<String> getActionScope() {
        return new ArrayList<>();
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean acceptScope(final ScopeCategory scope) {
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

    @Override
    public boolean implicitFilter() {
        return true;
    }

    @Override
    public List<Parameter> getParameters() {
        return ImplicitParameters.getParameters();
    }

    protected String getLocalizedActionMessage(String code, Object... parameters) {
        return getLocalizedMessage("action." + code, parameters);
    }

    protected String getLocalizedMessage(String code, Object... parameters) {
        return messageSource.getMessage(code, parameters, LocaleContextHolder.getLocale());
    }

}
