package org.talend.dataprep.preparation;

import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.annotation.Id;

public class PreparationActions implements Identifiable {

    public static final PreparationActions ROOT_CONTENT = new PreparationActions(Collections.<Action>emptyList());

    private final List<Action> actions;

    public PreparationActions(final List<Action> actions) {
        this.actions = unmodifiableList(actions);
    }

    /**
     * Return the immutable list of actions
     * 
     * @return - the list of actions
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Create a new PreparationActions with concatenated new Actions
     * 
     * @param newActions - the actions to add
     * @return - the new preparation actions
     */
    public PreparationActions append(final List<Action> newActions) {
        final List<Action> appendedActions = new ArrayList<>(getActions().size() + newActions.size());
        appendedActions.addAll(actions);
        appendedActions.addAll(newActions);
        return new PreparationActions(appendedActions);
    }

    /**
     * Transform actions list to readable JSON string
     * 
     * @throws IOException
     */
    public String serializeActions() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(actions);
    }

    @Id
    @Override
    public String id() {
        try {
            return DigestUtils.sha1Hex(serializeActions());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        String serializedActions;
        try {
            serializedActions = serializeActions();
        } catch (IOException e) {
            serializedActions = "invalid actions";
        }
        return "PreparationActions {" + "id:'" + id() + "', actions: " + serializedActions + '}';
    }
}
