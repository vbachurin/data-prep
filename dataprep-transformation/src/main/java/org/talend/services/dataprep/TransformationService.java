package org.talend.services.dataprep;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.format.export.ExportFormat;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.security.PublicAPI;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.api.action.dynamic.GenericParameter;
import org.talend.dataprep.transformation.preview.api.PreviewParameters;

@Service(name = "TransformationService")
public interface TransformationService {

    /**
     * Run the transformation given the provided export parameters. This operation transforms the dataset or preparation using
     * parameters in export parameters.
     * 
     * @param parameters Preparation id to apply.
     * @return The transformed content.
     */
    @RequestMapping(value = "/apply", method = POST, consumes = APPLICATION_JSON_VALUE)
    @VolumeMetered
    StreamingResponseBody execute(@RequestBody @Valid ExportParameters parameters);

    /**
     * Apply the preparation to the dataset out of the given IDs.
     * 
     * @param preparationId the preparation id to apply on the dataset.
     * @param datasetId the dataset id to transform.
     * @param formatName The output {@link ExportFormat format}. This format also set the MIME response type.
     * @param stepId the preparation step id to use (default is 'head').
     * @param name the transformation name.
     * @param exportParams additional (optional) export parameters.
     */
    @RequestMapping(value = "/apply/preparation/{preparationId}/dataset/{datasetId}/{format}", method = GET)
    @VolumeMetered
    StreamingResponseBody applyOnDataset(@PathVariable(value = "preparationId") String preparationId,
            @PathVariable(value = "datasetId") String datasetId, @PathVariable("format") String formatName,
            @RequestParam(value = "stepId", required = false, defaultValue = "head") String stepId,
            @RequestParam(value = "name", required = false, defaultValue = "untitled") String name,
            @RequestParam Map<String, String> exportParams);

    /**
     * Export the dataset to the given format.
     *
     * @param datasetId the dataset id to transform.
     * @param formatName The output {@link ExportFormat format}. This format also set the MIME response type.
     * @param name the transformation name.
     * @param exportParams additional (optional) export parameters.
     */
    @RequestMapping(value = "/export/dataset/{datasetId}/{format}", method = GET)
    @Timed
    StreamingResponseBody exportDataset(@PathVariable(value = "datasetId") String datasetId,
            @PathVariable("format") String formatName,
            @RequestParam(value = "name", required = false, defaultValue = "untitled") String name,
            @RequestParam Map<String, String> exportParams);

    /**
     * Compute the given aggregation.
     *
     * @param rawParams the aggregation rawParams as body rawParams.
     */
    @RequestMapping(value = "/aggregate", method = POST, produces = APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @VolumeMetered
    AggregationResult aggregate(@RequestBody String rawParams);

    /**
     * This operation allow client to create a diff between 2 list of actions starting from the same data. For example,
     * sending:
     * <ul>
     * <li>{a1, a2} as old actions</li>
     * <li>{a1, a2, a3} as new actions</li>
     * </ul>
     * ... will highlight changes done by a3.
     * <p>
     * To prevent the actions to exceed URL length limit, everything is shipped within via the multipart request body.
     *
     * @param rawParameters The preview parameters, encoded in json within the request body.
     * @param output Where to write the response.
     */
    @RequestMapping(value = "/transform/preview", method = POST, produces = APPLICATION_JSON_VALUE)
    @VolumeMetered
    void transformPreview(@RequestBody PreviewParameters previewParameters, OutputStream output);

    /**
     * Given a list of requested preview, it applies the diff to each one.
     * A diff is between 2 sets of actions and return the info like created columns ids
     */
    @RequestMapping(value = "/transform/diff/metadata", method = POST, produces = APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @VolumeMetered
    List<StepDiff> getCreatedColumns(@RequestBody List<PreviewParameters> previewParameters);

    /**
     * Get the action dynamic params.
     */
    @RequestMapping(value = "/transform/suggest/{action}/params", method = POST)
    @Timed
    GenericParameter dynamicParams(@PathVariable("action") String action, //
            @RequestParam(value = "columnId") String columnId, //
            InputStream content);

    /**
     * Returns all {@link ActionMetadata actions} data prep may apply to a column. Column is optional and only needed to
     * fill out default parameter values.
     *
     * @return A list of {@link ActionMetadata} that can be applied to this column.
     * @see #suggest(ColumnMetadata, int)
     */
    @RequestMapping(value = "/actions/column", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    List<ActionMetadata> columnActions(@RequestBody(required = false) ColumnMetadata column);

    /**
     * Suggest what {@link ActionMetadata actions} can be applied to <code>column</code>.
     *
     * @param column A {@link ColumnMetadata column} definition.
     * @param limit An optional limit parameter to return the first <code>limit</code> suggestions.
     * @return A list of {@link ActionMetadata} that can be applied to this column.
     * @see #suggest(DataSet)
     */
    @RequestMapping(value = "/suggest/column", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    List<ActionMetadata> suggest(@RequestBody(required = false) ColumnMetadata column, //
            @RequestParam(value = "limit", defaultValue = "5", required = false) int limit);

    /**
     * Returns all {@link ActionMetadata actions} data prep may apply to a line.
     *
     * @return A list of {@link ActionMetadata} that can be applied to a line.
     */
    @RequestMapping(value = "/actions/line", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    List<ActionMetadata> lineActions();

    /**
     * Suggest what {@link ActionMetadata actions} can be applied to <code>dataSetMetadata</code>.
     *
     * @param dataSet A {@link DataSetMetadata dataset} definition.
     * @return A list of {@link ActionMetadata} that can be applied to this data set.
     * @see #suggest(ColumnMetadata, int)
     */
    @RequestMapping(value = "/suggest/dataset", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    List<ActionMetadata> suggest(DataSet dataSet);

    /**
     * List all transformation related error codes.
     */
    @RequestMapping(value = "/transform/errors", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<JsonErrorCodeDescription> listErrors();

    /**
     * Get the available export formats
     */
    @RequestMapping(value = "/export/formats", method = GET)
    @Timed
    @PublicAPI
    List<ExportFormat> exportTypes();
}
