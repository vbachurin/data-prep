// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.services.dataprep;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.action.ActionDefinition;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.statistics.SemanticDomain;
import org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.format.export.ExportFormat;
import org.talend.dataprep.format.export.ExportFormatMessage;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.security.PublicAPI;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.api.action.dynamic.GenericParameter;
import org.talend.dataprep.transformation.preview.api.PreviewParameters;

@Service(name = "dataprep.TransformationService")
public interface TransformationService {

    /**
     * Run the transformation given the provided export parameters. This operation transforms the dataset or preparation using
     * parameters in export parameters.
     *
     * @param parameters
     * @return
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
            @RequestParam(name = "exportParams") Map<String, String> exportParams);

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
            @RequestParam(name = "exportParams") Map<String, String> exportParams);

    /**
     * Compute the given aggregation.
     *
     * @param rawParams the aggregation rawParams as body rawParams.
     */
    @RequestMapping(value = "/aggregate", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
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
     * @param previewParameters The preview parameters, encoded in json within the request body.
     */
    @RequestMapping(value = "/transform/preview", method = POST, produces = APPLICATION_JSON_VALUE)
    @VolumeMetered
    StreamingResponseBody transformPreview(@RequestBody PreviewParameters previewParameters);

    /**
     * Given a list of requested preview, it applies the diff to each one.
     * A diff is between 2 sets of actions and return the info like created columns ids
     */
    @RequestMapping(value = "/transform/diff/metadata", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @VolumeMetered
    Stream<StepDiff> getCreatedColumns(@RequestBody List<PreviewParameters> previewParameters);

    @RequestMapping(value = "/preparation/{preparationId}/cache", method = DELETE)
    @VolumeMetered
    void evictCache(@PathVariable(value = "preparationId") String preparationId);

    /**
     * Get the action dynamic params.
     */
    @RequestMapping(value = "/transform/suggest/{action}/params", method = POST)
    @Timed
    GenericParameter dynamicParams(@PathVariable("action") String action, @RequestParam(value = "columnId") String columnId,
            InputStream content);

    /**
     * Returns all {@link ActionDefinition actions} data prep may apply to a column. Column is optional and only needed to
     * fill out default parameter values.
     *
     * @return A list of {@link ActionDefinition} that can be applied to this column.
     * @see #suggest(ColumnMetadata, int)
     */
    @RequestMapping(value = "/actions/column", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    Stream<ActionDefinition> columnActions(@RequestBody(required = false) ColumnMetadata column);

    /**
     * Suggest what {@link ActionDefinition actions} can be applied to <code>column</code>.
     *
     * @param column A {@link ColumnMetadata column} definition.
     * @param limit An optional limit parameter to return the first <code>limit</code> suggestions.
     * @return A list of {@link ActionDefinition} that can be applied to this column.
     * @see #suggest(DataSet)
     */
    @RequestMapping(value = "/suggest/column", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    Stream<ActionDefinition> suggest(@RequestBody(required = false) ColumnMetadata column, //
            @RequestParam(value = "limit", defaultValue = "5", required = false) int limit);

    /**
     * Returns all {@link ActionDefinition actions} data prep may apply to a line.
     *
     * @return A list of {@link ActionDefinition} that can be applied to a line.
     */
    @RequestMapping(value = "/actions/line", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    Stream<ActionDefinition> lineActions();

    /**
     * Suggest what {@link ActionDefinition actions} can be applied to <code>dataSetMetadata</code>.
     *
     * @param dataSet A {@link DataSetMetadata dataset} definition.
     * @return A list of {@link ActionDefinition} that can be applied to this data set.
     * @see #suggest(ColumnMetadata, int)
     */
    @RequestMapping(value = "/suggest/dataset", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    List<ActionDefinition> suggest(DataSet dataSet);

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
    Stream<ExportFormatMessage> exportTypes();

    /**
     * Get the available export formats for preparation
     */
    @RequestMapping(value = "/export/formats/preparations/{preparationId}", method = GET)
    @Timed
    Stream<ExportFormatMessage> getPreparationExportTypesForPreparation(@PathVariable("preparationId") String preparationId);

    /**
     * Get the available export formats for dataset.
     */
    @RequestMapping(value = "/export/formats/datasets/{dataSetId}", method = GET)
    @Timed
    Stream<ExportFormatMessage> getPreparationExportTypesForDataSet(@PathVariable("dataSetId") String dataSetId);

    @RequestMapping(value = "/dictionary", method = GET, produces = APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    StreamingResponseBody getDictionary();

    /**
     * Return the semantic types for a given preparation / column.
     *
     * @param preparationId the preparation id.
     * @param columnId the column id.
     * @param stepId the step id (optional, if not specified, it's 'head')
     * @return the semantic types for a given preparation / column.
     */
    @RequestMapping(value = "/preparations/{preparationId}/columns/{columnId}/types", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    List<SemanticDomain> getPreparationColumnSemanticCategories(@PathVariable("preparationId") String preparationId,
            @PathVariable("columnId") String columnId, @RequestParam(defaultValue = "head", name = "stepId") String stepId);
}
