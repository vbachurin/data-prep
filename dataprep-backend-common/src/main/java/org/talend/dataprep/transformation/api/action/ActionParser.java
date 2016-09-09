//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.transformation.api.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Actions;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.actions.common.ActionFactory;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;
import org.talend.dataprep.transformation.pipeline.ActionRegistry;

/**
 * Parse the actions a dataset and prepare the closures to apply.
 */
@Component
public class ActionParser {

    @Autowired
    ActionFactory factory;

    @Autowired
    private ActionRegistry actionRegistry;

    /** The dataprep ready jackson builder. */
    @Autowired
    private ObjectMapper mapper;

    /**
     * Return the parsed actions ready to be run.
     *
     * @param actions the actions to be parsed as string.
     * @return the parsed actions.
     */
    public List<Action> parse(String actions) {
        if (actions == null) {
            // Actions cannot be null (but can be empty string for no op actions).
            throw new IllegalArgumentException("Actions parameter can not be null.");
        }
        if (StringUtils.isEmpty(actions)) {
            return Collections.emptyList();
        }
        try {
            // Parse action JSON
            final Actions parsedActions = mapper.readerFor(Actions.class).readValue(actions);
            // Create closures from parsed actions
            final List<Action> allActions = parsedActions.getActions();
            final List<Action> builtActions = buildActions(allActions);
            // all set: wraps everything and return to caller
            return builtActions;
        }
        catch(TDPException tpe) {
            // leave TDPException as is
            throw tpe;
        }
        catch (Exception e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    /**
     * Given a list of actions recreate but with the Spring Context {@link ActionMetadata}. It is mandatory to use any
     * action parsed from JSon.
     */
    public List<Action> buildActions(List<Action> allActions) {
        final List<Action> builtActions = new ArrayList<>(allActions.size() + 1);
        for (Action parsedAction : allActions) {
            if (parsedAction != null && parsedAction.getName() != null) {
                String actionNameLowerCase = parsedAction.getName().toLowerCase();
                final ActionMetadata metadata = actionRegistry.get(actionNameLowerCase);
                builtActions.add(factory.create(metadata, parsedAction.getParameters()));
            }
        }
        return builtActions;
    }
}
