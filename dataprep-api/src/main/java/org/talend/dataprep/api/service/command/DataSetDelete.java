package org.talend.dataprep.api.service.command;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.exception.Exceptions;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class DataSetDelete extends HystrixCommand<Void> {

    private final String contentServiceUrl;

    private final HttpClient client;

    private final String dataSetId;

    private DataSetDelete(HttpClient client, String contentServiceUrl, String dataSetId) {
        super(PreparationAPI.TRANSFORM_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.client = client;
        this.dataSetId = dataSetId;
    }

    @Override
    protected Void run() throws Exception {
        HttpDelete contentRetrieval = new HttpDelete(contentServiceUrl + "/" + dataSetId);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw Exceptions.User(APIMessages.UNABLE_TO_DELETE_DATASET, dataSetId);
    }
}
