package org.talend.dataprep.api.service;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@RestController
@Api(value = "api", basePath = "/api", description = "Data Preparation API")
public class DataPreparationAPI {

    private static final HystrixCommandGroupKey TRANSFORM_GROUP = () -> "org.talend.dataprep.api.transform"; //$NON-NLS-1$

    private static final HttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    @Value("${transformation.service.url}")
    private String transformServiceUrl;

    @Value("${dataset.service.url}")
    private String contentServiceUrl;

    @RequestMapping(value = "/api/transform", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Transforms a data set given data set id. This operation retrieves data set content and pass it to the transformation service.", notes = "Returns the data set modified with the provided actions.")
    public void transform(
            @ApiParam(value = "Actions to perform on data set (as JSON format).") @RequestParam(value = "actions", defaultValue = "", required = false) String actions,
            @ApiParam(value = "Data set id.") @RequestParam(value = "dataSetId", defaultValue = "", required = true) String dataSetId,
            HttpServletResponse response) {
        // Configure transformation flow
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<InputStream> contentRetrieval = new RetrievalCommand(client, dataSetId);
        HystrixCommand<InputStream> transformation = new TransformCommand(client, contentRetrieval, actions);
        // Perform transformation
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(transformation.execute(), outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException("Unable to transform data set #" + dataSetId + ".", e);
        }
    }

    void setDataSetServiceURL(String dataSetServiceURL) {
        this.contentServiceUrl = dataSetServiceURL;
    }

    void setTransformationServiceURL(String transformationServiceURL) {
        this.transformServiceUrl = transformationServiceURL;
    }

    private class TransformCommand extends ChainedCommand<InputStream, InputStream> {

        private final String actions;

        private final HttpClient client;

        public TransformCommand(HttpClient client, HystrixCommand<InputStream> content, String actions) {
            super(content);
            this.actions = actions;
            this.client = client;
        }

        @Override
        protected InputStream run() throws Exception {
            String uri = transformServiceUrl + "/?actions=" + Base64.getEncoder().encodeToString(actions.getBytes("UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
            HttpPost transformationCall = new HttpPost(uri);
            transformationCall.setEntity(new InputStreamEntity(getInput()));
            return client.execute(transformationCall).getEntity().getContent();
        }
    }

    private class RetrievalCommand extends HystrixCommand<InputStream> {

        private final HttpClient client;

        private final String dataSetId;

        public RetrievalCommand(HttpClient client, String dataSetId) {
            super(DataPreparationAPI.TRANSFORM_GROUP);
            this.client = client;
            this.dataSetId = dataSetId;
        }

        @Override
        protected InputStream run() throws Exception {
            HttpGet contentRetrieval = new HttpGet(contentServiceUrl + "/" + dataSetId + "/content?metadata=false");
            HttpResponse response = client.execute(contentRetrieval);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200) {
                if (statusCode == HttpStatus.SC_NO_CONTENT) {
                    return new ByteArrayInputStream(new byte[0]);
                } else if (statusCode == HttpStatus.SC_OK) {
                    return response.getEntity().getContent();
                }
            }
            throw new RuntimeException("Unable to retrieve content.");
        }
    }
}
