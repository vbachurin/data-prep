package org.talend.dataprep.preparation.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

@RestController
@Api(value = "preparations", basePath = "/preparations", description = "Operations on preparations")
public class PreparationService {

    @Autowired
    private PreparationRepository repository;

    private final JsonFactory factory = new JsonFactory();

    @RequestMapping(value = "/preparations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations the current user is allowed to see. Creation date is always displayed in UTC time zone.")
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

    @RequestMapping(value = "/preparations", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a preparation", notes = "Returns the id of the created preparation.")
    @Timed
    public String create(@ApiParam(value = "content") InputStream preparationContent) {
        try {
            String dataSetId = IOUtils.toString(preparationContent);
            Preparation preparation = new Preparation(dataSetId);
            repository.add(preparation);
            return preparation.getId();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create preparation.", e);
        }
    }

}
