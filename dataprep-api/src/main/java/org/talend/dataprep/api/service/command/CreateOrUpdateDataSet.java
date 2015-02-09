package org.talend.dataprep.api.service.command;

import com.netflix.hystrix.HystrixCommand;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.talend.dataprep.api.service.DataPreparationAPI;

import java.io.InputStream;

public class CreateOrUpdateDataSet extends HystrixCommand<String> {

    private final String contentServiceUrl;

    private final String id;

    private final String name;

    private final InputStream dataSetContent;

    private final HttpClient client;

    public CreateOrUpdateDataSet(HttpClient client, String contentServiceUrl, String id, String name, InputStream dataSetContent) {
        super(DataPreparationAPI.DATASET_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.id = id;
        this.name = name;
        this.dataSetContent = dataSetContent;
        this.client = client;
    }

    @Override
    protected String getFallback() {
        throw new RuntimeException("Fallback not supported in this command.");
    }

    @Override
    protected String run() throws Exception {
        HttpPut contentCreation = new HttpPut(contentServiceUrl + "/" + id + "/raw/?name=" + name); //$NON-NLS-1$ //$NON-NLS-2$
        contentCreation.setEntity(new InputStreamEntity(dataSetContent));
        HttpResponse response = client.execute(contentCreation);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_NO_CONTENT) {
                return StringUtils.EMPTY;
            } else if (statusCode == HttpStatus.SC_OK) {
                return IOUtils.toString(response.getEntity().getContent());
            }
        }
        throw new RuntimeException("Unable to create content.");
    }
}
