package org.talend.dataprep.api.service.command.transformation;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;

@Component
@Scope("request")
public class Transform extends DataPrepCommand<InputStream> {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(Transform.class);

    private final InputStream content;

    private final String actions;

    private Transform(HttpClient client, InputStream content, String actions) {
        super(APIService.TRANSFORM_GROUP, client);
        this.content = content;
        this.actions = actions;
    }

    @Override
    protected InputStream run() throws Exception {
        String uri = transformationServiceUrl + "/transform/?actions=" + actions; //$NON-NLS-1$
        HttpPost transformationCall = new HttpPost(uri);
        InputStreamEntity datasetContent = new InputStreamEntity(content);
        transformationCall.setEntity(datasetContent);
        try {
            HttpResponse response = client.execute(transformationCall);
            InputStream content = response.getEntity().getContent();
            return new ReleasableInputStream(content, transformationCall::releaseConnection);
        } catch (Exception e) {
            LOG.error("exception while processing transformation : " + e.getMessage(), e);
            throw e;
        }

    }
}
