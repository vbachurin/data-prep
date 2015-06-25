package org.talend.dataprep.api.service.command.preparation;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.CloneInputStream;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.api.service.command.dataset.DataSetGet;
import org.talend.dataprep.api.service.command.transformation.Transform;
import org.talend.dataprep.preparation.store.ContentCache;

import com.fasterxml.jackson.databind.JsonNode;

@Component
@Scope("request")
public class PreparationGetContent extends PreparationCommand<InputStream> {

    private final String id;

    private final String version;

    @Autowired
    ContentCache contentCache;

    private PreparationGetContent(HttpClient client, String id, String version) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
        this.version = version;
    }

    @Override
    protected InputStream run() throws Exception {
        // TODO Handle concurrent accesses to cache
        if (contentCache.has(id, version)) {
            return contentCache.get(id, version);
        }
        // retrieve preparation details
        final JsonNode preparation = getPreparationDetails(id);
        final String dataSetId = preparation.get("dataSetId").textValue();

        // get the actions to execute
        final List<String> stepIds = getActionsStepIds(preparation, version);
        final Map<String, Action> actions = getActions(preparation, stepIds);
        final String encodedActions = serializeAndEncode(actions);

        // Get the data set , and pass it to the transformation service as input
        final DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, client, dataSetId, false, true);
        final Transform transformCommand = context.getBean(Transform.class, client, retrieveDataSet, encodedActions);

        // ... and send it back to user (but saves it back in content cache).
        return new CloneInputStream(transformCommand.execute(), contentCache.put(id, version));
    }
}
