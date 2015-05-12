package org.talend.dataprep.api.service.command.preparation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationDelete extends DataPrepCommand<String> {

    private final String id;

    private PreparationDelete(HttpClient client, String id) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
    }

    @Override
    protected String run() throws Exception {
        final HttpDelete deletePreparation = new HttpDelete(preparationServiceUrl + "/preparations/" + id); //$NON-NLS-1$
        try {
            final HttpResponse response = client.execute(deletePreparation);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200) {
                return null;
            }
            throw new TDPException(APIErrorCodes.UNABLE_TO_DELETE_PREPARATION);
        } finally {
            deletePreparation.releaseConnection();
        }
    }
}
