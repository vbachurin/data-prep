package org.talend.dataprep.api.service.command;

import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${http.retry.pause}")
    public int PAUSE;

    @Value("${http.retry.max_retry}")
    private int MAX_RETRY;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetGetMetadata.class);

    private final HttpClient client;

    private final String dataSetId;

    private final HttpGet metadataRetrieval;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private int retryCount = 0;

    private DataSetGetMetadata(HttpClient client, String contentServiceUrl, String dataSetId) {
        super(PreparationAPI.DATASET_GROUP);
        this.client = client;
        this.dataSetId = dataSetId;
        metadataRetrieval = new HttpGet(contentServiceUrl + "/" + dataSetId + "/metadata");
    }

    @Override
    protected DataSetMetadata run() throws Exception {
        HttpResponse response = client.execute(metadataRetrieval);
        return handleResponse(response);
    }

    private DataSetMetadata handleResponse(HttpResponse response) throws java.io.IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            if (statusCode >= HttpStatus.SC_OK) {
                if (statusCode == HttpStatus.SC_NO_CONTENT) {
                    // Immediately release connection
                    return null;
                } else if (statusCode == HttpStatus.SC_ACCEPTED) {
                    // Data set exists, but content isn't yet analyzed, retry request
                    retryCount++;
                    if (retryCount > MAX_RETRY) {
                        LOGGER.error("Failed to retrieve data set metadata after {} tries.", retryCount);
                        throw Exceptions.User(APIMessages.UNABLE_TO_RETRIEVE_DATASET_METADATA);
                    }
                    // Pause before retry
                    final int pauseTime = PAUSE * retryCount;
                    LOGGER.debug("Data set #{} metadata is not ready, pausing for {} ms.", dataSetId, pauseTime);
                    try {
                        TimeUnit.MILLISECONDS.sleep(pauseTime);
                    } catch (InterruptedException e) {
                        throw Exceptions.User(APIMessages.UNABLE_TO_RETRIEVE_DATASET_METADATA, e);
                    }
                    return handleResponse(client.execute(metadataRetrieval));
                } else if (statusCode == HttpStatus.SC_OK) {
                    retryCount = 0;
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
