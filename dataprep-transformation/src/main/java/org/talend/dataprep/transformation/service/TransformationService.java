package org.talend.dataprep.transformation.service;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.transformation.api.action.dynamic.DynamicType;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.GenericParameter;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.exporter.ExportConfiguration;
import org.talend.dataprep.transformation.api.transformer.exporter.ExportFactory;
import org.talend.dataprep.transformation.api.transformer.json.DiffTransformerFactory;
import org.talend.dataprep.transformation.api.transformer.json.SimpleTransformerFactory;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "transformations", basePath = "/transform", description = "Transformations on data")
public class TransformationService {

    @Autowired
    private ApplicationContext context;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private ActionMetadata[] allActions;

    @Autowired
    private SimpleTransformerFactory simpleFactory;

    @Autowired
    private DiffTransformerFactory diffFactory;

    @Autowired
    private ExportFactory exportFactory;

    /**
     * Apply all <code>actions</code> to <code>content</code>. Actions is a Base64-encoded JSON list of {@link ActionMetadata} with parameters.
     *
     * @param actions A Base64-encoded list of actions.
     * @param content A JSON input that complies with {@link DataSet} bean.
     * @param response The response used to send transformation result back to client.
     */
    @RequestMapping(value = "/transform", method = POST, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Transform input data", notes = "This operation returns the input data transformed using the supplied actions.")
    @VolumeMetered
    public void transform(@ApiParam(value = "Actions to perform on content (encoded in Base64).") @RequestParam(value = "actions", defaultValue = "", required = false) String actions, //
            @ApiParam(value = "Data set content as JSON") InputStream content, HttpServletResponse response) {
        // A transformation is an export to JSON
        transform(ExportType.JSON, actions, null, content, response);
    }

    /**
     * Similar to {@link #transform(String, InputStream, HttpServletResponse)} except this method allows client to
     * customize the output format (see {@link ExportType available export types}).
     *
     * @param format The output {@link ExportType format}. This format also set the MIME response type.
     * @param actions A Base64-encoded list of actions.
     * @param csvSeparator The CSV separator (for {@link ExportType#CSV}), might be <code>null</code> for other export types.
     * @param content A JSON input that complies with {@link DataSet} bean.
     * @param response The response used to send transformation result back to client.
     */
    @RequestMapping(value = "/export/{format}", method = POST)
    @ApiOperation(value = "Transform input data", notes = "This operation export the input data transformed using the supplied actions in the provided format.")
    @VolumeMetered
    public void transform(@ApiParam(value = "Output format.") @PathVariable("format") final ExportType format, //
                          @ApiParam(value = "Actions to perform on content (encoded in Base64).") @RequestParam(value = "actions", defaultValue = "", required = false) final String actions, //
                          @ApiParam(value = "CSV separator.") @RequestParam(value = "separator", required = false) final String csvSeparator, //
                          @ApiParam(value = "Data set content as JSON") final InputStream content, //
                          final HttpServletResponse response) {
        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(content)) {
            final String decodedActions = new String(Base64.getDecoder().decode(actions));
            final Character decodedCsvSeparator = csvSeparator != null ? new String(Base64.getDecoder().decode(csvSeparator))
                    .charAt(0) : au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("csvSeparator", decodedCsvSeparator);

            final ExportConfiguration configuration = ExportConfiguration.builder().args(arguments).format(format)
                    .actions(decodedActions).build();

            response.setContentType(format.getMimeType());

            final Transformer transformer = exportFactory.getExporter(configuration);
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            transformer.transform(dataSet, response.getOutputStream());
        } catch(JsonMappingException e) {
            // Ignore (end of input)
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        } catch (UnsupportedOperationException e) {
            if (format != null) {
                throw new TDPException(TransformationErrorCodes.OUTPUT_TYPE_NOT_SUPPORTED, e);
            }
            throw e;
        }
    }

