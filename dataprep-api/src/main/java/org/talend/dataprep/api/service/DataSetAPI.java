package org.talend.dataprep.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.wordnik.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.service.command.*;
import org.talend.dataprep.metrics.Timed;

import com.netflix.hystrix.HystrixCommand;
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
            LOG.debug("Creating dataset (pool: " + connectionManager.getTotalStats() + ")...");
        }
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<String> creation = new CreateDataSet(client, contentServiceUrl, name, dataSetContent);
        String result = creation.execute();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Dataset creation done.");
        }
        return result;
    }

    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Update a data set by id.", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, notes = "Create or update a data set based on content provided in PUT body with given id. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too. Returns the id of the newly created data set.")
    public String createOrUpdateById(
            @ApiParam(value = "User readable name of the data set (e.g. 'Finance Report 2015', 'Test Data Set').") @RequestParam(defaultValue = "", required = false) String name,
            @ApiParam(value = "Id of the data set to update / create") @PathVariable(value = "id") String id,
            @ApiParam(value = "content") InputStream dataSetContent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating or updating dataset #" + id + " (pool: " + connectionManager.getTotalStats() + ")...");
        }
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<String> creation = new CreateOrUpdateDataSet(client, contentServiceUrl, id, name, dataSetContent);
        String result = creation.execute();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Dataset creation or update for #" + id + " done.");
        }
        return result;
    }

    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a data set by id.", produces = MediaType.APPLICATION_JSON_VALUE, notes = "Get a data set based on given id.")
    public void get(
            @ApiParam(value = "Id of the data set to get") @PathVariable(value = "id") String id,
            @RequestParam(defaultValue = "true") @ApiParam(name = "metadata", value = "Include metadata information in the response") boolean metadata,
            @RequestParam(defaultValue = "true") @ApiParam(name = "columns", value = "Include columns metadata information in the response") boolean columns,
            HttpServletResponse response) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Requesting dataset #" + id + " (pool: " + connectionManager.getTotalStats() + ")...");
        }
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<InputStream> retrievalCommand = new DataSetGetCommand(client, contentServiceUrl, id, metadata, columns);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(retrievalCommand.execute(), outputStream);
            outputStream.flush();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Request dataset #" + id + " (pool: " + connectionManager.getTotalStats() + ") done.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve content for id #" + id + ".", e);
        }
    }

    @RequestMapping(value = "/api/datasets", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List data sets.", produces = MediaType.APPLICATION_JSON_VALUE, notes = "Returns a list of data sets the user can use.")
    public void list(HttpServletResponse response) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing datasets (pool: " + connectionManager.getTotalStats() + ")...");
        }
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<InputStream> listCommand = new DataSetListCommand(client, contentServiceUrl);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(listCommand.execute(), outputStream);
            outputStream.flush();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Listing datasets (pool: " + connectionManager.getTotalStats() + ") done.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to list datasets.", e);
        }
    }

    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Delete a data set by id", notes = "Delete a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    @Timed
    public void delete(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to delete") String dataSetId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Delete dataset #" + dataSetId + " (pool: " + connectionManager.getTotalStats() + ")...");
        }
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<Void> deleteCommand = new DataSetDeleteCommand(client, contentServiceUrl, dataSetId);
        try {
            deleteCommand.execute();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Listing datasets (pool: " + connectionManager.getTotalStats() + ") done.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to list datasets.", e);
        }
    }

    @RequestMapping(value = "/api/datasets/{id}/{column}/actions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get suggested actions for a data set columns.", notes = "Returns the suggested actions for the given column in the dataset in decreasing order of likeness.")
    @Timed
    public void suggestColumnActions(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Data set id to get suggestions from.") String dataSetId,
            @PathVariable(value = "column") @ApiParam(name = "column", value = "Column name in the dataset. If column doesn't exist, operation returns no content.") String columnName,
            HttpServletResponse response) {
        // Get dataset metadata
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<DataSetMetadata> retrieveMetadata = new DataSetGetMetadataCommand(client, contentServiceUrl, dataSetId);
        // Asks transformation service for suggested actions for column type and domain
        HystrixCommand<InputStream> getSuggestedActions = new SuggestColumnActionsCommand(client, transformServiceUrl, retrieveMetadata, columnName);
        // Returns actions
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(getSuggestedActions.execute(), outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable to retrieve actions for column '" + columnName + "' in dataset #" + dataSetId + ".", e);
        }
    }

    @RequestMapping(value = "/api/datasets/{id}/actions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get suggested actions for a whole data set.", notes = "Returns the suggested actions for the given dataset in decreasing order of likeness.")
    @Timed
    public void suggestDatasetActions(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Data set id to get suggestions from.") String dataSetId,
            HttpServletResponse response) {
        // Get dataset metadata
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<DataSetMetadata> retrieveMetadata = new DataSetGetMetadataCommand(client, contentServiceUrl, dataSetId);
        // Asks transformation service for suggested actions for column type and domain
        HystrixCommand<InputStream> getSuggestedActions = new SuggestDataSetActionsCommand(client, transformServiceUrl, retrieveMetadata);
        // Returns actions
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(getSuggestedActions.execute(), outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable to retrieve actions for dataset #" + dataSetId + ".", e);
        }
    }

}
