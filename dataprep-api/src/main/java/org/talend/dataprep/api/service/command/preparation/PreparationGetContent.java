package org.talend.dataprep.api.service.command.preparation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.api.service.command.transformation.Transform;
import org.talend.dataprep.api.service.command.dataset.DataSetGet;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationGetContent extends DataPrepCommand<InputStream> {

    private final String id;

    private final String version;

    private PreparationGetContent(HttpClient client, String id, String version) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
        this.version = version;
    }

    @Override
    protected InputStream run() throws Exception {
        HttpGet contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id + "/content/" + version);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_ACCEPTED) {
                // TODO Should get actions from context
                // Preparation has the version... but no longer any content associated with it, rebuilds it
                // First get the preparation at version
                HttpGet preparationRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id); //$NON-NLS-1$
                JsonNode tree;
                try {
                    ObjectMapper mapper = builder.build();
                    InputStream content = client.execute(preparationRetrieval).getEntity().getContent();
                    tree = mapper.reader().readTree(content);
                } finally {
                    preparationRetrieval.releaseConnection();
                }
                // Get the data set
                String dataSetId = tree.get("dataSetId").textValue();
                DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, client, dataSetId, false, true);
                // ... transform it ...
                HttpGet actionsRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id + "/actions/" + version); //$NON-NLS-1$
                String actions;
                try {
                    actions = IOUtils.toString(client.execute(actionsRetrieval).getEntity().getContent());
                } finally {
                    actionsRetrieval.releaseConnection();
                }
                Transform transformCommand = context.getBean(Transform.class, client, retrieveDataSet,
                        Base64.getEncoder().encodeToString(actions.getBytes()));
                // ... and send it back to user (but saves it back in preparation service as cache).
                return new ReleasableInputStream(transformCommand.execute(), contentRetrieval::releaseConnection); // TODO saves it back in preparation service as cache
            } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                // Immediately release connection
                contentRetrieval.releaseConnection();
                return new ByteArrayInputStream(new byte[0]);
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
            }
        } else {
            contentRetrieval.releaseConnection();
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_PREPARATION_CONTENT);
    }
}
