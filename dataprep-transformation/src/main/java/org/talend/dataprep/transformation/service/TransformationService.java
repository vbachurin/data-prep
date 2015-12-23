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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.*;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.error.PreparationErrorCodes;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.http.HttpResponseContext;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.transformation.aggregation.AggregationService;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.api.transformer.suggestion.Suggestion;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngine;
import org.talend.dataprep.transformation.format.ExportFormat;
import org.talend.dataprep.transformation.format.FormatRegistrationService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "transformations", basePath = "/transform", description = "Transformations on data")
public class TransformationService {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(TransformationService.class);

    /** The Spring application context. */
    @Autowired
    private ApplicationContext context;

    /** The dataprep ready to use jackson object builder. */
    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    /** All available transformation actions. */
    @Autowired
    private ActionMetadata[] allActions;

    /** The transformer factory. */
    @Autowired
    private TransformerFactory factory;

    /** he aggregation service. */
    @Autowired
    private AggregationService aggregationService;

    /** The format registration service. */
    @Autowired
    private FormatRegistrationService formatRegistrationService;

    /** The action suggestion engine. */
    @Autowired
    private SuggestionEngine suggestionEngine;

    /** DataSet service url. */
    @Value("${dataset.service.url}")
    private String datasetServiceUrl;

    /** Preparation service url. */
    @Value("${preparation.service.url}")
    protected String preparationServiceUrl;

    /** Http client used to retrieve datasets or preparations. */
    @Autowired
    private HttpClient httpClient;


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
    @RequestMapping(value = "/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", method = GET)
    @ApiOperation(value = "Transform the given preparation to the given format on the given dataset id", notes = "This operation transforms the dataset using preparation id in the provided format.")
    @VolumeMetered
    //@formatter:off
    public void v2Transform(@ApiParam(value = "Preparation id to apply.") @PathVariable(value = "preparationId") final String preparationId,
                            @ApiParam(value = "DataSet id to transform.") @PathVariable(value = "datasetId") final String datasetId,
                            @ApiParam(value = "Output format") @PathVariable("format") final String formatName,
                            @ApiParam(value = "Step id", defaultValue = "head") @RequestParam(value = "stepId", required = false, defaultValue = "head") final String stepId,
                            @ApiParam(value = "Name of the transformation", defaultValue = "untitled") @RequestParam(value = "name", required = false, defaultValue = "untitled") final String name,
                            final OutputStream output) {
        //@formatter:on

        final ObjectMapper mapper = builder.build();

        // get the dataset
        final HttpGet datasetRetrieval = new HttpGet(datasetServiceUrl + "/datasets/" + datasetId + "/content");
        try {

            final HttpResponse datasetGet = httpClient.execute(datasetRetrieval);
            InputStream datasetContent = datasetGet.getEntity().getContent();

            // the parser need to be encapsulated within an auto closeable bloc so that the dataset can be fully
            // streamed
            try (JsonParser parser = mapper.getFactory().createParser(datasetContent)) {
                final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
                internalTransform(formatName, dataSet, output, preparationId, stepId, name);
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
     * Apply the preparation to the given content.
     *
     * @param preparationId the preparation id to apply on the dataset.
     * @param datasetContent the dataset content to transform.
     * @param formatName The output {@link ExportFormat format}. This format also set the MIME response type.
     * @param stepId the preparation step id to use (default is 'head').
     * @param name the transformation name.
     * @param output Where to write the response.
     */
    @RequestMapping(value = "/apply/preparation/{preparationId}/{format}", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Apply the given preparation to the given format on the given content", notes = "This operation transforms the content using preparation id to the provided format.")
    @VolumeMetered
    //@formatter:off
    public void v2TransformContent(@ApiParam(value = "Preparation id to apply.") @PathVariable(value = "preparationId") final String preparationId,
                                   @ApiParam(value = "Data set content as JSON.") final InputStream datasetContent,
                                   @ApiParam(value = "Output format") @PathVariable("format") final String formatName,
                                   @ApiParam(value = "Step id", defaultValue = "head") @RequestParam(value = "stepId", required = false, defaultValue = "head") final String stepId,
                                   @ApiParam(value = "Name of the transformation", defaultValue = "untitled") @RequestParam(value = "name", required = false, defaultValue = "untitled") final String name,
                                   final OutputStream output) {
    //@formatter:on

        final ObjectMapper mapper = builder.build();

        // the parser need to be encapsulated within an auto closeable bloc so that the dataset can be fully streamed
        try (JsonParser parser = mapper.getFactory().createParser(datasetContent)) {
            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
            internalTransform(formatName, dataSet, output, preparationId, stepId, name);
        }
 catch (Exception e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
    }

    /**
     * Transformation business logic.
     *
     * @param preparationId the preparation id to apply.
     * @param dataSet the dataset to transform.
     * @param response where to write the transformation.
     * @param formatName the wanted export format.
     * @param stepId the step id of the preparation to apply.
     * @param exportName the export name.
     */
    private void internalTransform(String preparationId, DataSet dataSet, OutputStream response, String formatName, String stepId,
            String exportName) {

        final ExportFormat format = getFormat(formatName);
        String actions = getActions(preparationId, stepId);

        HttpResponseContext.contentType(format.getMimeType());
        HttpResponseContext.header("Content-Disposition", "attachment; filename=\"" + exportName + format.getExtension() + "\"");

        Configuration configuration = Configuration.builder() //
                .format(format.getName()) //
                .output(response) //
                .actions(actions) //
                .build();
        factory.get(configuration).transform(dataSet, configuration);

    }

    /**
     * Return the format that matches the given name or throw an error if the format is unkown.
     *
     * @param formatName the format name.
     * @return the format that matches the given name.
     */
    private ExportFormat getFormat(String formatName) {
        final ExportFormat format = formatRegistrationService.getByName(formatName);
        if (format == null) {
            LOG.error("Export format {} not supported", formatName);
            throw new TDPException(TransformationErrorCodes.OUTPUT_TYPE_NOT_SUPPORTED);
        }
        return format;
    }

    /**
     * Return the actions from the preparation id and the step id.
     *
     * @param preparationId the preparation id.
     * @param stepId the step id.
     * @return the actions that match the given ids.
     */
    private String getActions(String preparationId, String stepId) {

        final HttpGet actionsRetrieval = new HttpGet(
                preparationServiceUrl + "/preparations/" + preparationId + "/actions/" + stepId);
        try {
            final HttpResponse get = httpClient.execute(actionsRetrieval);
            final HttpStatus status = HttpStatus.valueOf(get.getStatusLine().getStatusCode());
            if (status.is4xxClientError() || status.is5xxServerError()) {
                throw new IOException(status.getReasonPhrase());
            }
            return "{\"actions\": " + IOUtils.toString(get.getEntity().getContent()) + '}';
        } catch (IOException e) {
            final ExceptionContext context = ExceptionContext.build().put("id", preparationId).put("version", stepId);
            throw new TDPException(PreparationErrorCodes.UNABLE_TO_READ_PREPARATION, e, context);
        }
 finally {
            actionsRetrieval.releaseConnection();
        }
    }


    /**
     * Returns all {@link ActionMetadata actions} data prep may apply to a column. Column is optional and only needed
     * to fill out default parameter values.
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
     * @param limit  An optional limit parameter to return the first <code>limit</code> suggestions.
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
                .filter(s -> s.getScore() > 0) // Keep only strictly positive score (negative and 0 indicates not applicable)
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
