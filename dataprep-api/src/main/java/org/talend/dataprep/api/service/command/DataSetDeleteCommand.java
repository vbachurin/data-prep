package org.talend.dataprep.api.service.command;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.talend.dataprep.api.service.DataPreparationAPI;

import com.netflix.hystrix.HystrixCommand;

public class DataSetDeleteCommand extends HystrixCommand<Void> {

    private final String contentServiceUrl;

    private final HttpClient client;

    private final String dataSetId;

    public DataSetDeleteCommand(HttpClient client, String contentServiceUrl, String dataSetId) {
        super(DataPreparationAPI.TRANSFORM_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.client = client;
        this.dataSetId = dataSetId;
    }

    @Override
    protected Void getFallback() {
        return null;
    }

    @Override
    protected Void run() throws Exception {
        HttpDelete contentRetrieval = new HttpDelete(contentServiceUrl + "/" + dataSetId);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw new RuntimeException("Unable to delete dataset #" + dataSetId + ".");
    }
}
