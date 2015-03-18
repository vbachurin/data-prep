package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.preparation.Preparation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

public class PreparationGetContentCommand extends HystrixCommand<InputStream> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final String id;

    private final String version;

    private final String contentServiceUrl;

    private final String transformServiceUrl;

    public PreparationGetContentCommand(HttpClient client, String preparationServiceUrl, String contentServiceUrl,
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
        HttpGet contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id + "/content?" + version);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_ACCEPTED) {
                // Preparation has the version... but no longer any content associated with it, rebuilds it
                // First get the preparation at version
                HttpGet preparationRetrieval = new HttpGet(preparationServiceUrl + "/preparations/" + id + "/" + version); //$NON-NLS-1$
                ObjectMapper mapper = new ObjectMapper();
                InputStream content = client.execute(preparationRetrieval).getEntity().getContent();
                Preparation preparation = mapper.reader(Preparation.class).readValue(content);
                // Get the data set
                DataSetGetCommand retrieveDataSet = new DataSetGetCommand(client, contentServiceUrl, preparation.getDataSetId(),
                        false, false);
                // ... transform it ...
                String actions = preparation.getActions().toString(); // TODO
                TransformCommand transformCommand = new TransformCommand(client, transformServiceUrl, retrieveDataSet, actions);
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
