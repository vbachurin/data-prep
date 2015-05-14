package org.talend.dataprep.api.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.command.dataset.*;
import org.talend.dataprep.api.service.command.transformation.SuggestColumnActions;
import org.talend.dataprep.api.service.command.transformation.SuggestDataSetActions;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.metrics.Timed;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "api", basePath = "/api", description = "Data Preparation API")
public class DataSetAPI extends APIService {

    @RequestMapping(value = "/api/datasets", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a data set", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, notes = "Create a new data set based on content provided in POST body. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too. Returns the id of the newly created data set.")
    public String create(
            @ApiParam(value = "User readable name of the data set (e.g. 'Finance Report 2015', 'Test Data Set').") @RequestParam(defaultValue = "", required = false) String name,
            @ApiParam(value = "content") InputStream dataSetContent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating dataset (pool: {} )...", getConnectionManager().getTotalStats());
        }
        HttpClient client = getClient();
        HystrixCommand<String> creation = getCommand(CreateDataSet.class, client, name, dataSetContent);
        String result = creation.execute();
        LOG.debug("Dataset creation done.");
        return result;
    }

    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Update a data set by id.", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, //
        notes = "Create or update a data set based on content provided in PUT body with given id. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too. Returns the id of the newly created data set.")
    public String createOrUpdateById(
            @ApiParam(value = "User readable name of the data set (e.g. 'Finance Report 2015', 'Test Data Set').") @RequestParam(defaultValue = "", required = false) String name,
            @ApiParam(value = "Id of the data set to update / create") @PathVariable(value = "id") String id,
            @ApiParam(value = "content") InputStream dataSetContent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating or updating dataset #{} (pool: {})...", id, getConnectionManager().getTotalStats());
        }
        HttpClient client = getClient();
        HystrixCommand<String> creation = getCommand(CreateOrUpdateDataSet.class, client, id, name,
                dataSetContent);
        String result = creation.execute();
        LOG.debug("Dataset creation or update for #{} done.", id);
        return result;
    }


    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Update a dataset.", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, //
        notes = "Update a data set based on content provided in PUT body with given id. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too.")
    public String update(
        @ApiParam(value = "Id of the data set to update / create") @PathVariable(value = "id") String id,
        @ApiParam(value = "content") InputStream dataSetContent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating or updating dataset #{} (pool: {})...", id, getConnectionManager().getTotalStats());
        }
        HttpClient client = getClient();
        HystrixCommand<String> creation = getCommand(UpdateDataSet.class, client, id, dataSetContent);
        String result = creation.execute();
        LOG.debug("Dataset creation or update for #{} done.", id);
        return result;
    }

    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a data set by id.", produces = MediaType.APPLICATION_JSON_VALUE, notes = "Get a data set based on given id.")
    public void get(
            @ApiParam(value = "Id of the data set to get") @PathVariable(value = "id") String id,
            @RequestParam(defaultValue = "true") @ApiParam(name = "metadata", value = "Include metadata information in the response") boolean metadata,
            @RequestParam(defaultValue = "true") @ApiParam(name = "columns", value = "Include columns metadata information in the response") boolean columns,
            @RequestParam(defaultValue = "false") @ApiParam(name = "preview", value = "Is it a preview of the data set") boolean preview,
            @RequestParam(defaultValue = "") @ApiParam(name = "sheetName", value = "Sheet name to preview") String sheetName,
            HttpServletResponse response) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Requesting dataset #{} (pool: {})...", id, getConnectionManager().getTotalStats());
        }
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        HttpClient client = getClient();
        HystrixCommand<InputStream> retrievalCommand = getCommand( DataSetGet.class, client, id,
                                                                   metadata, columns, preview, sheetName );
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(retrievalCommand.execute(), outputStream);
            outputStream.flush();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Request dataset #{} (pool: {}) done.", id, getConnectionManager().getTotalStats());
            }
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }


    @RequestMapping(value = "/api/datasets", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List data sets.", produces = MediaType.APPLICATION_JSON_VALUE, notes = "Returns a list of data sets the user can use.")
    public void list(HttpServletResponse response) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing datasets (pool: {})...", getConnectionManager().getTotalStats());
        }
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        HttpClient client = getClient();
        HystrixCommand<InputStream> listCommand = getCommand(DataSetList.class, client);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(listCommand.execute(), outputStream);
            outputStream.flush();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Listing datasets (pool: {}) done.", getConnectionManager().getTotalStats());
            }
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Delete a data set by id", notes = "Delete a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    @Timed
    public void delete(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to delete") String dataSetId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Delete dataset #{} (pool: {})...", dataSetId, getConnectionManager().getTotalStats());
        }
        HttpClient client = getClient();
        HystrixCommand<Void> deleteCommand = getCommand(DataSetDelete.class, client, dataSetId);

        deleteCommand.execute();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing datasets (pool: {}) done.", getConnectionManager().getTotalStats());
        }
    }

    @RequestMapping(value = "/api/datasets/{id}/processcertification", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Ask certification for a dataset", notes = "Advance certification step of this dataset.")
    @Timed
    public void processCertification(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to update") String dataSetId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ask certification for dataset #{}", dataSetId);
        }
        HttpClient client = getClient();
        HystrixCommand<Void> command = getCommand(DatasetCertification.class, client, dataSetId);
        command.execute();
    }

    @RequestMapping(value = "/api/datasets/{id}/{column}/actions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get suggested actions for a data set columns.", notes = "Returns the suggested actions for the given column in the dataset in decreasing order of likeness.")
    @Timed
    public void suggestColumnActions(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Data set id to get suggestions from.") String dataSetId,
            @PathVariable(value = "column") @ApiParam(name = "column", value = "Column name in the dataset. If column doesn't exist, operation returns no content.") String columnName,
            HttpServletResponse response) {
        // Get dataset metadata
        HttpClient client = getClient();
        HystrixCommand<DataSetMetadata> retrieveMetadata = getCommand(DataSetGetMetadata.class, client, dataSetId);
        // Asks transformation service for suggested actions for column type and domain
        HystrixCommand<InputStream> getSuggestedActions = getCommand(SuggestColumnActions.class, client, retrieveMetadata, columnName);
        // Returns actions
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(getSuggestedActions.execute(), outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @RequestMapping(value = "/api/datasets/{id}/actions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get suggested actions for a whole data set.", notes = "Returns the suggested actions for the given dataset in decreasing order of likeness.")
    @Timed
    public void suggestDatasetActions(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Data set id to get suggestions from.") String dataSetId,
            HttpServletResponse response) {
        // Get dataset metadata
        HttpClient client = getClient();
        HystrixCommand<DataSetMetadata> retrieveMetadata = getCommand(DataSetGetMetadata.class, client, dataSetId);
        // Asks transformation service for suggested actions for column type and domain
        HystrixCommand<InputStream> getSuggestedActions = getCommand(SuggestDataSetActions.class, client, retrieveMetadata);
        // Returns actions
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(getSuggestedActions.execute(), outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
