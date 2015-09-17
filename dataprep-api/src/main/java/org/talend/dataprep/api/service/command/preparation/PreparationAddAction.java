package org.talend.dataprep.api.service.command.preparation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.json.JsonErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("request")
public class PreparationAddAction extends PreparationCommand<Void> {

    private final AppendStep step;

    private final String preparationId;

    private PreparationAddAction(final HttpClient client, final String preparationId, final AppendStep step) {
        super(APIService.PREPARATION_GROUP, client);
        this.step = step;
        this.preparationId = preparationId;
    }

    @Override
    protected Void run() throws Exception {
        final StepDiff diff = getDiffMetadata(preparationId, "head", step.getActions());
        step.setDiff(diff);

        final HttpPost actionAppend = new HttpPost(preparationServiceUrl + "/preparations/" + preparationId + "/actions"); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            final String stepAsString = builder.build().writeValueAsString(step);
            final InputStream stepInputStream = new ByteArrayInputStream(stepAsString.getBytes());

            actionAppend.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)); //$NON-NLS-1$
            actionAppend.setEntity(new InputStreamEntity(stepInputStream));
            final HttpResponse response = client.execute(actionAppend);
            int statusCode = response.getStatusLine().getStatusCode();

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
