package org.talend.dataprep.transformation.api.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class ActionParser {

    public static final Log LOGGER = LogFactory.getLog(ActionParser.class);

    public Action parse(String actions) {
        if (actions == null) {
            // Actions cannot be null (but can be empty string for no op actions).
            throw new IllegalArgumentException("Actions parameter can not be null.");
        }
        try {
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            String content = actions.trim();
            if (content.isEmpty()) {
                return new DefaultAction(); // No op action
            }
            JsonNode node = mapper.readTree(content);
            Iterator<JsonNode> elements = node.getElements();
            if (elements.hasNext()) {
                JsonNode root = elements.next();
                if (!root.isArray()) {
                    throw new IllegalArgumentException("'Actions' element should contain an array of 'action' elements.");
                }
                List<Action> parsedActions = new ArrayList<>();
                Iterator<JsonNode> actionNodes = root.getElements();
                while (actionNodes.hasNext()) {
                    JsonNode actionNode = actionNodes.next();
                    String actionType = actionNode.get("action").getTextValue().toLowerCase();
                    Action currentAction;
                    switch (actionType) {
                    case "uppercase": //$NON-NLS-1$
                        currentAction = new UpperCase();
                        break;
                    case "lowercase": //$NON-NLS-1$
                        currentAction = new LowerCase();
                        break;
                    default:
                        throw new NotImplementedException("No support for action '" + actionType + "'.");
                    }
                    Iterator<Map.Entry<String, JsonNode>> parameters = actionNode.get("parameters").getFields(); //$NON-NLS-1$
                    currentAction.init(parameters);
                    parsedActions.add(currentAction);
                }
                return new CompositeAction(parsedActions.toArray(new Action[parsedActions.size()]));
            } else {
                return new DefaultAction(); // Should not happen, but no action means no op.
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse actions in '" + actions + "'.", e);
        }
    }
}
