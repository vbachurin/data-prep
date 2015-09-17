package org.talend.dataprep.api.service.command.transformation;

import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.GenericCommand;

@Component
@Scope("request")
public class Transform extends GenericCommand<InputStream> {

    private Transform(HttpClient client, InputStream content, String actions) {
        super(APIService.TRANSFORM_GROUP, client);
        execute(() -> {
            String uri = transformationServiceUrl + "/transform/JSON"; //$NON-NLS-1$
            HttpPost transformationCall = new HttpPost(uri);
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("actions", new StringBody(actions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$
                    .addPart("content", new InputStreamBody(content, ContentType.APPLICATION_JSON)) //$NON-NLS-1$
                    .build();
            transformationCall.setEntity(reqEntity);
            return transformationCall;
        });
        on(HttpStatus.OK).then(pipeStream());
    }

}
