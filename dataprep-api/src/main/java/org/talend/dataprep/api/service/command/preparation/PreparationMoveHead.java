package org.talend.dataprep.api.service.command.preparation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.daikon.exception.json.JsonErrorCode;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("request")
public class PreparationMoveHead extends DataPrepCommand<Void> {

    private final String headId;

    private final String preparationId;

    private PreparationMoveHead(final HttpClient client, final String preparationId, final String headId) {
        super(APIService.PREPARATION_GROUP, client);
        this.headId = headId;
        this.preparationId = preparationId;
    }

    @Override
    protected Void run() throws Exception {
        final HttpPut actionAppend = new HttpPut(preparationServiceUrl + "/preparations/" + preparationId + "/head/" + headId); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            final HttpResponse response = client.execute(actionAppend);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                final ObjectMapper build = builder.build();
                final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
                errorCode.setHttpStatus(statusCode);
                throw new TDPException(errorCode);
            }

            return null;

        } finally {
            actionAppend.releaseConnection();
        }
    }
}
