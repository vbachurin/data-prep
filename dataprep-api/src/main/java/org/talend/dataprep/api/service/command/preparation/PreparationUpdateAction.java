package org.talend.dataprep.api.service.command.preparation;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.json.JsonErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("request")
public class PreparationUpdateAction extends PreparationCommand<Void> {

    private final String stepId;

    private final AppendStep updatedStep;

    private final String preparationId;

    private PreparationUpdateAction(final HttpClient client, final String preparationId, final String stepId, final AppendStep updatedStep) {
        super(APIService.PREPARATION_GROUP, client);
        this.stepId = stepId;
        this.updatedStep = updatedStep;
        this.preparationId = preparationId;
    }

    @Override
    protected Void run() throws Exception {
        final StepDiff diff = getDiffMetadata();
        updatedStep.setDiff(diff);

        final HttpPut actionAppend = new HttpPut(preparationServiceUrl + "/preparations/" + preparationId + "/actions/" + stepId); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            final String stepAsString = builder.build().writeValueAsString(updatedStep);
            final InputStream stepInputStream = new ByteArrayInputStream(stepAsString.getBytes());

            actionAppend.setHeader(new BasicHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));
            actionAppend.setEntity(new InputStreamEntity(stepInputStream));
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

    /**
     * Get the diff metadata introduced by the step to append (ex : the created columns)
     */
    private StepDiff getDiffMetadata() throws IOException {
        final Preparation preparation = getPreparation(preparationId);
        final int stepIndex = preparation.getSteps().indexOf(stepId);
        final String parentStepId = preparation.getSteps().get(stepIndex - 1);

        return getDiffMetadata(preparationId, parentStepId, updatedStep.getActions());
    }
}
