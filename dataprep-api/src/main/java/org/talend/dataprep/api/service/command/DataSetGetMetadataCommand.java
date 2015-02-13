package org.talend.dataprep.api.service.command;

import com.netflix.hystrix.HystrixCommand;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.service.DataPreparationAPI;

public class DataSetGetMetadataCommand extends HystrixCommand<DataSetMetadata> {
    private final HttpClient client;

    public DataSetGetMetadataCommand(HttpClient client) {
        super(DataPreparationAPI.DATASET_GROUP);
        this.client = client;
    }

    @Override
    protected DataSetMetadata run() throws Exception {
        try {
            client.execute(new HttpHost(""), null);
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve metadata information from content service.", e);
        }
    }
}
