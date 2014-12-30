package org.talend.dataprep.dataset.service;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.dataset.store.DataSet;
import org.talend.dataprep.dataset.store.DataSetRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@Api(value = "datasets", basePath = "/datasets", description = "Operations on data sets")
public class DataSetService {

    private final JsonFactory factory = new JsonFactory();

    @Autowired
    private DataSetRepository dataSetRepository;

    @RequestMapping(value = "/datasets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all data sets", notes = "Returns the list of data sets the current user is allowed to see.")
    public void list(HttpServletResponse response) {
        Iterable<DataSet> dataSets = dataSetRepository.list();
        try (JsonGenerator generator = factory.createJsonGenerator(response.getOutputStream())) {
            generator.writeStartArray();
            for (DataSet dataSet : dataSets) {
                generator.writeString(dataSet.getId());
            }
            generator.writeEndArray();
            generator.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected I/O exception during message output.", e);
        }
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a data set", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, notes = "Create a new data set based on content provided in POST body. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too. Returns the id of the newly created data set.")
    public String create(@ApiParam(value = "content") InputStream dataSetContent) {
        String id = UUID.randomUUID().toString();
        dataSetRepository.add(new DataSet(id));
        return id;
    }

    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a data set by id", notes = "Get a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    public void get(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the requested data set") String dataSetId, HttpServletResponse response) {
        DataSet dataSet = dataSetRepository.get(dataSetId);
        try (JsonGenerator generator = factory.createJsonGenerator(response.getOutputStream())) {
            generator.writeStartArray();
            generator.writeString(dataSet.getId());
            generator.writeEndArray();
            generator.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected I/O exception during message output.", e);
        }
    }

    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Delete a data set by id", notes = "Delete a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    public void delete(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to delete") String dataSetId) {
        dataSetRepository.remove(dataSetId);
    }

    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Update a data set by id", consumes = "text/plain", notes = "Update a data set content based on provided id and PUT body. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too.")
    public void update(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to update") String dataSetId,
            @ApiParam(value = "content") InputStream dataSetContent) {
        dataSetRepository.add(new DataSet(dataSetId));
    }
}
