package org.talend.dataprep.api.service.command.common;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.api.service.command.dataset.DataSetGet;
import org.talend.dataprep.transformation.preview.api.PreviewParameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommandGroupKey;

public abstract class PreparationCommand<T> extends GenericCommand<T> {

    @Autowired
    protected Jackson2ObjectMapperBuilder builder;


    protected PreparationCommand(final HystrixCommandGroupKey groupKey, final HttpClient client) {
        super(groupKey, client);
    }

    /**
     * Get the diff metadata (containing the created columns ids) by adding the 'actionsToAdd' to the preparation (preparationId) at the 'insertionStepId'
     * @param preparationId     The preparation id
     * @param insertionStepId   The step id where we want to add the actions
     * @param actionsToAdd      The actions to add
     */
    protected StepDiff getDiffMetadata(final String preparationId, final String insertionStepId, final List<Action> actionsToAdd) throws IOException {

        // get preparation details
        final Preparation preparation = getPreparation(preparationId);
        final String dataSetId = preparation.getDataSetId();

        // extract insertion point actions
        final List<Action> actions = getPreparationActions(preparation, insertionStepId);

        final List<Action> diffActions = new ArrayList<>(actions);
        diffActions.addAll(actionsToAdd);

        final PreviewParameters previewParameters = new PreviewParameters( //
                serializeActions(actions), //
                serializeActions(diffActions), //
                dataSetId, //
                null);

        final String uri = transformationServiceUrl + "/transform/diff/metadata";
        final HttpPost transformationCall = new HttpPost(uri);

        final ObjectMapper mapper = builder.build();
        try {
            transformationCall
                    .setEntity(new StringEntity(mapper.writer().writeValueAsString(previewParameters), APPLICATION_JSON));
            final InputStream diffInputStream = client.execute(transformationCall).getEntity().getContent();
            return mapper.readValue(diffInputStream, StepDiff.class);
        }
        finally {
            transformationCall.releaseConnection();
        }
    }

    /**
     * Call Preparation Service to get preparation.
     *
     * @param preparationId - the preparation id
     * @return the resulting Json node object
     * @throws java.io.IOException
     */
    protected Preparation getPreparation(final String preparationId) throws IOException {
        if (StringUtils.isEmpty(preparationId)) {
            throw new IllegalArgumentException("Preparation id cannot be empty.");
        }
        final HttpGet preparationRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + preparationId);
        try {
            InputStream content = client.execute(preparationRetrieval).getEntity().getContent();
            return builder.build().readerFor(Preparation.class).readValue(content);
        } finally {
            preparationRetrieval.releaseConnection();
        }
    }

    /**
     * Call Dataset Service to get dataset metadata details
     *
     * @param datasetId - the preparation id
     * @return the resulting Json node object
     * @throws java.io.IOException
     */
    protected JsonNode getDatasetDetails(final String datasetId) throws IOException {
        final HttpGet datasetRetrieval = new HttpGet(datasetServiceUrl + "/datasets/" + datasetId + "/metadata");
        try {
            InputStream content = client.execute(datasetRetrieval).getEntity().getContent();
            return builder.build().readTree(content);
        } finally {
            datasetRetrieval.releaseConnection();
        }
    }

    /**
     * Get the full dataset records.
     *
     * @param dataSetId the dataset id.
     * @return the resulting input stream records
     */
    protected InputStream getDatasetContent(final String dataSetId) {
        return getDatasetContent(dataSetId, null);
    }

    /**
     * Get dataset records
     *
     * @param dataSetId the dataset id.
     * @param sample the wanted sample size (if null or <=0, the full dataset content is returned).
     * @return the resulting input stream records
     */
    protected InputStream getDatasetContent(final String dataSetId, Long sample) {
        final DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, client, dataSetId, true, sample);
        return retrieveDataSet.execute();
    }

    /**
     * Serialize the actions to string.
     *
     * @param stepActions - map of couple (stepId, action)
     * @return the serialized actions
     */
    protected String serializeActions(final Collection<Action> stepActions) throws JsonProcessingException {
        return "{\"actions\": " + builder.build().writeValueAsString(stepActions) + "}";
    }

    /**
     * Serialize the list of integer to json string.
     * 
     * @param listToEncode - list of integer to encode
     * @return the serialized and encoded list
     */
    protected String serializeIds(final List<Integer> listToEncode) throws JsonProcessingException {
        return builder.build().writeValueAsString(listToEncode);
    }

    protected List<Action> getPreparationActions(Preparation preparation, String stepId) throws IOException {
        if (StringUtils.isEmpty(stepId) || Step.ROOT_STEP.id().equals(stepId)) {
            // No need for a connection to retrieve an empty list.
            return Collections.emptyList();
        }
        final HttpGet actionsRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + preparation.id()
                + "/actions/" + stepId);
        try {
            InputStream content = client.execute(actionsRetrieval).getEntity().getContent();
            return builder.build() //
                    .readerFor(new TypeReference<List<Action>>() { //
                    }) //
                    .readValue(content);
        } finally {
            actionsRetrieval.releaseConnection();
        }
    }

}
