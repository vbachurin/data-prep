package org.talend.dataprep.api.service.command;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.exception.Exceptions;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class DatasetCertification extends HystrixCommand<Void> {

    private final String contentServiceUrl;

    private final HttpClient client;

    private final String dataSetId;

    private final boolean ask;

    private DatasetCertification(HttpClient client, String contentServiceUrl, String dataSetId, boolean ask) {
        super(PreparationAPI.TRANSFORM_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.client = client;
        this.dataSetId = dataSetId;
        this.ask = ask;
    }

    @Override
    protected Void getFallback() {
        return null;
    }

    @Override
    protected Void run() throws Exception {
        HttpPut contentRetrieval = new HttpPut(contentServiceUrl + "/" + dataSetId + (ask ? "/askcertification" : "/certify"));
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw Exceptions.User(APIMessages.UNABLE_TO_DELETE_DATASET, dataSetId);
    }
}
