package org.talend.dataprep.dataset.service;

import static org.talend.dataprep.api.dataset.DataSetMetadata.Builder.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

import javax.jms.Message;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.json.DataSetMetadataModule;
import org.talend.dataprep.api.dataset.json.SimpleDataSetMetadataJsonSerializer;
import org.talend.dataprep.dataset.exception.DataSetMessages;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.*;

@RestController
@Api(value = "datasets", basePath = "/datasets", description = "Operations on data sets")
public class DataSetService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-YYYY HH:mm"); //$NON-NLS-1

    private static final Logger LOG = LoggerFactory.getLogger( DataSetService.class );

    private final JsonFactory factory = new JsonFactory();

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    private DataSetMetadataRepository dataSetMetadataRepository;

    @Autowired
    private DataSetContentStore contentStore;

    @Autowired
    private SimpleDataSetMetadataJsonSerializer metadataJsonSerializer;

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
    }

    private static void queueEvents(String id, JmsTemplate template) {
        String[] destinations = { Destinations.SCHEMA_ANALYSIS, Destinations.CONTENT_ANALYSIS };
        for (String destination : destinations) {
            template.send(destination, session -> {
                Message message = session.createMessage();
                message.setStringProperty("dataset.id", id); //$NON-NLS-1
                    return message;
                });
        }
    }

    /**
     * @return Get user name from Spring Security context, return "anonymous" if no user is currently logged in.
     */
    private static String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String author;
        if (principal != null) {
            author = principal.toString();
        } else {
            author = "anonymous"; //$NON-NLS-1
        }
        return author;
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all data sets", notes = "Returns the list of data sets the current user is allowed to see. Creation date is always displayed in UTC time zone.")
    @Timed
    public void list(final HttpServletResponse response) {
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        final Iterable<DataSetMetadata> dataSets = dataSetMetadataRepository.list();
        try (final JsonGenerator generator = factory.createGenerator(response.getOutputStream())) {
            generator.writeStartArray();
            for (DataSetMetadata dataSetMetadata : dataSets) {
                metadataJsonSerializer.serialize(dataSetMetadata, generator);
            }
            generator.writeEndArray();
            generator.flush();
        } catch (IOException e) {
            throw Exceptions.Internal(DataSetMessages.UNEXPECTED_IO_EXCEPTION, e);
        }
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a data set", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, notes = "Create a new data set based on content provided in POST body. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too. Returns the id of the newly created data set.")
    @Timed
    @VolumeMetered
    public String create(
            @ApiParam(value = "User readable name of the data set (e.g. 'Finance Report 2015', 'Test Data Set').") @RequestParam(defaultValue = "", required = false) String name,
            @ApiParam(value = "content") InputStream dataSetContent, HttpServletResponse response) {
        response.setHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE); //$NON-NLS-1$
        final String id = UUID.randomUUID().toString();
        DataSetMetadata dataSetMetadata = metadata().id(id).name(name).author(getUserName()).created(System.currentTimeMillis())
                .build();
        // Save data set content
        contentStore.storeAsRaw(dataSetMetadata, dataSetContent);
        // Create the new data set
        dataSetMetadataRepository.add(dataSetMetadata);
        // Queue events (schema analysis, content indexing for search...)
        queueEvents(id, jmsTemplate);
        return id;
    }

    @RequestMapping(value = "/datasets/{id}/content", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a data set by id", notes = "Get a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    @Timed
    public void get(
            @RequestParam(defaultValue = "true") @ApiParam(name = "metadata", value = "Include metadata information in the response") boolean metadata,
            @RequestParam(defaultValue = "true") @ApiParam(name = "columns", value = "Include column information in the response") boolean columns,
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the requested data set") String dataSetId,
            HttpServletResponse response) {
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
        if (dataSetMetadata == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return; // No data set, returns empty content.
        }
        if (!dataSetMetadata.getLifecycle().schemaAnalyzed()) {
            // Schema is not yet ready (but eventually will, returns 202 to indicate this).
            LOG.debug("Data set #{} not yet ready for service.", dataSetId);

            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }

        try (JsonGenerator generator = factory.createGenerator(response.getOutputStream())) {
            // Write general information about the dataset
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(DataSetMetadataModule.get(metadata, columns, contentStore.get(dataSetMetadata)));
            mapper.writer().writeValue(generator, dataSetMetadata);
            generator.flush();
        } catch (IOException e) {
            throw Exceptions.Internal(DataSetMessages.UNEXPECTED_IO_EXCEPTION, e);
        }
    }

    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Delete a data set by id", notes = "Delete a data set content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content.")
    @Timed
    public void delete(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to delete") String dataSetId) {
        DataSetMetadata metadata = dataSetMetadataRepository.get(dataSetId);
        if (metadata != null) {
            contentStore.delete(metadata);
            dataSetMetadataRepository.remove(dataSetId);
        }
    }

    @RequestMapping(value = "/datasets/{id}/raw", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Update a data set by id", consumes = "text/plain", notes = "Update a data set content based on provided id and PUT body. Id should be a UUID returned by the list operation. Not valid or non existing data set id returns empty content. For documentation purposes, body is typed as 'text/plain' but operation accepts binary content too.")
    @Timed
    @VolumeMetered
    public void updateRawDataSet(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set to update") String dataSetId,
            @RequestParam(value = "name", required = false) @ApiParam(name = "name", value = "New value for the data set name") String name,
            @ApiParam(value = "content") InputStream dataSetContent) {
        DataSetMetadata.Builder builder = metadata().id(dataSetId);
        if (name != null) {
            builder = builder.name(name);
        }
        DataSetMetadata dataSetMetadata = builder.build();
        // Save data set content
        contentStore.storeAsRaw(dataSetMetadata, dataSetContent);
        dataSetMetadataRepository.add(dataSetMetadata);
        // Content was changed, so queue events (schema analysis, content indexing for search...)
        queueEvents(dataSetId, jmsTemplate);
    }

    @RequestMapping(value = "/datasets/{id}/metadata", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Get metadata information of a data set by id", notes = "Get metadata information of a data set by id. Not valid or non existing data set id returns empty content.")
    @ApiResponses({@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Data set does not exist.")})
    @Timed
    public void getMetadata(
            @PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the data set metadata") String dataSetId,
            HttpServletResponse response) {
        if (dataSetId == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        DataSetMetadata metadata = dataSetMetadataRepository.get(dataSetId);
        if (metadata == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        try (JsonGenerator generator = factory.createGenerator(response.getOutputStream())) {
            // Write general information about the dataset
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(DataSetMetadataModule.get(true, true, null));
            mapper.writer().writeValue(generator, metadata);
            generator.flush();
        } catch (IOException e) {
            throw Exceptions.Internal(DataSetMessages.UNEXPECTED_IO_EXCEPTION, e);
        }
    }

}
