package org.talend.dataprep.api.service.command.error;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Command used to list the supported error codes from low level api.
 */
@Component
@Scope("prototype")
public class ErrorList extends DataPrepCommand<InputStream> {
    public enum ServiceType {DATASET, TRANSFORMATION, PREPARATION}

    /** http client to use to reach the api. */
    private final HttpClient client;

    private final ServiceType type;

    /**
     * Private constructor.
     *
     * @param client the http client to use to reach the api.
     * @param type the api type.
     * @param groupKey the command group key.
     */
    private ErrorList(HttpClient client, HystrixCommandGroupKey groupKey, ServiceType type) {
        super(groupKey, client);
        this.type = type;
        this.client = client;
    }

    /**
     * @return the error list in json format within the input stream.
     * @throws Exception if an error occurs.
     */
    @Override
    protected InputStream run() throws Exception {
        final String serviceUrl = getServiceUrl();
        HttpGet contentRetrieval = new HttpGet(serviceUrl);
        HttpResponse response = client.execute(contentRetrieval);

        StatusLine responseStatus = response.getStatusLine();
        int statusCode = responseStatus.getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_ACCEPTED) {
                contentRetrieval.releaseConnection();
                return new ByteArrayInputStream(new byte[0]);
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
            }
        }

        contentRetrieval.releaseConnection();
        String message = serviceUrl + " call returned " + responseStatus.getStatusCode() + '-' + responseStatus.getReasonPhrase();
        Exception cause = new Exception(message);
        throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_ERRORS, cause);
    }

    private String getServiceUrl() {
        switch (type) {
            case DATASET: return datasetServiceUrl + "/datasets/errors";
            case TRANSFORMATION: return transformationServiceUrl + "/transform/errors";
            case PREPARATION: return preparationServiceUrl + "/preparations/errors";
            default: return null;
        }
    }


}