    /**
     * This operation allow client to create a diff between 2 list of actions starting from the same data. For example, sending:
     * <ul>
     * <li>{a1, a2} as old actions</li>
     * <li>{a1, a2, a3} as new actions</li>
     * </ul>
     * ... will highlight changes done by a3.
     * @param oldActions A Base64-encoded list of actions.
     * @param newActions A Base64-encoded list of actions.
     * @param indexes Allows client to indicates specific line numbers to focus on.
     * @param content A JSON input that complies with {@link DataSet} bean.
     * @param response The response used to send transformation result back to client.
     */
    @RequestMapping(value = "/transform/preview", method = POST, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Transform input data", notes = "This operation returns the input data diff between the old and the new transformation actions")
    @VolumeMetered
    public void transformPreview(@ApiParam(value = "Old actions to perform on content (encoded in Base64).") @RequestParam(value = "oldActions", required = false) final String oldActions, //
                                 @ApiParam(value = "New actions to perform on content (encoded in Base64).") @RequestParam(value = "newActions", required = false) final String newActions, //
                                 @ApiParam(value = "The row indexes to return") @RequestParam(value = "indexes", required = false) final String indexes, //
                                 @ApiParam(value = "Data set content as JSON") final InputStream content, //
                                 final HttpServletResponse response) {
        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(content)) {
            final String decodedIndexes = indexes == null ? null : new String(Base64.getDecoder().decode(indexes));
            final String decodedOldActions = oldActions == null ? null : new String(Base64.getDecoder().decode(oldActions));
            final String decodedNewActions = newActions == null ? null : new String(Base64.getDecoder().decode(newActions));

            final Transformer transformer = diffFactory.withIndexes(decodedIndexes).withActions(decodedOldActions, decodedNewActions).get();
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            transformer.transform(dataSet, response.getOutputStream());
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    /**
     * Suggest what {@link ActionMetadata actions} can be applied to <code>column</code>.
     * @param column A {@link ColumnMetadata column} definition.
     * @return A list of {@link ActionMetadata} that can be applied to this column.
     * @see #suggest(DataSetMetadata)
     */
    @RequestMapping(value = "/suggest/column", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Suggest actions for a given column metadata", notes = "This operation returns an array of suggested actions in decreasing order of importance.")
    @ResponseBody
    public List<ActionMetadata> suggest(@RequestBody(required = false) ColumnMetadata column) {
        if (column == null) {
            return Collections.emptyList();
        }
        // look for all actions applicable to the column type
        return Stream.of(allActions).filter(am -> am.accept(column)).collect(Collectors.toList());
    }

    /**
     * Suggest what {@link ActionMetadata actions} can be applied to <code>dataSetMetadata</code>.
     * @param dataSetMetadata A {@link DataSetMetadata dataset} definition.
     * @return A list of {@link ActionMetadata} that can be applied to this data set.
     * @see #suggest(ColumnMetadata)
     */
    @RequestMapping(value = "/suggest/dataset", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Suggest actions for a given data set metadata", notes = "This operation returns an array of suggested actions in decreasing order of importance.")
    @ResponseBody
    public List<ActionMetadata> suggest(DataSetMetadata dataSetMetadata) {
        if (dataSetMetadata == null) {
            return Collections.emptyList();
        }
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
     * Get the transformation dynamic params
     */
    @RequestMapping(value = "/transform/suggest/{action}/params", method = POST)
    @ApiOperation(value = "Get the transformation dynamic parameters", notes = "Returns the transformation parameters.")
    @Timed
    public GenericParameter dynamicParams(@ApiParam(value = "Transformation name.") @PathVariable("action") final String action, //
                                          @ApiParam(value = "The column id.") @RequestParam(value = "columnId", required = true) final String columnId, //
                                          @ApiParam(value = "Data set content as JSON") final InputStream content) {
        final DynamicType actionType = DynamicType.fromAction(action);
        if (actionType == null) {
            final TDPExceptionContext context = TDPExceptionContext.build().put("name", action);
            throw new TDPException(TransformationErrorCodes.UNKNOWN_DYNAMIC_ACTION, context);
        }
        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(content)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            return actionType.getGenerator(context).getParameters(columnId, dataSet);
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    /**
     * Get the available export types
     */
    @RequestMapping(value = "/export/types", method = GET)
    @ApiOperation(value = "Get the available export types")
    @Timed
    public void exportTypes(final HttpServletResponse response) {
        List<ExportType> exportTypes = exportFactory.getExportTypes();
        exportTypes.remove(ExportType.JSON);
        try {
            builder.build() //
                    .writer() //
                    .writeValue(response.getOutputStream(), exportTypes);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }
}
