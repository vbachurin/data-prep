package org.talend.dataprep.api.service.command;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class Transform extends ChainedCommand<InputStream, InputStream> {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(Transform.class);

    private final String transformServiceUrl;

    private final String actions;

    private final HttpClient client;

    private Transform(HttpClient client, String transformServiceUrl, HystrixCommand<InputStream> content, String actions) {
        super(content);
        this.transformServiceUrl = transformServiceUrl;
        this.actions = actions;
        this.client = client;
    }

    @Override
    protected InputStream run() throws Exception {

        String uri = transformServiceUrl + "/transform/?actions=" + actions; //$NON-NLS-1$

        // TODO temp log to see what's going in newbuild
        LOG.error("post on " + uri);

        InputStreamEntity datasetContent = new InputStreamEntity(getInput());

        // TODO temp log to see what's going in newbuild
        LOG.error("dataset retrieved...");

        HttpPost transformationCall = new HttpPost(uri);
        transformationCall.setEntity(datasetContent);
        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }
}
