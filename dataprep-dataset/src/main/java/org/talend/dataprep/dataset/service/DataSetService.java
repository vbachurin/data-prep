package org.talend.dataprep.dataset.service;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@Api(value = "datasets", basePath = "/datasets", description = "Operations on data sets")
public class DataSetService {

    private static final Log LOGGER = LogFactory.getLog(DataSetService.class);

    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "List all data sets", notes = "Returns the list of data sets the current user is allowed to see.")
    public void list() {
        LOGGER.info("DataSetService.list");
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.POST)
    @ApiOperation(value = "Create a data set", consumes = "text/plain", notes = "Create a new data set based on content provided in POST body. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too.")
    public void create(@ApiParam(value = "content") InputStream dataSetContent) {
        LOGGER.info("DataSetService.create");
    }

    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a data set by id", notes = "Get a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    public void get(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the requested data set") String dataSetId) {
        LOGGER.info("DataSetService.get (" + dataSetId + ")");
    }

    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a data set by id", notes = "Delete a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    public void delete(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to delete") String dataSetId) {
        LOGGER.info("DataSetService.delete (" + dataSetId + ")");
    }

    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update a data set by id", consumes = "text/plain", notes = "Update a data set content based on provided id and PUT body. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too.")
    public void update(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to update") String dataSetId, @ApiParam(value = "content") InputStream dataSetContent) {
        LOGGER.info("DataSetService.update (" + dataSetId + ")");
    }
}
