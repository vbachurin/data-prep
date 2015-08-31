package org.talend.dataprep.api.service.command.preparation;

import static org.talend.dataprep.api.APIErrorCodes.UNABLE_TO_DELETE_ACTION_IN_PREPARATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.json.JsonErrorCode;

@Component
@Scope("request")
public class PreparationDeleteAction extends DataPrepCommand<Void> {

    private final String stepId;

    private final String preparationId;

    private final boolean single;

    private PreparationDeleteAction(final HttpClient client, final String preparationId, final String stepId, final boolean single) {
        super(APIService.PREPARATION_GROUP, client);
        this.stepId = stepId;
        this.preparationId = preparationId;
        this.single = single;
    }

    @Override
    protected Void run() throws Exception {
        final HttpDelete actionAppend = new HttpDelete(preparationServiceUrl + "/preparations/" + preparationId + "/actions/" + stepId + "?single=" + single); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
