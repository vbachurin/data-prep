package org.talend.dataprep.api.service.command;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.talend.dataprep.api.service.APIService;

import com.netflix.hystrix.HystrixCommand;

public class PreparationCreate extends HystrixCommand<String> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final String dataSetId;

    public PreparationCreate(HttpClient client, String preparationServiceUrl, String dataSetId) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceUrl;
        this.dataSetId = dataSetId;
    }

    @Override
    protected String getFallback() {
        return StringUtils.EMPTY;
    }

    @Override
    protected String run() throws Exception {
        HttpPut contentRetrieval = new HttpPut(preparationServiceUrl + "/preparations");
        contentRetrieval.setEntity(new StringEntity(dataSetId));
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            if (statusCode == 200) {
                return IOUtils.toString(response.getEntity().getContent());
            }
            throw new RuntimeException("Unable to retrieve preparation list.");
        } finally {
            contentRetrieval.releaseConnection();
        }
    }
}
