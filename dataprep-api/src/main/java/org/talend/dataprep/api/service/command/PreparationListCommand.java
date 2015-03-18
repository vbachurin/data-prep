package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.talend.dataprep.api.service.APIService;

import com.netflix.hystrix.HystrixCommand;

public class PreparationListCommand extends HystrixCommand<InputStream> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final Format format;

    public enum Format {
        SHORT,
        LONG
    }

    public PreparationListCommand(HttpClient client, String preparationServiceUrl, Format format) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceUrl;
        this.format = format;
    }

    @Override
    protected InputStream getFallback() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    protected InputStream run() throws Exception {
        HttpGet contentRetrieval;
        switch (format) {
            case SHORT:
                contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations"); //$NON-NLS-1$
                break;
            case LONG:
                contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations/all"); //$NON-NLS-1$
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
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
        }
        throw new RuntimeException("Unable to retrieve preparation list.");
    }
}
