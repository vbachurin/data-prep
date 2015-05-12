package org.talend.dataprep.api.service.command.preparation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.api.PreviewDiffInput;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;

import com.fasterxml.jackson.databind.JsonNode;
import org.talend.dataprep.api.service.command.common.PreparationCommand;

@Component
@Scope("request")
public class PreviewDiff extends PreparationCommand<InputStream> {

    private final PreviewDiffInput input;

    public PreviewDiff(final HttpClient client, final PreviewDiffInput input) {
        super(APIService.PREPARATION_GROUP, client);
        this.input = input;
    }

    @Override
    protected InputStream run() throws Exception {

        //get preparation details
        final JsonNode preparationDetails = getPreparationDetails(input.getPreparationId());
        final String dataSetId = preparationDetails.get("dataSetId").textValue();

        //extract actions by steps in chronological order, until defined last active step (from input)
        final List<String> currentStepsIds = getActionsStepIds(preparationDetails, input.getCurrentStepId());
        final Map<String, Action> currentActions = getActions(preparationDetails, currentStepsIds);

        //extract actions without disabled steps
        final List<String> previewStepsIds = getActionsStepIds(preparationDetails, input.getPreviewStepId());
        final Map<String, Action> previewActions = getActions(preparationDetails, previewStepsIds);

        //serialize and base 64 encode the 2 actions list
        final String currentEncodedActions = serializeAndEncode(currentActions);
        final String previewEncodedActions = serializeAndEncode(previewActions);

        //get dataset content
        final InputStream content = getDatasetContent(dataSetId);

        //get usable tdpIds
        final String encodedTdpIds = serializeAndEncode(input.getTdpIds());

        //call transformation preview with content and the 2 transformations
        return previewTransformation(content, currentEncodedActions, previewEncodedActions, encodedTdpIds);
    }

    /**
     * Call the transformation service to compute preview between old and new transformation
     * @param content - the dataset content
     * @param oldEncodedActions - the old actions
     * @param newEncodedActions - the preview actions
     * @param encodedTdpIds - the TDP ids
     * @throws java.io.IOException
     */
    private InputStream previewTransformation(final InputStream content, final String oldEncodedActions, final String newEncodedActions,final String encodedTdpIds) throws IOException {
        final String uri = this.transformationServiceUrl + "/transform/preview?oldActions=" + oldEncodedActions + "&newActions=" + newEncodedActions + "&indexes=" + encodedTdpIds;
        HttpPost transformationCall = new HttpPost(uri);

        transformationCall.setEntity(new InputStreamEntity(content));
        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }
}
