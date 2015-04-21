package org.talend.dataprep.api.service.command;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.exception.Exceptions;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationDelete extends HystrixCommand<String> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final String id;

    private PreparationDelete(HttpClient client, String preparationServiceURL, String id) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceURL;
        this.id = id;
    }

    @Override
    protected String run() throws Exception {
        HttpDelete deletePreparation = new HttpDelete(preparationServiceUrl + "/preparations/" + id); //$NON-NLS-1$
        try {
            HttpResponse response = client.execute(deletePreparation);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200) {
                return null;
            }
            throw Exceptions.User(APIMessages.UNABLE_TO_DELETE_PREPARATION);
        } finally {
            deletePreparation.releaseConnection();
        }
    }
}
