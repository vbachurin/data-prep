package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.exception.Exceptions;

import com.netflix.hystrix.HystrixCommand;

/**
 * Command used to retreive the preparations used by a given dataset.
 */
@Component
@Scope("request")
public class PreparationListForDataSet extends HystrixCommand<InputStream> {

    /** Rest http client. */
    private final HttpClient client;

    /** Preparation service base url. */
    private final String preparationServiceUrl;

    /** The wanted DataSet id. */
    private final String dataSetId;

    /**
     * Private constructor.
     *
     * @param client the http client to use to access the preparation service.
     * @param preparationServiceUrl the prepration service base url.
     * @param dataSetId the wanted dataset id.
     */
    private PreparationListForDataSet(HttpClient client, String preparationServiceUrl, String dataSetId) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceUrl;
        this.dataSetId = dataSetId;
    }

    /**
     * @see HystrixCommand#run()
     */
    @Override
    protected InputStream run() throws Exception {

        HttpGet contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations?dataSetId=" + dataSetId); //$NON-NLS-1$

        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_ACCEPTED) {
                // Immediately release connection
                contentRetrieval.releaseConnection();
                return new ByteArrayInputStream(new byte[0]);
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
            }
        } else {
            contentRetrieval.releaseConnection();
        }
        throw Exceptions.User(APIMessages.UNABLE_TO_RETRIEVE_PREPARATION_LIST);
    }

}
