package org.talend.dataprep.transformation.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.json.DataSetMetadataModule;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.api.type.Types;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.transformation.exception.Messages;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.Cut;
import org.talend.dataprep.transformation.api.action.metadata.DeleteEmpty;
import org.talend.dataprep.transformation.api.action.metadata.DeleteOnValue;
import org.talend.dataprep.transformation.api.action.metadata.FillWithDefaultIfEmpty;
import org.talend.dataprep.transformation.api.action.metadata.FillWithDefaultIfEmptyBoolean;
import org.talend.dataprep.transformation.api.action.metadata.FillWithDefaultIfEmptyInteger;
import org.talend.dataprep.transformation.api.action.metadata.LowerCase;
import org.talend.dataprep.transformation.api.action.metadata.Negate;
import org.talend.dataprep.transformation.api.action.metadata.UpperCase;
import org.talend.dataprep.transformation.api.transformer.SimpleTransformerFactory;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;

import com.wordnik.swagger.annotations.*;

@RestController
@Api(value = "transformations", basePath = "/transform", description = "Transformations on data")
public class TransformationService {

    private final TransformerFactory factory = new SimpleTransformerFactory();

    @RequestMapping(value = "/transform", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Transform input data", notes = "This operation returns the input data transformed using the supplied actions.")
    @VolumeMetered
    public void transform(
            @ApiParam(value = "Actions to perform on content (encoded in Base64).") @RequestParam(value = "actions", defaultValue = "", required = false) String actions,
            @ApiParam(value = "Data set content as JSON") InputStream content, HttpServletResponse response) {
        try {
            Transformer transformer = factory.get(new String(Base64.getDecoder().decode(actions)));
            transformer.transform(content, response.getOutputStream());
        } catch (IOException e) {
            throw Exceptions.User(Messages.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @RequestMapping(value = "/suggest/column", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Suggest actions for a given column metadata", notes = "This operation returns an array of suggested actions in decreasing order of importance.")
    @ApiResponses({ @ApiResponse(code = 500, message = "Internal error") })
    public @ResponseBody List<ActionMetadata> suggest(@RequestBody(required = false) ColumnMetadata column) {
        if (column == null) {
            return Collections.emptyList();
        }
        String typeName = column.getType();
        Type type = Types.get(typeName);
        if (Types.STRING.isAssignableFrom(type)) {
            return Arrays.asList(UpperCase.INSTANCE, LowerCase.INSTANCE, FillWithDefaultIfEmpty.INSTANCE, Cut.INSTANCE,
                    DeleteEmpty.INSTANCE, DeleteOnValue.INSTANCE);
        } else if (Types.BOOLEAN.isAssignableFrom(type)) {
            return Arrays.asList(Negate.INSTANCE, FillWithDefaultIfEmptyBoolean.INSTANCE);
        } else if (Types.INTEGER.isAssignableFrom(type)) {
            return Collections.singletonList(FillWithDefaultIfEmptyInteger.INSTANCE);
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/suggest/dataset", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Suggest actions for a given data set metadata", notes = "This operation returns an array of suggested actions in decreasing order of importance.")
    @ApiResponses({ @ApiResponse(code = 500, message = "Internal error") })
    public @ResponseBody List<ActionMetadata> suggest(InputStream dataset) {
        if (dataset == null) {
            return Collections.emptyList();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(DataSetMetadataModule.DEFAULT);
            DataSetMetadata dataSetMetadata = objectMapper.reader(DataSetMetadata.class).readValue(dataset);
            // Temporary: no data set actions at this moment
            if (dataSetMetadata != null) {
                return Collections.emptyList();
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            throw Exceptions.User(Messages.UNABLE_TO_COMPUTE_DATASET_ACTIONS, e);
        }
    }

}
