package org.talend.dataprep.transformation.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.json.DataSetMetadataModule;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.SimpleTransformerFactory;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;
import org.talend.dataprep.transformation.exception.Messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@RestController
@Api(value = "transformations", basePath = "/transform", description = "Transformations on data")
public class TransformationService {

    @Autowired
    private ActionMetadata[] allActions;

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
        Type type = Type.get(typeName);
        ArrayList<ActionMetadata> suggestedActions = new ArrayList<>();
        for (ActionMetadata am : allActions) {
            Set<Type> compatibleColumnTypes = am.getCompatibleColumnTypes();
            for (Type columnType : compatibleColumnTypes) {
                if (columnType.isAssignableFrom(type)) {
                    suggestedActions.add(am);
                }
            }
        }
        return suggestedActions;
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
