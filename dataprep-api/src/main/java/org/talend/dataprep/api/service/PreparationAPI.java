package org.talend.dataprep.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.api.PreviewDiffInput;
import org.talend.dataprep.api.service.api.PreviewUpdateInput;
import org.talend.dataprep.api.service.command.*;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.metrics.Timed;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "api", basePath = "/api", description = "Data Preparation API")
public class PreparationAPI extends APIService {

    @RequestMapping(value = "/api/preparations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all preparations.", notes = "Returns the list of preparations the current user is allowed to see.")
    @Timed
    public void listPreparations(
            @RequestParam(value = "format", defaultValue = "long") @ApiParam(name = "format", value = "Format of the returned document (can be 'long' or 'short'). Defaults to 'long'.") String format,
            HttpServletResponse response) {
        LOG.debug("Listing preparations (pool: {} )...", getConnectionManager().getTotalStats());
        PreparationList.Format listFormat = PreparationList.Format.valueOf(format.toUpperCase());
        HttpClient client = getClient();
        HystrixCommand<InputStream> command = getCommand(PreparationList.class, client, preparationServiceURL, listFormat);
        try {
            response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(command.execute(), outputStream);
            outputStream.flush();
            LOG.debug("Listed preparations (pool: {} )...", getConnectionManager().getTotalStats());
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @RequestMapping(value = "/api/preparations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a new preparation for preparation content in body.", notes = "Returns the created preparation id.")
    @Timed
    public String createPreparation(
            @ApiParam(name = "body", value = "The original preparation. You may set all values, service will override values you can't write to.") @RequestBody Preparation preparation,
            HttpServletResponse response) {
        LOG.debug("Creating preparation (pool: {} )...", getConnectionManager().getTotalStats());
        HttpClient client = getClient();
        PreparationCreate preparationCreate = getCommand(PreparationCreate.class, client, preparationServiceURL, preparation);
        final String preparationId = preparationCreate.execute();
        LOG.debug("Created preparation (pool: {} )...", getConnectionManager().getTotalStats());
        return preparationId;
    }

    @RequestMapping(value = "/api/preparations/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Update a preparation with content in body.", notes = "Returns the updated preparation id.")
    @Timed
    public String updatePreparation(
            @ApiParam(name = "id", value = "The id of the preparation to update.") @PathVariable("id") String id,
            @ApiParam(name = "body", value = "The updated preparation. Null values are ignored during update. You may set all values, service will override values you can't write to.") @RequestBody Preparation preparation,
            HttpServletResponse response) {
        LOG.debug("Updating preparation (pool: {} )...", getConnectionManager().getTotalStats());
        HttpClient client = getClient();
        PreparationUpdate preparationUpdate = getCommand(PreparationUpdate.class, client, preparationServiceURL, id, preparation);
        final String preparationId = preparationUpdate.execute();
        LOG.debug("Updated preparation (pool: {} )...", getConnectionManager().getTotalStats());
        return preparationId;
    }

    @RequestMapping(value = "/api/preparations/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Delete a preparation by id", notes = "Delete a preparation content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing preparation id returns empty content.")
    @Timed
    public String deletePreparation(
            @ApiParam(name = "id", value = "The id of the preparation to delete.") @PathVariable("id") String id) {
        LOG.debug("Deleting preparation (pool: {} )...", getConnectionManager().getTotalStats());
        HttpClient client = getClient();
        PreparationDelete preparationDelete = getCommand(PreparationDelete.class, client, preparationServiceURL, id);
        final String preparationId = preparationDelete.execute();
        LOG.debug("Deleted preparation (pool: {} )...", getConnectionManager().getTotalStats());
        return preparationId;
    }

    @RequestMapping(value = "/api/preparations/{id}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a preparation by id and details.", notes = "Returns the preparation details.")
    @Timed
    public void getPreparation(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Preparation id.") String preparationId,
            HttpServletResponse response) {
        LOG.debug("Retrieving preparation details (pool: {} )...", getConnectionManager().getTotalStats());
        HttpClient client = getClient();
        HystrixCommand<InputStream> command = getCommand(PreparationGet.class, client, preparationServiceURL, preparationId);
        try {
            // You cannot use Preparation object mapper here: to serialize steps & actions, you'd need a version
            // repository not available at API level. Code below copies command result direct to response.
            response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(command.execute(), outputStream);
            outputStream.flush();
            LOG.debug("Retrieved preparation details (pool: {} )...", getConnectionManager().getTotalStats());
        }
        catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @RequestMapping(value = "/api/preparations/{id}/content", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get preparation content by id and at a given version.", notes = "Returns the preparation content at version.")
    @Timed
    public void getPreparation(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Preparation id.") String preparationId,
            @RequestParam(value = "version", defaultValue = "head") @ApiParam(name = "version", value = "Version of the preparation (can be 'origin', 'head' or the version id). Defaults to 'head'.") String version,
            HttpServletResponse response) {
        LOG.debug("Retrieving preparation content (pool: {} )...", getConnectionManager().getTotalStats());
        HttpClient client = getClient();
        HystrixCommand<InputStream> command = getCommand(PreparationGetContent.class, client, preparationServiceURL,
                contentServiceUrl, transformServiceUrl, preparationId, version);
        try {
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(command.execute(), outputStream);
            outputStream.flush();
            LOG.debug("Retrieved preparation content (pool: {} )...", getConnectionManager().getTotalStats());
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @RequestMapping(value = "/api/preparations/{id}/actions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an action at the end of preparation.", notes = "Does not return any value, client may expect successful operation based on HTTP status code.")
    @Timed
    public void addPreparationAction(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Preparation id.") String preparationId,
            @ApiParam("Action to add at end of the preparation.") InputStream body, HttpServletResponse response) {
        LOG.debug("Adding action to preparation (pool: {} )...", getConnectionManager().getTotalStats());
        HttpClient client = getClient();
        HystrixCommand<Void> command = getCommand(PreparationAddAction.class, client, preparationServiceURL, preparationId, body);
        command.execute();
        LOG.debug("Added action to preparation (pool: {} )...", getConnectionManager().getTotalStats());
    }

    @RequestMapping(value = "/api/preparations/{id}/actions/{action}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates an action in the preparation.", notes = "Does not return any value, client may expect successful operation based on HTTP status code.")
    @Timed
    public void updatePreparationAction(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Preparation id.") String preparationId,
            @PathVariable(value = "action") @ApiParam(name = "action", value = "Step id in the preparation.") String stepId,
            @ApiParam("New content for the action.") InputStream body, HttpServletResponse response) {
        LOG.debug("Updating preparation action at step #{} (pool: {} )...", stepId, getConnectionManager().getTotalStats());
        HttpClient client = getClient();
        HystrixCommand<Void> command = getCommand(PreparationUpdateAction.class, client, preparationServiceURL, preparationId,
                stepId, body);
        command.execute();
        LOG.debug("Updated preparation action at step #{} (pool: {} )...", stepId, getConnectionManager().getTotalStats());
    }

    // ---------------------------------------------------------------------------------
    // ----------------------------------------PREVIEW----------------------------------
    // ---------------------------------------------------------------------------------

    @RequestMapping(value = "/api/preparations/preview/diff", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void previewDiff(@RequestBody PreviewDiffInput input, HttpServletResponse response) {
        try {
            HystrixCommand<InputStream> transformation = getCommand(PreviewDiff.class, getClient(), contentServiceUrl,
                    transformServiceUrl, preparationServiceURL, input);
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(transformation.execute(), outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_TRANSFORM_DATASET, e);
        }
    }

    @RequestMapping(value = "/api/preparations/preview/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void previewUpdate(@RequestBody PreviewUpdateInput input, HttpServletResponse response) {
        try {
            HystrixCommand<InputStream> transformation = getCommand(PreviewUpdate.class, getClient(), contentServiceUrl,
                    transformServiceUrl, preparationServiceURL, input);
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(transformation.execute(), outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_TRANSFORM_DATASET, e);
        }
    }
}
