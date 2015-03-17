package org.talend.dataprep.preparation.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.metrics.Timed;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@Api(value = "preparations", basePath = "/preparations", description = "Operations on preparations")
public class PreparationService {

    private final JsonFactory factory = new JsonFactory();

    @RequestMapping(value = "/preparations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations the current user is allowed to see. Creation date is always displayed in UTC time zone.")
    @Timed
    public void list(HttpServletResponse response) {
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
        try (JsonGenerator generator = factory.createGenerator(response.getOutputStream())) {
            generator.writeStartArray();
            generator.writeEndArray();
            generator.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected I/O exception during message output.", e);
        }
    }

}
