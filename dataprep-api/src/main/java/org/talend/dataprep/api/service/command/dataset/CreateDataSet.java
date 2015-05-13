package org.talend.dataprep.api.service.command.dataset;

import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;

import com.netflix.hystrix.HystrixCommand;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

@Component
@Scope("request")
public class CreateDataSet extends DataPrepCommand<String> {

    private final String name;

    private final InputStream dataSetContent;

    private CreateDataSet(HttpClient client, String name, InputStream dataSetContent) {
        super(PreparationAPI.DATASET_GROUP, client);
        this.name = name;
        this.dataSetContent = dataSetContent;
    }

    @Override
    protected String run() throws Exception {
        HttpPost contentCreation = new HttpPost(datasetServiceUrl + "/datasets/?name=" + URLEncoder.encode(name, "UTF-8")); //$NON-NLS-1$
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
        throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_DATASET);
    }
}
