package org.talend.dataprep.services.service;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;

@RestController
@Api(value = "api", basePath = "/api", description = "Data Prep API")
public class DataPreparationService {

    private static final HystrixCommandGroupKey TRANSFORM_GROUP = () -> "org.talend.dataprep.services.DataPreparationService.transform"; //$NON-NLS-1$

    private static final HttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    @Value("${transformation.service.url}")
    private String transformServiceUrl;

    @Value("${dataset.service.url}")
    private String contentServiceUrl;

    @RequestMapping(value = "/api/transform", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Transform a data set", notes = "Returns the data set modified with the provided actions.")
    public void transform(
            @ApiParam(value = "Actions to perform on data set") @RequestParam(value = "actions", defaultValue = "", required = false) String actions,
            @ApiParam(value = "Data set id") @RequestParam(value = "dataSetId", defaultValue = "", required = true) String dataSetId,
            HttpServletResponse response) {
        // Configure transformation flow
        HttpClient client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        HystrixCommand<InputStream> contentRetrieval = new RetrievalCommand(client, dataSetId);
        HystrixCommand<InputStream> transformation = new TransformCommand(client, contentRetrieval, actions);
        // Perform transformation
        try {
            IOUtils.copy(transformation.execute(), response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Unable to transform data set #" + dataSetId + ".", e);
        }
    }

    private static abstract class ChainedCommand<O, I> extends HystrixCommand<O> {

        private HystrixCommand<I> input;

        public ChainedCommand(HystrixCommand<I> input) {
            super(input.getCommandGroup());
            this.input = input;
        }

        public I getInput() {
            return input.observe().toBlocking().first();
        }

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
            String uri = transformServiceUrl + "/?actions=" + URLEncoder.encode(actions, "UTF-8");
            HttpPost transformationCall = new HttpPost(uri);
            transformationCall.setEntity(new InputStreamEntity(getInput()));
            return client.execute(transformationCall).getEntity().getContent();
        }
    }

    private class RetrievalCommand extends HystrixCommand<InputStream> {

        private final HttpClient client;

        private final String dataSetId;

        public RetrievalCommand(HttpClient client, String dataSetId) {
            super(DataPreparationService.TRANSFORM_GROUP);
            this.client = client;
            this.dataSetId = dataSetId;
        }

        @Override
        protected InputStream run() throws Exception {
            // http://localhost:8080/datasets/45aba7c4-ff23-45c2-acfe-c8658ad55598/content?metadata=false
            HttpGet contentRetrieval = new HttpGet(contentServiceUrl + "/" + dataSetId + "/content?metadata=false");
            return client.execute(contentRetrieval).getEntity().getContent();
        }
    }
}
