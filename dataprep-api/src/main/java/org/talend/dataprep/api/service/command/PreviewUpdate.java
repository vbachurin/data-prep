package org.talend.dataprep.api.service.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.api.PreviewUpdateInput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreviewUpdate extends HystrixCommand<InputStream> {

    private final HttpClient client;

    private final String contentServiceUrl;
    private final String transformationServiceUrl;
    private final String preparationServiceUrl;

    private final PreviewUpdateInput input;

    private ObjectMapper objectMapper;
    private ObjectReader jsonReader;
    private ObjectWriter jsonWriter;

    @Autowired
    private WebApplicationContext context;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private PreviewUpdate(final HttpClient client, final String contentServiceUrl, final String transformationServiceUrl, final String preparationServiceUrl, final PreviewUpdateInput input) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.contentServiceUrl = contentServiceUrl;
        this.transformationServiceUrl = transformationServiceUrl;
        this.preparationServiceUrl = preparationServiceUrl;
        this.input = input;
    }

    @Override
    protected InputStream run() throws Exception {

        //get preparation details
        final JsonNode preparationDetails = getPreparationDetails(input.getPreparationId());

        //extract actions by steps in chronological order, until input defined last active step
        final List<String> stepsIds = getActionsStepIds(preparationDetails, input.getCurrentStepId());
        final Map<String, Action> originalActions = getActions(preparationDetails, stepsIds);

        //modify actions to include the update
        final Map<String, Action> modifiedActions = new LinkedHashMap<>(originalActions);
        if(modifiedActions.get(input.getUpdateStepId()) != null) {
            modifiedActions.put(input.getUpdateStepId(), input.getAction());
        }

        //serialize and base 64 encode the 2 actions list
        final String oldEncodedActions = serializeAndEncodeActions(originalActions);
        final String newEncodedActions = serializeAndEncodeActions(modifiedActions);

        //get dataset content
        final InputStream content = getDatasetContent(preparationDetails);

        //get usable tdpIds
        final String encodedTdpIds = serializeAndEncodeList(input.getTdpIds());


        //call transformation preview with content and the 2 transformations
        return previewTransformation(content, oldEncodedActions, newEncodedActions, encodedTdpIds);
    }

    /**
     * Call the transformation service to compute preview between old and new transformation
     * @param content - the dataset content
     * @param oldEncodedActions - the old actions
     * @param newEncodedActions - the preview actions
     * @param encodedTdpIds - the TDP ids
     * @throws IOException
     */
    private InputStream previewTransformation(final InputStream content, final String oldEncodedActions, final String newEncodedActions,final String encodedTdpIds) throws IOException {
        final String uri = this.transformationServiceUrl + "/transform/preview?oldActions=" + oldEncodedActions + "&newActions=" + newEncodedActions + "&indexes=" + encodedTdpIds;
        HttpPost transformationCall = new HttpPost(uri);

        transformationCall.setEntity(new InputStreamEntity(content));
        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }

    /**
     * Get dataset recoreds
     * @param preparationDetails - the Json node preparation details
     * @return the resulting input stream records
     */
    private InputStream getDatasetContent(final JsonNode preparationDetails) {
        final String dataSetId = preparationDetails.get("dataSetId").textValue();
        final DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, client, contentServiceUrl, dataSetId, false, true, false);
        return retrieveDataSet.execute();
    }

    /**
     * Serialize the actions to string and encode it to base 64
     * @param stepActions - map of couple (stepId, action)
     * @return the serialized and encoded actions
     */
    private String serializeAndEncodeActions(final Map<String, Action> stepActions) throws JsonProcessingException {
        final String serialized = "{\"actions\": " + getJsonWriter().writeValueAsString(stepActions.values()) + "}";

        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }

    /**
     * Serialize the list of integer to string and encode it to base 64
     * @param listToEncode - list of integer to encode
     * @return the serialized and encoded list
     */
    private String serializeAndEncodeList(final List<Integer> listToEncode) throws JsonProcessingException {
        final String serialized = getJsonWriter().writeValueAsString(listToEncode);

        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }

    /**
     * Get a map of couples (step id, action)
     * @param preparationDetails - the Json node preparation details
     * @param stepsIds - the step ids in the chronological order
     * @return The map of couples in the StepsIds order
     * @throws IOException
     */
    private Map<String, Action> getActions(final JsonNode preparationDetails, final List<String> stepsIds) throws IOException {
        final Map<String, Action> result = new LinkedHashMap<>(stepsIds.size());
        final JsonNode actionsNode = preparationDetails.get("actions");

        for(int i = 0; i < stepsIds.size(); ++i) {
            result.put(stepsIds.get(i), getObjectMapper().readValue(actionsNode.get(i).toString(), Action.class));
        }

        return result;
    }

    /**
     * Get the list of steps ids, corresponding to an action, in the chronological order
     * If the last active step is provided, the method will only get the steps id from first to the last active step (included)
     * @param preparationDetails - the Json node preparation details
     * @param lastActiveStep - the last active step id
     * @return the list of steps ids
     */
    private List<String> getActionsStepIds(final JsonNode preparationDetails, final String lastActiveStep) throws JsonProcessingException {
        final List<String> result = new ArrayList<>(preparationDetails.size() - 1);
        final JsonNode stepsNode = preparationDetails.get("steps");

        //steps are in reverse order and the last is the initial step (no actions). So we skip the last and we get them in reverse order
        for(int i = stepsNode.size() - 2; i >= 0; --i) {
            final String stepId = stepsNode.get(i).textValue();
            result.add(stepId);

            if(lastActiveStep != null && stepId.equals(lastActiveStep)) {
                break;
            }
        }

        return result;
    }

    /**
     * Call Preparation Service to get preparation details
     * @param preparationId - the preparation id
     * @return the resulting Json node object
     * @throws IOException
     */
    private JsonNode getPreparationDetails(final String preparationId) throws IOException {
        final HttpGet preparationRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + preparationId);
        try {
            InputStream content = client.execute(preparationRetrieval).getEntity().getContent();
            return getJsonReader().readTree(content);
        } finally {
            preparationRetrieval.releaseConnection();
        }
    }

    public ObjectMapper getObjectMapper() {
        if(objectMapper == null) {
            objectMapper = builder.build();
        }
        return objectMapper;
    }

    public ObjectReader getJsonReader() {
        if(jsonReader == null) {
            jsonReader = getObjectMapper().reader();
        }
        return jsonReader;
    }

    public ObjectWriter getJsonWriter() {
        if(jsonWriter == null) {
            jsonWriter = getObjectMapper().writer();
        }
        return jsonWriter;
    }
}
