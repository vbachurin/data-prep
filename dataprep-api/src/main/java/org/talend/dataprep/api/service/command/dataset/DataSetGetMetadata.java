package org.talend.dataprep.api.service.command.dataset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("request")
public class DataSetGetMetadata extends DataPrepCommand<DataSetMetadata> {

    private final String dataSetId;

    private DataSetGetMetadata(HttpClient client, String dataSetId) {
        super(PreparationAPI.DATASET_GROUP, client);
        this.dataSetId = dataSetId;
    }

    @Override
    protected DataSetMetadata run() throws Exception {
        final HttpGet metadataRetrieval = new HttpGet(datasetServiceUrl + "/datasets/" + dataSetId + "/metadata");
        final HttpResponse response = client.execute(metadataRetrieval);
        return handleResponse(response, metadataRetrieval);
    }

    private DataSetMetadata handleResponse(final HttpResponse response, final HttpGet metadataRetrieval)
            throws java.io.IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            if (statusCode >= HttpStatus.SC_OK) {
                if (statusCode == HttpStatus.SC_NO_CONTENT) {
                    // Immediately release connection
                    return null;
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
