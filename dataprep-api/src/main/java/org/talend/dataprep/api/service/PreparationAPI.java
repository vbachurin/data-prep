package org.talend.dataprep.api.service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.service.command.PreparationListCommand;
import org.talend.dataprep.metrics.Timed;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.talend.dataprep.preparation.Preparation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@RestController
@Api(value = "api", basePath = "/api", description = "Data Preparation API")
public class PreparationAPI extends APIService {

    @RequestMapping(value = "/api/preparations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a preparation by id and at a given version.", notes = "Returns the dataset modified by this preparation.")
    @Timed
    public void listTransformations(
            @RequestParam(value = "format", defaultValue = "long") @ApiParam(name = "format", value = "Format of the returned document (can be 'long' or 'short'). Defaults to 'long'.") String format,
            HttpServletResponse response) {
        PreparationListCommand.Format listFormat = PreparationListCommand.Format.valueOf(format.toUpperCase());
        HttpClient client = getClient();
        PreparationListCommand command = new PreparationListCommand(client, preparationServiceURL, listFormat);
        try {
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(command.execute(), outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable to copy preparations to output.", e);
        }
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
