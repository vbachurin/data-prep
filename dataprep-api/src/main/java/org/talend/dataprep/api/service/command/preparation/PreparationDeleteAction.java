package org.talend.dataprep.api.service.command.preparation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.talend.dataprep.api.APIErrorCodes.UNABLE_TO_DELETE_ACTION_IN_PREPARATION;

@Component
@Scope("request")
public class PreparationDeleteAction extends DataPrepCommand<Void> {

    private final String stepId;

    private final String preparationId;

    private PreparationDeleteAction(final HttpClient client, final String preparationId, final String stepId) {
        super(APIService.PREPARATION_GROUP, client);
        this.stepId = stepId;
        this.preparationId = preparationId;
    }

    @Override
    protected Void run() throws Exception {
        final HttpDelete actionAppend = new HttpDelete(preparationServiceUrl + "/preparations/" + preparationId + "/actions/" + stepId); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            final HttpResponse response = client.execute(actionAppend);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return null;
            }
            throw new TDPException(UNABLE_TO_DELETE_ACTION_IN_PREPARATION, TDPExceptionContext.build()
                    .put("id", preparationId)
                    .put("stepId", stepId));
        } finally {
            actionAppend.releaseConnection();
        }
    }
}
