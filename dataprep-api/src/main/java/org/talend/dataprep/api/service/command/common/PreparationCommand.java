package org.talend.dataprep.api.service.command.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.api.service.command.dataset.DataSetGet;
import org.talend.dataprep.cache.ContentCache;
import org.talend.dataprep.cache.ContentCacheKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;

public abstract class PreparationCommand<T> extends DataPrepCommand<T> {

    /**
     * <p>
     * A configuration to allow work from intermediate (cached) preparation content. If set to <b>true</b> and
     * preparation has:
     * <ul>
     *     <li>Root</li>
     *     <li>Step #1</li>
     *     <li>Step #2</li>
     * </ul>
     * When user asks for content @ Step #2, a more efficient approach is to start from content @ Step #1 (iso. starting
     * over from Root).
     * </p>
     * <p>
     * However, content cached for Step #1 can't be used as is (columns are located after "records", causing DataSet
     * deserialization to fail).
     * </p>
     */
    private static final boolean ALLOW_WORK_FROM_CACHE = false;

    @Autowired
    protected Jackson2ObjectMapperBuilder builder;

    @Autowired
    protected ContentCache contentCache;

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

        // get dataset content with 1 row
        final InputStream content = getDatasetContent(dataSetId, 1L);

        // extract insertion point actions
        final List<Action> actions = getPreparationActions(preparation, insertionStepId);
        final String serializedReferenceActions = serializeActions(actions);

        final List<Action> diffActions = new ArrayList<>(actions);
        diffActions.addAll(actionsToAdd);
        final String serializedActions = serializeActions(diffActions);

        // get created columns ids
        final HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("diffActions", new StringBody(serializedActions, TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("referenceActions", new StringBody(serializedReferenceActions, TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("content", new InputStreamBody(content, APPLICATION_JSON)) //$NON-NLS-1$
                .build();

        final String uri = transformationServiceUrl + "/transform/diff/metadata";
        final HttpPost transformationCall = new HttpPost(uri);
        try {
            transformationCall.setEntity(reqEntity);
            final InputStream diffInputStream = client.execute(transformationCall).getEntity().getContent();
            final ObjectMapper mapper = builder.build();
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
            return builder.build().reader(Preparation.class).readValue(content);
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
            return getJsonReader().readTree(content);
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
        final DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, client, dataSetId, false, true, sample);
        return retrieveDataSet.execute();
    }

    /**
     * Serialize the actions to string.
     *
     * @param stepActions - map of couple (stepId, action)
     * @return the serialized actions
     */
    protected String serializeActions(final Collection<Action> stepActions) throws JsonProcessingException {
        return "{\"actions\": " + getJsonWriter().writeValueAsString(stepActions) + "}";
    }

    /**
     * Serialize the list of integer to json string.
     * 
     * @param listToEncode - list of integer to encode
     * @return the serialized and encoded list
     */
    protected String serializeIds(final List<Integer> listToEncode) throws JsonProcessingException {
        return getJsonWriter().writeValueAsString(listToEncode);
    }

    /**
     * Encode the string to base 64
     *
     * @param toEncode The string to encode
     * @return the encoded string
     */
    protected String encode(String toEncode) {
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
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
                    .reader(new TypeReference<List<Action>>() { //
                    }) //
                    .readValue(content);
        } finally {
            actionsRetrieval.releaseConnection();
        }
    }

    /**
     * Return the preparation context from the given arguments.
     *
     * @param preparationId the preparation id.
     * @param stepId the step id.
     * @param sample the optional sample size.
     * @return the preparation context from the given arguments.
     * @throws IOException if an error occurs.
     */
    protected PreparationContext getContext(String preparationId, String stepId, Long sample) throws IOException {

        PreparationContext ctx = new PreparationContext();
        if (preparationId == null) {
            ctx.actions = Collections.emptyList();
            ctx.version = Step.ROOT_STEP.id();
            return ctx;
        }
        // Modifies asked version with actual step id
        final Preparation preparation = getPreparation(preparationId);
        String version = stepId;
        if ("head".equals(stepId)) {
            int lastIndex = preparation.getSteps().size() - 1;
            version = preparation.getSteps().get(lastIndex);
        } else if ("origin".equals(version)) {
            version = Step.ROOT_STEP.id();
        }
        ctx.preparation = preparation;
        ctx.version = version;
        // Direct try on cache at given version
        ContentCacheKey key = new ContentCacheKey(preparation, version, sample);
        if (contentCache.has(key)) {
            ctx.content = contentCache.get(key);
            ctx.actions = Collections.emptyList();
            ctx.fromCache = true;
            return ctx;
        }
        // At this point, content does *not* come from cache
        ctx.fromCache = false;
        String transformationStartStep;
        if (ALLOW_WORK_FROM_CACHE) {
            // Try to find intermediate cached version (starting from version)
            transformationStartStep = version;
            final List<String> preparationSteps = preparation.getSteps();
            for (String step : preparationSteps) {
                transformationStartStep = step;
                key = new ContentCacheKey(preparation, step, sample);
                if (contentCache.has(key)) {
                    ctx.content = contentCache.get(key);
                    break;
                }
            }
            // Did not find any cache for retrieve preparation details, starts over from original dataset
            if (Step.ROOT_STEP.id().equals(transformationStartStep)) {
                ctx.content = getDatasetContent(preparation.getDataSetId(), sample);
            }
        } else {
            // Don't allow to work from intermediate cached steps, so start over from root (data set content).
            ctx.content = getDatasetContent(preparation.getDataSetId(), sample);
            transformationStartStep = stepId;
        }
        // Build the actions to execute
        if (Step.ROOT_STEP.id().equals(transformationStartStep)) {
            // Went down to root step and found nothing in cache -> get all preparation actions
            ctx.actions = getPreparationActions(preparation, stepId);
        } else {
            // Stopped in the middle -> compute list of actions to remove
            ctx.actions = getPreparationActions(preparation, transformationStartStep);
        }
        return ctx;
    }

    /**
     * Internal class that holds the preparation context.
     */
    public class PreparationContext {

        /** True if the preparation content comes from the cache. */
        boolean fromCache;

        /** The preparation content (may be the dataset one if no action is performed). */
        InputStream content;

        /** The list of actions performed in the preparation. */
        List<Action> actions;

        /** The actual preparation. */
        Preparation preparation;

        /** The preparation version (step). */
        String version;

        /**
         * @return the preparation content.
         */
        public InputStream getContent() {
            return content;
        }

        /**
         * @return the preparation actions.
         */
        public List<Action> getActions() {
            return actions;
        }

        /**
         * @return the actual preparation.
         */
        public Preparation getPreparation() {
            return preparation;
        }

        /**
         * @return the preparation version.
         */
        public String getVersion() {
            return version;
        }

        /**
         * @return true if the preparation comes from the cache.
         */
        public boolean fromCache() {
            return fromCache;
        }
    }
}
