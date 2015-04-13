package org.talend.dataprep.api.service.command;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.exception.Exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class DataSetGetMetadata extends HystrixCommand<DataSetMetadata> {

    private final HttpClient client;

    private final String dataSetId;

    private final String contentServiceUrl;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private DataSetGetMetadata(HttpClient client, String contentServiceUrl, String dataSetId) {
        super(PreparationAPI.DATASET_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.client = client;
        this.dataSetId = dataSetId;
    }

    @Override
    protected DataSetMetadata run() throws Exception {
        HttpGet metadataRetrieval = new HttpGet(contentServiceUrl + "/" + dataSetId + "/metadata");
        HttpResponse response = client.execute(metadataRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            if (statusCode >= 200) {
                if (statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_ACCEPTED) {
                    // Immediately release connection
                    return null;
                } else if (statusCode == HttpStatus.SC_OK) {
                    ObjectMapper mapper = builder.build();
                    return mapper.reader(DataSetMetadata.class).readValue(response.getEntity().getContent());
                }
            }
            throw Exceptions.User(APIMessages.UNABLE_TO_RETRIEVE_DATASET_METADATA);
        } finally {
            metadataRetrieval.releaseConnection();
        }
    }
}
