package org.talend.dataprep.api.service.command.preparation;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
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
        final Preparation preparationDetails = getPreparationDetails(input.getPreparationId());
        final String dataSetId = preparationDetails.getDataSetId();
        // extract actions by steps in chronological order, until defined last active step (from input)
        Map<String, Action> originalActions = new LinkedHashMap<>();
        final List<String> steps = preparationDetails.getSteps();
        final Iterator<Action> actions = getPreparationActions(preparationDetails, input.getCurrentStepId()).iterator();
        for (String step : steps) {
            originalActions.put(step, actions.next());
        }
        // modify actions to include the update
        final Map<String, Action> modifiedActions = new LinkedHashMap<>(originalActions);
        if (modifiedActions.get(input.getUpdateStepId()) != null) {
            modifiedActions.put(input.getUpdateStepId(), input.getAction());
        }
        // serialize and base 64 encode the 2 actions list
        final String oldEncodedActions = serialize(new ArrayList<>(originalActions.values()));
        final String newEncodedActions = serialize(new ArrayList<>(modifiedActions.values()));
        // get dataset content
        final InputStream content = getDatasetContent(dataSetId);
        // get usable tdpIds
        final String encodedTdpIds = serializeAndEncode(input.getTdpIds());
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
        final String uri = this.transformationServiceUrl + "/transform/preview?oldActions=" + oldEncodedActions + "&newActions="
                + newEncodedActions + "&indexes=" + encodedTdpIds;
        HttpPost transformationCall = new HttpPost(uri);

        transformationCall.setEntity(new InputStreamEntity(content));
        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }
}
