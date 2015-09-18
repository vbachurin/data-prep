package org.talend.dataprep.api.service.command.preparation;

import java.io.InputStream;
import java.util.*;

import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.api.PreviewDiffInput;

@Component
@Scope("request")
public class PreviewDiff extends PreviewAbstract {

    /** The all the preview parameters. */
    private final PreviewDiffInput input;

    public PreviewDiff(final HttpClient client, final PreviewDiffInput input) {
        super(client);
        this.input = input;
    }

    @Override
    protected InputStream run() throws Exception {

        // get preparation details
        final Preparation preparation = getPreparation(input.getPreparationId());
        final String dataSetId = preparation.getDataSetId();

        // get steps from first operation to head
        final List<String> steps = preparation.getSteps();
        steps.remove(0);
        
        // extract actions by steps in chronological order, until defined last active step (from input)
        final Map<String, Action> originalActions = new LinkedHashMap<>();
        final Iterator<Action> actions = getPreparationActions(preparation, input.getCurrentStepId()).iterator();
        steps.stream().filter(step -> actions.hasNext()).forEach(step -> originalActions.put(step, actions.next()));

        // extract actions by steps in chronological order, until preview step (from input)
        final Map<String, Action> previewActions = new LinkedHashMap<>();
        final Iterator<Action> previewActionsIterator = getPreparationActions(preparation, input.getPreviewStepId()).iterator();
        steps.stream().filter(step -> previewActionsIterator.hasNext()).forEach(step -> previewActions.put(step, previewActionsIterator.next()));

        // serialize the 2 actions list
        final String oldEncodedActions = serializeActions(new ArrayList<>(originalActions.values()));
        final String newEncodedActions = serializeActions(new ArrayList<>(previewActions.values()));

        // get dataset content
        final InputStream content = getDatasetContent(dataSetId);
        // get usable tdpIds
        final String encodedTdpIds = serializeIds(input.getTdpIds());

        // call transformation preview with content and the 2 transformations
        return previewTransformation(content, oldEncodedActions, newEncodedActions, encodedTdpIds);
    }


}
