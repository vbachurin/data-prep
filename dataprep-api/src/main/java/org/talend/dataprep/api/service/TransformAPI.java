package org.talend.dataprep.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.command.DataSetGet;
import org.talend.dataprep.api.service.command.Transform;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.talend.dataprep.exception.TDPExceptionContext;

@RestController
@Api(value = "api", basePath = "/api", description = "Data Preparation API")
public class TransformAPI extends APIService {

    @RequestMapping(value = "/api/transform/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Transforms a data set given data set id. This operation retrieves data set content and pass it to the transformation service.", notes = "Returns the data set modified with the provided actions in request body.")
    public void transform(@PathVariable(value = "id") @ApiParam(value = "Data set id.") String dataSetId,
            @ApiParam(value = "Actions to perform on data set (as JSON format).") InputStream body, HttpServletResponse response) {
        if (dataSetId == null) {
            throw new IllegalArgumentException("Data set id cannot be null.");
        }
        LOG.debug("Transforming dataset id #{} (pool: {})...",dataSetId,getConnectionManager().getTotalStats());
        try {
            // Configure transformation flow
            String encodedActions = Base64.getEncoder().encodeToString(IOUtils.toByteArray(body));
            response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE); //$NON-NLS-1$
            HttpClient client = getClient();
            HystrixCommand<InputStream> contentRetrieval = getCommand(DataSetGet.class, client, contentServiceUrl, dataSetId, false, false);
            HystrixCommand<InputStream> transformation = getCommand(Transform.class, client, transformServiceUrl, contentRetrieval,
                    encodedActions);
            // Perform transformation
            ServletOutputStream outputStream = response.getOutputStream();

            // TODO temp log to see what's going in newbuild
            LOG.error("TransformAPI about to start");

            InputStream input = transformation.execute();

            // TODO temp log to see what's going in newbuild
            LOG.error("done, copying input to output");

            IOUtils.copyLarge(input, outputStream);
            outputStream.flush();

            // TODO temp log to see what's going in newbuild
            LOG.error("completely done !!!");

        } catch (IOException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_TRANSFORM_DATASET, e, TDPExceptionContext.build().put("dataSetId", dataSetId));
        }
 catch (Throwable e) {
            LOG.error("error while PreparationAPITest " + e.getMessage(), e);
            throw e;
        }

        // TODO temp log to see what's going in newbuild
        LOG.error("finished !!!");

        LOG.debug("Transformation of dataset id #{} done.", dataSetId);
    }
}
