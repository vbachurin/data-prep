package org.talend.dataprep.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.ErrorCode;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.metrics.Timed;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Common API that does not stand in either DataSet, Preparation nor Transform.
 */
@RestController
@Api(value = "api", basePath = "/api", description = "Common data-prep API")
public class CommonAPI extends APIService {

    /** List of supported error codes. */
    private List<ErrorCode> supportedErrors;

    /**
     * Initialize the list of supported error codes.
     */
    @PostConstruct
    public void initSupportedErrors() {
        supportedErrors = new ArrayList<>();
        supportedErrors.addAll(Arrays.asList(CommonErrorCodes.values()));
        supportedErrors.addAll(Arrays.asList(APIErrorCodes.values()));

        // TODO how to import these error codes properly ?
        // errors.addAll(Arrays.asList(DataSetErrorCodes.values()));
        // errors.addAll(Arrays.asList(PreparationErrorCodes.values()));
        // errors.addAll(Arrays.asList(TransformationErrorCodes.values()));
    }

    /**
     * Describe the supported error codes.
     * 
     * @param response the http response.
     */
    @RequestMapping(value = "/api/errors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all supported errors.", notes = "Returns the list of all supported errors.")
    @Timed
    public void listErrors(HttpServletResponse response) {

        LOG.debug("Listing errors");
        try {
            JsonGenerator generator = (new JsonFactory()).createGenerator(response.getOutputStream());
            generator.writeStartArray();

            for (ErrorCode error : supportedErrors) {

                generator.writeStartObject();
                generator.writeStringField("code", error.getCode()); //$NON-NLS-1$
                generator.writeStringField("http status code", String.valueOf(error.getHttpStatus())); //$NON-NLS-1$
                if (error.getExpectedContextEntries().size() > 0) {
                    generator.writeFieldName("context"); //$NON-NLS-1$
                    generator.writeStartArray();
                    for (String entry : error.getExpectedContextEntries()) {
                        generator.writeString(entry);
                    }
                    generator.writeEndArray();
                }
                generator.writeEndObject();
            }
            generator.writeEndArray();

            generator.flush();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }

    }

}
