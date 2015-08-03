package org.talend.dataprep.api.service.command.preparation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.json.JsonErrorCode;

import java.io.InputStream;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.talend.dataprep.api.APIErrorCodes.UNABLE_TO_UPDATE_ACTION_IN_PREPARATION;

@Component
@Scope("request")
public class PreparationUpdateAction extends DataPrepCommand<Void> {

    private final String stepId;

    private final InputStream actions;

    private final String id;

    private PreparationUpdateAction(HttpClient client, String id, String stepId, InputStream actions) {
        super(APIService.PREPARATION_GROUP, client);
        this.stepId = stepId;
        this.actions = actions;
        this.id = id;
    }

    @Override
    protected Void run() throws Exception {
        HttpPut actionAppend = new HttpPut(preparationServiceUrl + "/preparations/" + id + "/actions/" + stepId); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            actionAppend.setHeader(new BasicHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));
            actionAppend.setEntity(new InputStreamEntity(actions));

            final HttpResponse response = client.execute(actionAppend);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return null;
            } else if (statusCode >= 400) {
                final ObjectMapper build = builder.build();
                final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
                errorCode.setHttpStatus(statusCode);
                throw new TDPException(errorCode);
            }

            throw new TDPException(UNABLE_TO_UPDATE_ACTION_IN_PREPARATION, TDPExceptionContext.build().put("id", id));
        } finally {
            actionAppend.releaseConnection();
        }
    }
}
