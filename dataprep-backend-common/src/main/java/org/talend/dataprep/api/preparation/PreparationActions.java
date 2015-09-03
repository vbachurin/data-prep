package org.talend.dataprep.api.preparation;

import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PreparationActions extends Identifiable implements Serializable {

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

    @Override
    public String id() {
        return getId();
    }

    @Override
    public String getId() {
        try {
            return DigestUtils.sha1Hex(serializeActions());
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_COMPUTE_ID, e);
        }
    }

    @Override
    public void setId(String id) {
        // No op
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

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PreparationActions that = (PreparationActions) o;
        return Objects.equals(actions, that.actions);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(actions);
    }
}
