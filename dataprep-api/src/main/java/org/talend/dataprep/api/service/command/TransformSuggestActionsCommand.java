package org.talend.dataprep.api.service.command;

import com.netflix.hystrix.HystrixCommand;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.service.DataPreparationAPI;

import java.io.IOException;
import java.io.InputStream;

public class TransformSuggestActionsCommand extends ChainedCommand<InputStream, DataSetMetadata> {

    private final HttpClient client;

    public TransformSuggestActionsCommand(HystrixCommand<DataSetMetadata> retrieveMetadata, String columnName, HttpClient client) {
        super(DataPreparationAPI.TRANSFORM_GROUP, retrieveMetadata);
        this.client = client;
    }

    public TransformSuggestActionsCommand(HystrixCommand<DataSetMetadata> retrieveMetadata, HttpClient client) {
        super(DataPreparationAPI.TRANSFORM_GROUP, retrieveMetadata);
        this.client = client;
    }

    @Override
    protected InputStream run() throws Exception {
        try {
            client.execute(new HttpHost(""), null);
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Unable to retrieve suggested actions from transformation service.", e);
        }
    }
}
