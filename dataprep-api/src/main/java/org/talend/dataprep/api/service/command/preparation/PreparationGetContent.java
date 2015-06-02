package org.talend.dataprep.api.service.command.preparation;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.api.service.command.dataset.DataSetGet;
import org.talend.dataprep.api.service.command.transformation.Transform;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.JsonNode;

@Component
@Scope("request")
public class PreparationGetContent extends PreparationCommand<InputStream> {

    private final String id;

    private final String version;

    private PreparationGetContent(HttpClient client, String id, String version) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
        this.version = version;
    }

    @Override
    protected InputStream run() throws Exception {
        // try to get content from cache
        final HttpGet contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id + "/content/" + version);
        final HttpResponse response = client.execute(contentRetrieval);
        final int statusCode = response.getStatusLine().getStatusCode();

        switch (statusCode) {
        case SC_OK:
            return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);

        case SC_ACCEPTED:
            contentRetrieval.releaseConnection();

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

            // ... and send it back to user (but saves it back in preparation service as cache).
            return transformCommand.execute(); // TODO saves it back in preparation service as cache

        default:
            contentRetrieval.releaseConnection();
            throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_PREPARATION_CONTENT);
        }
    }
}
