package org.talend.dataprep.api.service.command;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class DataSetGetMetadata extends HystrixCommand<DataSetMetadata> {

    private static final int MAX_RETRY = 5;

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
                        throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_METADATA);
                    }
                    // Pause before retry
                    final int pauseTime = 100 * retryCount;
                    LOGGER.info("Data set #{} metadata is not ready, pausing for {} ms.", dataSetId, pauseTime);
                    try {
                        Thread.sleep(pauseTime);
                    } catch (InterruptedException e) {
                        throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_METADATA, e);
                    }
                    return handleResponse(client.execute(metadataRetrieval));
                } else if (statusCode == HttpStatus.SC_OK) {
                    ObjectMapper mapper = builder.build();
                    return mapper.reader(DataSetMetadata.class).readValue(response.getEntity().getContent());
                }
            }
            throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_METADATA);
        } finally {
            metadataRetrieval.releaseConnection();
        }
    }
}
