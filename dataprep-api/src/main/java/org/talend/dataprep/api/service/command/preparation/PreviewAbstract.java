package org.talend.dataprep.api.service.command.preparation;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.PreparationCommand;

/**
 * Base class for preview commands.
 */
public abstract class PreviewAbstract extends PreparationCommand<InputStream> {

    /**
     * Default constructor.
     * 
     * @param client the http client to use.
     */
    public PreviewAbstract(final HttpClient client) {
        super(APIService.PREPARATION_GROUP, client);
    }

    /**
     * Call the transformation service to compute preview between old and new transformation
     *
     * @param content - the dataset content
     * @param oldEncodedActions - the old actions
     * @param newEncodedActions - the preview actions
     * @param encodedTdpIds - the TDP ids
     * @throws java.io.IOException
     */
    protected InputStream previewTransformation(final InputStream content, final String oldEncodedActions,
            final String newEncodedActions, final String encodedTdpIds) throws IOException {

        final String uri = this.transformationServiceUrl + "/transform/preview";
        HttpPost transformationCall = new HttpPost(uri);

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("oldActions", new StringBody(oldEncodedActions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("newActions", new StringBody(newEncodedActions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("indexes", new StringBody(encodedTdpIds, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("content", new InputStreamBody(content, ContentType.APPLICATION_JSON)) //$NON-NLS-1$
                .build();
        transformationCall.setEntity(reqEntity);

        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }
}
