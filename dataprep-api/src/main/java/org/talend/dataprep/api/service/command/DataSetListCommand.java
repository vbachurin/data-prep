package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.talend.dataprep.api.service.DataPreparationAPI;

import com.netflix.hystrix.HystrixCommand;

public class DataSetListCommand extends HystrixCommand<InputStream> {

    private final String contentServiceUrl;

    private final HttpClient client;

    public DataSetListCommand(HttpClient client, String contentServiceUrl) {
        super(DataPreparationAPI.TRANSFORM_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.client = client;
    }

    @Override
    protected InputStream getFallback() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    protected InputStream run() throws Exception {
        HttpGet contentRetrieval = new HttpGet(contentServiceUrl);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_ACCEPTED) {
                return new ByteArrayInputStream(new byte[0]);
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
            }
        }
        throw new RuntimeException("Unable to list datasets.");
    }
}
