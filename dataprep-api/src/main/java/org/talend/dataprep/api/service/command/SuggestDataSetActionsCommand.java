package org.talend.dataprep.api.service.command;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.http.MediaType;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.json.DataSetMetadataModule;
import org.talend.dataprep.api.service.PreparationAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

public class SuggestDataSetActionsCommand extends ChainedCommand<InputStream, DataSetMetadata> {

    private final String transformServiceUrl;

    private final HttpClient client;

    public SuggestDataSetActionsCommand(HttpClient client, String transformServiceUrl,
            HystrixCommand<DataSetMetadata> retrieveMetadata) {
        super(PreparationAPI.TRANSFORM_GROUP, retrieveMetadata);
        this.transformServiceUrl = transformServiceUrl;
        this.client = client;
    }

    @Override
    protected InputStream run() throws Exception {
        HttpPost post = new HttpPost(transformServiceUrl + "/suggest/dataset");
        post.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        DataSetMetadata metadata = getInput();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(DataSetMetadataModule.DEFAULT);
        StringWriter dataSetMetadataJSON = new StringWriter();
        objectMapper.writer().writeValue(dataSetMetadataJSON, metadata);
        post.setEntity(new StringEntity(dataSetMetadataJSON.toString()));
        HttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_ACCEPTED) {
                // Immediately release connection
                post.releaseConnection();
                return null;
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), post::releaseConnection);
            }
        }
        throw new RuntimeException("Unable to retrieve suggested actions.");
    }
}
