package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.http.MediaType;
import org.talend.dataprep.api.service.DataPreparationAPI;

import com.netflix.hystrix.HystrixCommand;

public class DataSetUpdateCommand extends ChainedCommand<InputStream, InputStream> {

    private final String contentServiceUrl;

    private final HttpClient client;

    private final String dataSetId;

    private final String actions;

    public DataSetUpdateCommand(HttpClient client, String contentServiceUrl, String dataSetId,
            HystrixCommand<InputStream> transformedContent, String actions) {
        super(DataPreparationAPI.DATASET_GROUP, transformedContent);
        this.contentServiceUrl = contentServiceUrl;
        this.client = client;
        this.dataSetId = dataSetId;
        this.actions = actions;
    }

    @Override
    protected InputStream getFallback() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    protected InputStream run() throws Exception {
        HttpPut contentUpdate = new HttpPut(contentServiceUrl + "/" + dataSetId + "/content?actions=" + actions);
        contentUpdate.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        PipedInputStream result = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream(result);
        InputStream input = new CloneInputStream(getInput(), Collections.singletonList(pipedOutputStream));
        contentUpdate.setEntity(new InputStreamEntity(input));
        HttpResponse response = client.execute(contentUpdate);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return new ReleasableInputStream(result, contentUpdate::releaseConnection);
        } else {
            contentUpdate.releaseConnection();
            throw new RuntimeException("Unable to update content.");
        }
    }
}
