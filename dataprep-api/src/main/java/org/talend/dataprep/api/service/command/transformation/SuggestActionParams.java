package org.talend.dataprep.api.service.command.transformation;

import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.command.common.ChainedCommand;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class SuggestActionParams extends ChainedCommand<InputStream, InputStream> {

    private SuggestActionParams(final HttpClient client, final HystrixCommand<InputStream> content, final String action,
            final String columnId) {
        super(client, content);
        execute(() -> {
            final String uri = transformationServiceUrl + "/transform/suggest/" + action + "/params?columnId=" + columnId;
            final HttpPost getParametersCall = new HttpPost(uri);
            final InputStreamEntity entity = new InputStreamEntity(getInput());
            getParametersCall.setEntity(entity);
            return getParametersCall;
        });
        on(HttpStatus.OK).then(pipeStream());
    }

}
