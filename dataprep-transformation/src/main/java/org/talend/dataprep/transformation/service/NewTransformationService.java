package org.talend.dataprep.transformation.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory.COLUMN;
import static org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory.LINE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.cache.ContentCache;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.transformation.aggregation.AggregationService;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.suggestion.Suggestion;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngine;
import org.talend.dataprep.transformation.cache.TransformationCacheKey;
import org.talend.dataprep.transformation.format.ExportFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "transformations", basePath = "/transform", description = "Transformations on data")
public class NewTransformationService extends BaseTransformationService {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(NewTransformationService.class);

    /** The Spring application context. */
    @Autowired
    private ApplicationContext context;
    /** All available transformation actions. */
    @Autowired
    private ActionMetadata[] allActions;
    /** he aggregation service. */
    @Autowired
    private AggregationService aggregationService;
    /** The action suggestion engine. */
    @Autowired
    private SuggestionEngine suggestionEngine;

    /** The content cache to... cache transformations results. */
    @Autowired
    private ContentCache contentCache;

    /**
     * Apply the preparation to the dataset out of the given IDs.
     *
     * @param preparationId the preparation id to apply on the dataset.
     * @param datasetId the dataset id to transform.
     * @param formatName The output {@link ExportFormat format}. This format also set the MIME response type.
     * @param stepId the preparation step id to use (default is 'head').
     * @param name the transformation name.
     * @param output Where to write the response.
     */
    //@formatter:off
    @RequestMapping(value = "/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", method = GET)
    @ApiOperation(value = "Transform the given preparation to the given format on the given dataset id", notes = "This operation transforms the dataset using preparation id in the provided format.")
    @VolumeMetered
    public void applyOnDataset(@ApiParam(value = "Preparation id to apply.") @PathVariable(value = "preparationId") final String preparationId,
                               @ApiParam(value = "DataSet id to transform.") @PathVariable(value = "datasetId") final String datasetId,
                               @ApiParam(value = "Output format") @PathVariable("format") final String formatName,
                               @ApiParam(name = "Sample size", value = "Optional sample size to use for the dataset, if missing, the full dataset is returned") @RequestParam(value="sample", required = false) Long sample,
                               @ApiParam(value = "Step id", defaultValue = "head") @RequestParam(value = "stepId", required = false, defaultValue = "head") final String stepId,
                               @ApiParam(value = "Name of the transformation", defaultValue = "untitled") @RequestParam(value = "name", required = false, defaultValue = "untitled") final String name,
                               final OutputStream output) {
    //@formatter:on

        final ObjectMapper mapper = builder.build();

        // get the dataset
        String datasetGetUrl = datasetServiceUrl + "/datasets/" + datasetId + "/content";
        if (sample != null) {
            datasetGetUrl += "?sample=" + sample;
        }
        final HttpGet datasetRetrieval = new HttpGet(datasetGetUrl);
        try {

            final HttpResponse datasetGet = httpClient.execute(datasetRetrieval);
            final HttpStatus response = HttpStatus.valueOf(datasetGet.getStatusLine().getStatusCode());
            if (response.is4xxClientError() || response.is5xxServerError() || response.value() == HttpStatus.NO_CONTENT.value()) {
                throw new TDPException(DataSetErrorCodes.UNABLE_TO_READ_DATASET_CONTENT);
            }
            InputStream datasetContent = datasetGet.getEntity().getContent();

            // the parser need to be encapsulated within an auto closeable block so that its records can be fully
            // streamed
            try (JsonParser parser = mapper.getFactory().createParser(datasetContent)) {
                final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
                useCache(preparationId, dataSet, output, formatName, stepId, name, sample);
            }

        } catch (Exception e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
        // need to release the connection eventually
        finally {
            datasetRetrieval.releaseConnection();
        }
    }

    /**
     * Get the transformation out of the cache, if it's not cached, performs the transformation and cache its result.
     *
     * @param preparationId the preparation id.
     * @param dataSet the DataSet.
     * @param output where to write the output.
     * @param formatName the format name.
     * @param stepId the preparation step id.
     * @param name the preparation name.
     * @param sample the sample size.
     * @throws IOException if an error occurs.
     */
    private void useCache(String preparationId, DataSet dataSet, OutputStream output, String formatName, String stepId,
            String name, Long sample) throws IOException {

        // compute the cache key
        TransformationCacheKey key;
        try {
            key = new TransformationCacheKey(preparationId, dataSet.getMetadata(), formatName, stepId, sample);
        } catch (IOException e) {
            LOG.warn("cannot generate transformation cache key for {}. Cache will not be used.", dataSet.getMetadata(), e);
            internalTransform(preparationId, dataSet, output, formatName, stepId, name);
            return;
        }

        // get it from the cache if available
        final InputStream inputStream = contentCache.get(key);
        if (inputStream != null) {
            IOUtils.copyLarge(inputStream, output);
            return;
        }

        // or save it into the cache
        final OutputStream newCacheEntry = contentCache.put(key, ContentCache.TimeToLive.DEFAULT);
        TeeOutputStream outputStreams = new TeeOutputStream(output, newCacheEntry);
        internalTransform(preparationId, dataSet, outputStreams, formatName, stepId, name);
    }


    /**
     * Returns all {@link ActionMetadata actions} data prep may apply to a column. Column is optional and only needed to
     * fill out default parameter values.
     *
     * @return A list of {@link ActionMetadata} that can be applied to this column.
     * @see #suggest(ColumnMetadata, int)
     */
    @RequestMapping(value = "/actions/column", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Return all actions for a column (regardless of column metadata)", notes = "This operation returns an array of actions.")
    @ResponseBody
    public List<ActionMetadata> columnActions(@RequestBody(required = false) ColumnMetadata column) {
        return Stream.of(allActions) //
                .filter(action -> action.acceptScope(COLUMN)) //
                .map(am -> column != null ? am.adapt(column) : am) //
                .collect(toList());
    }

    /**
     * Suggest what {@link ActionMetadata actions} can be applied to <code>column</code>.
     *
     * @param column A {@link ColumnMetadata column} definition.
     * @param limit An optional limit parameter to return the first <code>limit</code> suggestions.
     * @return A list of {@link ActionMetadata} that can be applied to this column.
     * @see #suggest(DataSet)
     */
    @RequestMapping(value = "/suggest/column", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Suggest actions for a given column metadata", notes = "This operation returns an array of suggested actions in decreasing order of importance.")
    @ResponseBody
    public List<ActionMetadata> suggest(@RequestBody(required = false) ColumnMetadata column, //
            @ApiParam(value = "How many actions should be suggested at most", defaultValue = "5") @RequestParam(value = "limit", defaultValue = "5", required = false) int limit) {
        if (column == null) {
            return Collections.emptyList();
        }
        // look for all actions applicable to the column type
        final List<ActionMetadata> actions = Stream.of(allActions) //
                .filter(am -> am.acceptColumn(column)) // Filter on acceptable columns (for type)
                .collect(toList());
        final List<Suggestion> suggestions = suggestionEngine.score(actions, column);
        return suggestions.stream() //
                .filter(s -> s.getScore() > 0) // Keep only strictly positive score (negative and 0 indicates not
                                               // applicable)
                .limit(limit) //
                .map(Suggestion::getAction) // Get the action for positive suggestions
                .map(am -> am.adapt(column)) // Adapt default values (e.g. column name)
                .collect(Collectors.toList());
    }

    /**
     * Returns all {@link ActionMetadata actions} data prep may apply to a line.
     *
     * @return A list of {@link ActionMetadata} that can be applied to a line.
     */
    @RequestMapping(value = "/actions/line", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Return all actions on lines", notes = "This operation returns an array of actions.")
    @ResponseBody
    public List<ActionMetadata> lineActions() {
        return Stream.of(allActions) //
                .filter(action -> action.acceptScope(LINE)) //
                .map(action -> action.adapt(LINE)) //
                .collect(toList());
    }

    /**
     * Suggest what {@link ActionMetadata actions} can be applied to <code>dataSetMetadata</code>.
     *
     * @param dataSet A {@link DataSetMetadata dataset} definition.
     * @return A list of {@link ActionMetadata} that can be applied to this data set.
     * @see #suggest(ColumnMetadata, int)
     */
    @RequestMapping(value = "/suggest/dataset", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Suggest actions for a given data set metadata", notes = "This operation returns an array of suggested actions in decreasing order of importance.")
    @ResponseBody
    public List<ActionMetadata> suggest(DataSet dataSet) {
        return Collections.emptyList();
    }

    /**
     * List all transformation related error codes.
     */
    @RequestMapping(value = "/transform/errors", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all transformation related error codes.", notes = "Returns the list of all transformation related error codes.")
    @Timed
    public void listErrors(HttpServletResponse response) {
        try {
            // need to cast the typed dataset errors into mock ones to use json parsing
            List<JsonErrorCodeDescription> errors = new ArrayList<>(TransformationErrorCodes.values().length);
            for (TransformationErrorCodes code : TransformationErrorCodes.values()) {
                errors.add(new JsonErrorCodeDescription(code));
            }
            builder.build().writer().writeValue(response.getOutputStream(), errors);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * Get the available export formats
     */
    @RequestMapping(value = "/export/formats", method = GET)
    @ApiOperation(value = "Get the available format types")
    @Timed
    public void exportTypes(final HttpServletResponse response) {
        final List<ExportFormat> types = formatRegistrationService.getExternalFormats();
        try {
            builder.build() //
                    .writer() //
                    .writeValue(response.getOutputStream(), types);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }
}
