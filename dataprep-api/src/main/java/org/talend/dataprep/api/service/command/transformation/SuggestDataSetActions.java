package org.talend.dataprep.api.service.command.transformation;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.ChainedCommand;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class SuggestDataSetActions extends ChainedCommand<InputStream, DataSetMetadata> {

    private SuggestDataSetActions(HttpClient client, HystrixCommand<DataSetMetadata> retrieveMetadata) {
        super(PreparationAPI.TRANSFORM_GROUP, client, retrieveMetadata);
    }

    @Override
    protected InputStream run() throws Exception {
        HttpPost post = new HttpPost(transformationServiceUrl + "/suggest/dataset");
        post.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        DataSetMetadata metadata = getInput();
        ObjectMapper objectMapper = builder.build();
        byte[] dataSetMetadataJSON  = objectMapper.writer().writeValueAsBytes(metadata);
        post.setEntity(new ByteArrayEntity(dataSetMetadataJSON));
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
        throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_SUGGESTED_ACTIONS);
    }
}
