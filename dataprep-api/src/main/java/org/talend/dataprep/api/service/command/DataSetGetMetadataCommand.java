package org.talend.dataprep.api.service.command;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.json.DataSetMetadataModule;
import org.talend.dataprep.api.service.DataPreparationAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

public class DataSetGetMetadataCommand extends HystrixCommand<DataSetMetadata> {

    private final HttpClient client;

    private final String dataSetId;

    private final String contentServiceUrl;

    public DataSetGetMetadataCommand(HttpClient client, String contentServiceUrl, String dataSetId) {
        super(DataPreparationAPI.DATASET_GROUP);
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
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(DataSetMetadataModule.DEFAULT);
                    return mapper.reader(DataSetMetadata.class).readValue(response.getEntity().getContent());
                }
            }
            throw new RuntimeException("Unable to retrieve metadata.");
        } finally {
            metadataRetrieval.releaseConnection();
        }
    }
}
