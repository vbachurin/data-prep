package org.talend.dataprep.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.command.ErrorList;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.ErrorCode;
import org.talend.dataprep.exception.JsonErrorCode;
import org.talend.dataprep.metrics.Timed;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Common API that does not stand in either DataSet, Preparation nor Transform.
 */
@RestController
@Api(value = "api", basePath = "/api", description = "Common data-prep API")
public class CommonAPI extends APIService {

    /**
     * Describe the supported error codes.
     * 
     * @param response the http response.
     */
    @RequestMapping(value = "/api/errors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all supported errors.", notes = "Returns the list of all supported errors.")
    @Timed
    public void listErrors(HttpServletResponse response) throws IOException {

        LOG.debug("Listing supported error codes");

        OutputStream output = response.getOutputStream();

        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(output);
        generator.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        generator.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);

        generator.setCodec(new ObjectMapper());

        // start the errors array
        generator.writeStartArray();

        // write the direct known errors
        writeErrorsFromEnum(generator, CommonErrorCodes.values());
        writeErrorsFromEnum(generator, APIErrorCodes.values());

        // get dataset api errors
        HttpClient client = getClient();
        HystrixCommand<InputStream> datasetErrors = getCommand(ErrorList.class, client, contentServiceUrl,
                PreparationAPI.DATASET_GROUP);
        writeErrorsFromApi(factory, generator, datasetErrors.execute());

        // get preparation api errors
        HystrixCommand<InputStream> preparationErrors = getCommand(ErrorList.class, client, preparationServiceURL
                + "/preparations", PreparationAPI.PREPARATION_GROUP);
        writeErrorsFromApi(factory, generator, preparationErrors.execute());

        // get transformation api errors
        HystrixCommand<InputStream> transformationErrors = getCommand(ErrorList.class, client,
                transformServiceUrl + "/transform", PreparationAPI.TRANSFORM_GROUP);
        writeErrorsFromApi(factory, generator, transformationErrors.execute());

        // close the errors array
        generator.writeEndArray();
        generator.flush();

        output.flush();
    }

    /**
     * Write the given error codes to the generator.
     *
     * @param generator the json generator to use.
     * @param codes the error codes to write.
     * @throws IOException if an error occurs.
     */
    private void writeErrorsFromEnum(JsonGenerator generator, ErrorCode[] codes) throws IOException {
        for (ErrorCode code : codes) {
            // cast to JsonErrorCode needed to ease json handling
            JsonErrorCode mock = new JsonErrorCode(code);
            generator.writeObject(mock);
        }
        generator.flush();
    }

    /**
     * Write the given error codes to the generator.
     *
     * @param generator the json generator to use.
     * @param input the error codes to write to read from the input stream.
     * @throws IOException if an error occurs.
     */
    private void writeErrorsFromApi(JsonFactory factory, JsonGenerator generator, InputStream input) throws IOException {
        JsonParser parser = factory.createParser(input);
        parser.setCodec(new ObjectMapper());
        Iterator<List<JsonErrorCode>> iterator = parser.readValuesAs(new TypeReference<List<JsonErrorCode>>() {
        });
        List<JsonErrorCode> errors = iterator.next();
        for (JsonErrorCode error : errors) {
            generator.writeObject(error);
        }
        generator.flush();
    }
}
