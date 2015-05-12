package org.talend.dataprep.api.service.command.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.service.command.dataset.DataSetGet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.netflix.hystrix.HystrixCommandGroupKey;

public abstract class PreparationCommand<T> extends DataPrepCommand<T> {

    protected PreparationCommand(final HystrixCommandGroupKey groupKey, final HttpClient client) {
        super(groupKey, client);
    }

    /**
     * Call Preparation Service to get preparation details
     * @param preparationId - the preparation id
     * @return the resulting Json node object
     * @throws java.io.IOException
     */
    protected JsonNode getPreparationDetails(final String preparationId) throws IOException {
        final HttpGet preparationRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + preparationId);
        try {
            InputStream content = client.execute(preparationRetrieval).getEntity().getContent();
            return getJsonReader().readTree(content);
        } finally {
            preparationRetrieval.releaseConnection();
        }
    }

    /**
     * Get dataset records
     * @param dataSetId - the dataset id
     * @return the resulting input stream records
     */
    protected InputStream getDatasetContent(final String dataSetId) {
        final DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, client, dataSetId, false, true);
        return retrieveDataSet.execute();
    }

    /**
     * Serialize the actions to string and encode it to base 64
     * @param stepActions - map of couple (stepId, action)
     * @return the serialized and encoded actions
     */
    protected String serializeAndEncode(final Map<String, Action> stepActions) throws JsonProcessingException {
        final String serialized = "{\"actions\": " + getJsonWriter().writeValueAsString(stepActions.values()) + "}";

        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }

    /**
     * Serialize the list of integer to string and encode it to base 64
     * @param listToEncode - list of integer to encode
     * @return the serialized and encoded list
     */
    protected String serializeAndEncode(final List<Integer> listToEncode) throws JsonProcessingException {
        final String serialized = getJsonWriter().writeValueAsString(listToEncode);

        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }

    /**
     * Get the list of steps ids, corresponding to an action, in the chronological order
     * If the last active step is provided, the method will only get the steps id from first to the last active step (included)
     * @param preparationDetails - the Json node preparation details
     * @param lastActiveStep - the last active step id
     * @return the list of steps ids
     */
    protected List<String> getActionsStepIds(final JsonNode preparationDetails, final String lastActiveStep) throws JsonProcessingException {
        final List<String> result = new ArrayList<>(preparationDetails.size() - 1);
        final JsonNode stepsNode = preparationDetails.get("steps");

        if(lastActiveStep != null && !lastActiveStep.equals(stepsNode.get(stepsNode.size() - 1).textValue())) {
            //steps are in reverse order and the last is the initial step (no actions). So we skip the last and we get them in reverse order
            for(int i = stepsNode.size() - 2; i >= 0; --i) {
                final String stepId = stepsNode.get(i).textValue();
                result.add(stepId);

                if(stepId.equals(lastActiveStep)) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Get a map of couples (step id, action)
     * @param preparationDetails - the Json node preparation details
     * @param stepsIds - the step ids in the chronological order
     * @return The map of couples in the StepsIds order
     * @throws java.io.IOException
     */
    protected Map<String, Action> getActions(final JsonNode preparationDetails, final List<String> stepsIds) throws IOException {
        final Map<String, Action> result = new LinkedHashMap<>(stepsIds.size());
        final JsonNode actionsNode = preparationDetails.get("actions");

        for(int i = 0; i < stepsIds.size(); ++i) {
            result.put(stepsIds.get(i), getObjectMapper().readValue(actionsNode.get(i).toString(), Action.class));
        }

        return result;
    }
}
