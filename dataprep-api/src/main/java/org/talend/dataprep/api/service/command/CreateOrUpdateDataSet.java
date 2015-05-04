package org.talend.dataprep.api.service.command;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class CreateOrUpdateDataSet extends HystrixCommand<String> {

    private final String contentServiceUrl;

    private final String id;

    private final String name;

    private final InputStream dataSetContent;

    private final HttpClient client;

    private CreateOrUpdateDataSet(HttpClient client, String contentServiceUrl, String id, String name, InputStream dataSetContent) {
        super(PreparationAPI.DATASET_GROUP);
        this.contentServiceUrl = contentServiceUrl;
        this.id = id;
        this.name = name;
        this.dataSetContent = dataSetContent;
        this.client = client;
    }

    @Override
    protected String run() throws Exception {
        HttpPut contentCreation = new HttpPut(contentServiceUrl + "/" + id + "/raw/?name=" + name); //$NON-NLS-1$ //$NON-NLS-2$
        try {
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
        } finally {
            contentCreation.releaseConnection();
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_OR_UPDATE_DATASET);
    }
}
