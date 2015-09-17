package org.talend.dataprep.api.service.command.preparation;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.talend.dataprep.api.APIErrorCodes.UNABLE_TO_UPDATE_PREPARATION;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.daikon.exception.ExceptionContext;

@Component
@Scope("request")
public class PreparationUpdate extends DataPrepCommand<String> {

    private final String id;

    private final Preparation preparation;

    private PreparationUpdate(HttpClient client, String id, Preparation preparation) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
        this.preparation = preparation;
    }

    @Override
    protected String run() throws Exception {
        final byte[] preparationJSONValue = getJsonWriter().writeValueAsBytes(preparation);
        final HttpPut preparationCreation = new HttpPut(preparationServiceUrl + "/preparations/" + id);
        try {
            preparationCreation.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
            preparationCreation.setEntity(new ByteArrayEntity(preparationJSONValue));

            final HttpResponse response = client.execute(preparationCreation);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return IOUtils.toString(response.getEntity().getContent());
            }
            throw new TDPException(UNABLE_TO_UPDATE_PREPARATION, ExceptionContext.build().put("id", id));
        } finally {
            preparationCreation.releaseConnection();
        }
    }
}
