package org.talend.dataprep.preparation.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.preparation.Preparation;
import org.talend.dataprep.preparation.store.PreparationRepository;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "preparations", basePath = "/preparations", description = "Operations on preparations")
public class PreparationService {

    private static final Log LOGGER = LogFactory.getLog(PreparationService.class);

    @Autowired
    private PreparationRepository repository;

    private final JsonFactory factory = new JsonFactory();

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

    @RequestMapping(value = "/preparations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations ids the current user is allowed to see. Creation date is always displayed in UTC time zone. See 'preparations/all' to get all details at once.")
    @Timed
    public void list(HttpServletResponse response) {
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        try (JsonGenerator generator = factory.createGenerator(response.getOutputStream())) {
            generator.writeStartArray();
            for (Preparation preparation : repository.list()) {
                generator.writeString(preparation.getId());
            }
            generator.writeEndArray();
            generator.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected I/O exception during message output.", e);
        }
    }

    @RequestMapping(value = "/preparations/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations the current user is allowed to see. Creation date is always displayed in UTC time zone. This operation return all details on the preparations.")
    @Timed
    public Iterable<Preparation> listAll(HttpServletResponse response) {
        return repository.list();
    }

    @RequestMapping(value = "/preparations", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a preparation", notes = "Returns the id of the created preparation.")
    @Timed
    public String create(@ApiParam(value = "content") InputStream preparationContent) {
        try {
            String dataSetId = IOUtils.toString(preparationContent);
            Preparation preparation = new Preparation(dataSetId);
            preparation.setAuthor(getUserName());
            repository.add(preparation);
            return preparation.getId();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create preparation.", e);
        }
    }

    @RequestMapping(value = "/preparations/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get preparation details", notes = "Return the details of the preparation with provided id.")
    @Timed
    public Preparation get(@ApiParam(value = "id") @PathVariable(value = "id") String id) {
        return repository.get(id);
    }

    @RequestMapping(value = "/preparations/{id}/actions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an action to a preparation", notes = "Append an action at end of the preparation with given id.")
    @Timed
    public void append(@ApiParam(value = "id") @PathVariable(value = "id") String id, @ApiParam(value = "action") InputStream body) {
        Preparation preparation = repository.get(id);
        if (preparation != null) {
            try {
                List<String> actions = preparation.getActions();
                String actionsAsString = IOUtils.toString(body);
                actions.add(actionsAsString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Added actions to preparation #" + id + ": " + actionsAsString);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to add actions to preparation #" + id, e);
            }
        } else {
            throw new RuntimeException("Preparation id #" + id + " does not exist.");
        }
    }
}
