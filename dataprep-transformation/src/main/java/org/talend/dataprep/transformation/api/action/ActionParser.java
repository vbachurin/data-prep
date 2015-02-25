package org.talend.dataprep.transformation.api.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.talend.dataprep.api.DataSetRow;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.LowerCase;
import org.talend.dataprep.transformation.api.action.metadata.UpperCase;

public class ActionParser {

    public static final Log LOGGER = LogFactory.getLog(ActionParser.class);

    public Consumer<DataSetRow> parse(String actions) {
        if (actions == null) {
            // Actions cannot be null (but can be empty string for no op actions).
            throw new IllegalArgumentException("Actions parameter can not be null.");
        }
        try {
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            String content = actions.trim();
            if (content.isEmpty()) {
                return row -> {}; // No op action
            }
            JsonNode node = mapper.readTree(content);
            Iterator<JsonNode> elements = node.getElements();
            if (elements.hasNext()) {
                JsonNode root = elements.next();
                if (!root.isArray()) {
                    throw new IllegalArgumentException("'Actions' element should contain an array of 'action' elements.");
                }
                List<Consumer<DataSetRow>> parsedActions = new ArrayList<>();
                Iterator<JsonNode> actionNodes = root.getElements();
                while (actionNodes.hasNext()) {
                    JsonNode actionNode = actionNodes.next();
                    String actionType = actionNode.get("action").getTextValue().toLowerCase(); //$NON-NLS-1$
                    ActionMetadata currentAction;
                    switch (actionType) {
                    case UpperCase.UPPER_CASE_ACTION_NAME:
                        currentAction = UpperCase.INSTANCE;
                        break;
                    case LowerCase.LOWER_CASE_ACTION_NAME:
                        currentAction = LowerCase.INSTANCE;
                        break;
                    default:
                        throw new NotImplementedException("No support for action '" + actionType + "'.");
                    }
                    Iterator<Map.Entry<String, JsonNode>> parameters = actionNode.get("parameters").getFields(); //$NON-NLS-1$
                    parsedActions.add(currentAction.create(parameters));
                }
                return row -> {
                    for (Consumer<DataSetRow> parsedAction : parsedActions) {
                        parsedAction.accept(row);
                    }
                };
            } else {
                return row -> {}; // Should not happen, but no action means no op.
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse actions in '" + actions + "'.", e);
        }
    }
}
