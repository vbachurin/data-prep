package org.talend.dataprep.api.service.command.preparation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.api.PreviewUpdateInput;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.PreparationCommand;

@Component
@Scope("request")
public class PreviewUpdate extends PreparationCommand<InputStream> {

    private final PreviewUpdateInput input;

    public PreviewUpdate(final HttpClient client, final PreviewUpdateInput input) {
        super(APIService.PREPARATION_GROUP, client);
        this.input = input;
    }

    @Override
    protected InputStream run() throws Exception {

        // get preparation details
        final Preparation preparation = getPreparation(input.getPreparationId());
        final String dataSetId = preparation.getDataSetId();

        // extract actions by steps in chronological order, until defined last active step (from input)
        Map<String, Action> originalActions = new LinkedHashMap<>();
        final List<String> steps = preparation.getSteps();
        final Iterator<Action> actions = getPreparationActions(preparation, input.getCurrentStepId()).iterator();
        steps.stream().filter(step -> actions.hasNext()).forEach(step -> originalActions.put(step, actions.next()));

        // modify actions to include the update
        final Map<String, Action> modifiedActions = new LinkedHashMap<>(originalActions);
        if (modifiedActions.get(input.getUpdateStepId()) != null) {
            modifiedActions.put(input.getUpdateStepId(), input.getAction());
        }

        // serialize and base 64 encode the 2 actions list
        final String oldEncodedActions = serializeActions(originalActions.values());
        final String newEncodedActions = serializeActions(modifiedActions.values());

        // get dataset content
        final InputStream content = getDatasetContent(dataSetId);

        // get usable tdpIds
        final String encodedTdpIds = serializeIds(input.getTdpIds());

        // call transformation preview with content and the 2 transformations
        return previewTransformation(content, oldEncodedActions, newEncodedActions, encodedTdpIds);
    }

    /**
     * Call the transformation service to compute preview between old and new transformation
     * 
     * @param content - the dataset content
     * @param oldEncodedActions - the old actions
     * @param newEncodedActions - the preview actions
     * @param encodedTdpIds - the TDP ids
     * @throws IOException
     */
    private InputStream previewTransformation(final InputStream content, final String oldEncodedActions,
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
