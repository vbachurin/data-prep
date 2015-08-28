package org.talend.dataprep.api.service.command.preparation;

import static org.apache.http.HttpStatus.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

@Component
@Scope("request")
public class PreparationGet extends DataPrepCommand<InputStream> {

    private final String id;

    private PreparationGet(HttpClient client, String id) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
    }

    @Override
    protected InputStream run() throws Exception {
        final HttpGet contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id);
        final HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();

        switch (statusCode) {
        case SC_NO_CONTENT:
        case SC_ACCEPTED:
            contentRetrieval.releaseConnection();
            return new ByteArrayInputStream(new byte[0]);
        case SC_OK:
            return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
        default:
            contentRetrieval.releaseConnection();
            throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_PREPARATION_LIST);
        }
    }
}
