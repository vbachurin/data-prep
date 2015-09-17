package org.talend.dataprep.api.service.command.preparation;

import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.http.HttpStatus;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.PreparationCommand;

/**
 * Base class for preview commands.
 */
public abstract class PreviewAbstract extends PreparationCommand<InputStream> {

    private String oldEncodedActions;

    private String newEncodedActions;

    private InputStream content;

    private String encodedTdpIds;

    /**
     * Default constructor.
     * 
     * @param client the http client to use.
     */
    public PreviewAbstract(final HttpClient client) {
        super(APIService.PREPARATION_GROUP, client);
    }

    @Override
    protected InputStream run() throws Exception {
        if (oldEncodedActions == null || newEncodedActions == null || content == null || encodedTdpIds == null) {
            throw new IllegalStateException("Missing context.");
        }
        execute(() -> {
            final String uri = this.transformationServiceUrl + "/transform/preview";
            HttpPost transformationCall = new HttpPost(uri);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("oldActions", new StringBody(oldEncodedActions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                    .addPart("newActions", new StringBody(newEncodedActions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                    .addPart("indexes", new StringBody(encodedTdpIds, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                    .addPart("content", new InputStreamBody(content, ContentType.APPLICATION_JSON)) //$NON-NLS-1$
                    .build();
            transformationCall.setEntity(reqEntity);
            return transformationCall;
        });
        on(HttpStatus.OK).then(pipeStream());
        return super.run();
    }

    protected void setContext(String oldEncodedActions, String newEncodedActions, InputStream content, String encodedTdpIds) {
        this.oldEncodedActions = oldEncodedActions;
        this.newEncodedActions = newEncodedActions;
        this.content = content;
        this.encodedTdpIds = encodedTdpIds;
    }
}
