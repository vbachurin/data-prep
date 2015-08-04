package org.talend.dataprep.transformation.api.action.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

import java.util.Map;

import static org.talend.dataprep.exception.error.CommonErrorCodes.*;
import static org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata.ACTION_BEAN_PREFIX;
import static org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters.SCOPE;

@Component
public class ActionMetadataValidation {
    /**
     * The dataprep spring application context.
     */
    @Autowired
    private ApplicationContext context;

    /**
     * Get the scope category from parameters
     *
     * @param parameters the transformation parameters
     * @return the scope
     * @throws TDPException if the scope parameter is missing
     */
    private ScopeCategory getScope(final Map<String, String> parameters) {
        final ScopeCategory scope = ScopeCategory.from(parameters.get(SCOPE.getKey()));
        if (scope == null) {
            throw new TDPException(MISSING_ACTION_SCOPE);
        }
        return scope;
    }

    /**
     * Scope consistency checks
     * 1. scope has mandatory parameters
     * 2. scope is available for the current transformation
     *
     * @param action     the action metadata
     * @param parameters the transformation parameters
     * @throws TDPException if the scope is missing, not supported or
     */
    public void checkScopeConsistency(final ActionMetadata action, final Map<String, String> parameters) {
        final ScopeCategory scope = getScope(parameters);

        if (!action.acceptScope(scope)) {
            throw new TDPException(UNSUPPORTED_ACTION_SCOPE);
        }

        if (!scope.checkMandatoryParameters(parameters)) {
            throw new TDPException(MISSING_ACTION_SCOPE_PARAMETER);
        }
    }

    /**
     * Scope consistency checks from action name
     * 1. scope has mandatory parameters
     * 2. scope is available for the current transformation
     *
     * @param actionName the action name
     * @param parameters the transformation parameters
     * @throws TDPException if the scope is missing, not supported or
     */
    public void checkScopeConsistency(final String actionName, final Map<String, String> parameters) {
        final ActionMetadata actionMetadata = context.getBean(ACTION_BEAN_PREFIX + actionName, ActionMetadata.class);
        checkScopeConsistency(actionMetadata, parameters);
    }
}
