package org.talend.dataprep.api.service.command.preparation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.json.JsonErrorCode;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("request")
public class PreparationDeleteAction extends PreparationCommand<Void> {

    private final String stepId;

    private final String preparationId;

    private PreparationDeleteAction(final HttpClient client, final String preparationId, final String stepId) {
        super(APIService.PREPARATION_GROUP, client);
        this.stepId = stepId;
        this.preparationId = preparationId;
    }

    @Override
    protected Void run() throws Exception {
        final HttpDelete deleteAction = new HttpDelete(preparationServiceUrl + "/preparations/" + preparationId + "/actions/" + stepId); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            final HttpResponse response = client.execute(deleteAction);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                final ObjectMapper build = builder.build();
                final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
                errorCode.setHttpStatus(statusCode);
                throw new TDPException(errorCode);
            }

            return null;

        } finally {
            deleteAction.releaseConnection();
        }
    }
}
