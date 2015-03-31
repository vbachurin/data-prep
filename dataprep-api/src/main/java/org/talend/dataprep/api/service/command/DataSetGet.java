package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.PreparationAPI;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class DataSetGet extends HystrixCommand<InputStream> {

    private final String contentServiceUrl;

    private final HttpClient client;

    private final boolean metadata;

    private final String dataSetId;

    private final boolean columns;

    private DataSetGet(HttpClient client, String contentServiceUrl, String dataSetId, boolean metadata, boolean columns) {
        super(PreparationAPI.TRANSFORM_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.client = client;
        this.metadata = metadata;
        this.dataSetId = dataSetId;
        this.columns = columns;
    }

    @Override
    protected InputStream getFallback() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    protected InputStream run() throws Exception {
        HttpGet contentRetrieval = new HttpGet(contentServiceUrl + "/" + dataSetId + "/content/?metadata=" + metadata
                + "&columns=" + columns);
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
        throw new RuntimeException("Unable to retrieve content.");
    }
}
