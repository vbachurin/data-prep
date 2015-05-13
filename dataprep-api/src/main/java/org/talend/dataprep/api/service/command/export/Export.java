package org.talend.dataprep.api.service.command.export;

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
import org.talend.dataprep.api.service.api.ExportInput;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;

import com.fasterxml.jackson.databind.JsonNode;
import org.talend.dataprep.api.service.command.common.PreparationCommand;

@Component
@Scope("request")
public class Export extends PreparationCommand<InputStream> {

    private final ExportInput input;

    private Export(final HttpClient client, final ExportInput input) {
        super(APIService.PREPARATION_GROUP, client);
        this.input = input;
    }

    @Override
    protected InputStream run() throws Exception {
        String dataSetId;
        String encodedActions = null;

        //Get dataset id and actions from preparation
        if(input.getPreparationId() != null) {
            final JsonNode preparationDetails = getPreparationDetails(input.getPreparationId());

            final List<String> currentStepsIds = getActionsStepIds(preparationDetails, input.getStepId());
            final Map<String, Action> actions = getActions(preparationDetails, currentStepsIds);

            dataSetId = preparationDetails.get("dataSetId").textValue();
            encodedActions = serializeAndEncode(actions);
        }
        //Get provided dataset id
        else {
            dataSetId = input.getDatasetId();
        }

        //Get dataset content and call export service
        final String uri = this.transformationServiceUrl + "/transform/export" + (encodedActions != null ? "?actions=" + encodedActions : "");
        final HttpPost transformationCall = new HttpPost(uri);
        final InputStream content = getDatasetContent(dataSetId);
        transformationCall.setEntity(new InputStreamEntity(content));

        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }
}
