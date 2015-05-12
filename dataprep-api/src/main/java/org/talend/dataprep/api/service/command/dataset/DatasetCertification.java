package org.talend.dataprep.api.service.command.dataset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class DatasetCertification extends DataPrepCommand<Void> {

    private final String dataSetId;


    private DatasetCertification(HttpClient client, String dataSetId) {
        super(PreparationAPI.TRANSFORM_GROUP, client);
        this.dataSetId = dataSetId;
    }

    @Override
    protected Void getFallback() {
        return null;
    }

    @Override
    protected Void run() throws Exception {
        HttpPut contentRetrieval = new HttpPut(datasetServiceUrl + "/datasets/" + dataSetId + "/processcertification");
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_CERTIFY_DATASET);
    }
}
