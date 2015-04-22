package org.talend.dataprep.api.service.command;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.exception.Exceptions;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationAddAction extends HystrixCommand<Void> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final InputStream actions;

    private final String id;

    private PreparationAddAction(HttpClient client, String preparationServiceUrl, String id, InputStream actions) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceUrl;
        this.actions = actions;
        this.id = id;
    }

    @Override
    protected Void getFallback() {
        return null;
    }

    @Override
    protected Void run() throws Exception {
        HttpPost actionAppend = new HttpPost(preparationServiceUrl + "/preparations/" + id + "/actions"); //$NON-NLS-1$ //$NON-NLS-2$
        actionAppend.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)); //$NON-NLS-1$
        actionAppend.setEntity(new InputStreamEntity(actions));
        HttpResponse response = client.execute(actionAppend);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw Exceptions.User(APIMessages.UNABLE_TO_ACTIONS_TO_PREPARATION, id);
    }
}
