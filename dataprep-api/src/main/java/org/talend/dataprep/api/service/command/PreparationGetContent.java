package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;

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
import org.talend.dataprep.api.service.APIService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationGetContent extends HystrixCommand<InputStream> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final String id;

    private final String version;

    private final String contentServiceUrl;

    private final String transformServiceUrl;

    @Autowired
    private WebApplicationContext context;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private PreparationGetContent(HttpClient client, String preparationServiceUrl, String contentServiceUrl,
            String transformServiceUrl, String id, String version) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceUrl;
        this.contentServiceUrl = contentServiceUrl;
        this.transformServiceUrl = transformServiceUrl;
        this.id = id;
        this.version = version;
    }

    @Override
    protected InputStream getFallback() {
        return new ByteArrayInputStream(new byte[0]);
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
                ObjectMapper mapper = builder.build();
                InputStream content = client.execute(preparationRetrieval).getEntity().getContent();
                JsonNode tree = mapper.reader().readTree(content);
                // Get the data set
                String dataSetId = tree.get("dataSetId").textValue();
                DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, client, contentServiceUrl, dataSetId, false, true);
                // ... transform it ...
                HttpGet actionsRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id + "/actions/" + version); //$NON-NLS-1$
                String actions = IOUtils.toString(client.execute(actionsRetrieval).getEntity().getContent());
                Transform transformCommand = context.getBean(Transform.class, client, transformServiceUrl, retrieveDataSet, Base64.getEncoder()
                        .encodeToString(actions.getBytes()));
                // ... and send it back to user (but saves it back in preparation service).
                return new CloneInputStream(transformCommand.execute(), Collections.emptyList()); // TODO
            } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                // Immediately release connection
                contentRetrieval.releaseConnection();
                return new ByteArrayInputStream(new byte[0]);
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
            }
        }
        throw new RuntimeException("Unable to retrieve preparation list.");
    }
}
