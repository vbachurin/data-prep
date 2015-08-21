package org.talend.dataprep.api.service.command.preparation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.cache.ContentCache;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.json.JsonErrorCode;

import java.io.InputStream;

import static org.talend.dataprep.api.APIErrorCodes.UNABLE_TO_ACTIONS_TO_PREPARATION;

@Component
@Scope("request")
public class PreparationAddAction extends DataPrepCommand<Void> {

    @Autowired
    private ContentCache contentCache;

    private final InputStream actions;

    private final String id;

    private PreparationAddAction(HttpClient client, String id, InputStream actions) {
        super(APIService.PREPARATION_GROUP, client);
        this.actions = actions;
        this.id = id;
    }

    @Override
    protected Void run() throws Exception {

        final HttpPost actionAppend = new HttpPost(preparationServiceUrl + "/preparations/" + id + "/actions"); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            actionAppend.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)); //$NON-NLS-1$
            actionAppend.setEntity(new InputStreamEntity(actions));
            final HttpResponse response = client.execute(actionAppend);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 400) {
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
