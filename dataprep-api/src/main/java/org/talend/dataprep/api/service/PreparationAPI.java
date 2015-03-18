package org.talend.dataprep.api.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.service.command.DataSetGetMetadataCommand;
import org.talend.dataprep.api.service.command.SuggestDataSetActionsCommand;
import org.talend.dataprep.metrics.Timed;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "api", basePath = "/api", description = "Data Preparation API")
public class PreparationAPI extends APIService {

    @RequestMapping(value = "/api/preparations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a preparation by id and at a given version.", notes = "Returns the dataset modified by this preparation.")
    @Timed
    public void listTransformations(
            @RequestParam(value = "format", defaultValue = "long") @ApiParam(name = "format", value = "Format of the returned document (can be 'long' or 'short'). Defaults to 'long'.") String dataSetId,
            HttpServletResponse response) {
    }

    @RequestMapping(value = "/api/preparations/{id}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a preparation by id and details.", notes = "Returns the preparation details.")
    @Timed
    public void getTransformation(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Preparation id.") String preparationId,
            HttpServletResponse response) {
    }

    @RequestMapping(value = "/api/preparations/{id}/content", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get preparation content by id and at a given version.", notes = "Returns the dataset modified by this preparation.")
    @Timed
    public void getTransformation(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Preparation id.") String preparationId,
            @RequestParam(value = "version", defaultValue = "head") @ApiParam(name = "version", value = "Version of the preparation (can be 'origin', 'head' or the version id). Defaults to 'head'.") String version,
            HttpServletResponse response) {
    }
}
