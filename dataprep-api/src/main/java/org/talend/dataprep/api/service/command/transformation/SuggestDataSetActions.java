package org.talend.dataprep.api.service.command.transformation;

import static org.talend.dataprep.api.service.command.common.Defaults.asNull;
import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.ChainedCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class SuggestDataSetActions extends ChainedCommand<InputStream, DataSetMetadata> {

    private SuggestDataSetActions(HttpClient client, HystrixCommand<DataSetMetadata> retrieveMetadata) {
        super(PreparationAPI.TRANSFORM_GROUP, client, retrieveMetadata);
        execute(this::onExecute);
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_SUGGESTED_ACTIONS, e));
        on(HttpStatus.NO_CONTENT, HttpStatus.ACCEPTED).then(asNull());
        on(HttpStatus.OK).then(pipeStream());
    }

    private HttpRequestBase onExecute() {
        try {
            final HttpPost post = new HttpPost(transformationServiceUrl + "/suggest/dataset");
            post.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
            DataSetMetadata metadata = getInput();
            ObjectMapper objectMapper = builder.build();
            byte[] dataSetMetadataJSON = objectMapper.writer().writeValueAsBytes(metadata);
            post.setEntity(new ByteArrayEntity(dataSetMetadataJSON));
            return post;
        } catch (JsonProcessingException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
