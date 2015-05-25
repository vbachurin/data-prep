package org.talend.dataprep.api.service.command.transformation;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.ChainedCommand;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class SuggestActionParams extends ChainedCommand<InputStream, InputStream> {

    /**
     * This class' logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SuggestActionParams.class);

    /**
     * The target action for this parameters getter operation
     */
    private final String action;
    private String columnId;

    private SuggestActionParams(final HttpClient client, final HystrixCommand<InputStream> content, final String action, final String columnId) {
        super(client, content);
        this.action = action;
        this.columnId = columnId;
    }

    @Override
    protected InputStream run() throws Exception {
        try {
            final String uri = transformationServiceUrl + "/transform/suggest/" + action + "/params?columnId=" + columnId;
            final InputStreamEntity content = new InputStreamEntity(getInput());

            final HttpPost getParametersCall = new HttpPost(uri);
            getParametersCall.setEntity(content);

            return new ReleasableInputStream(client.execute(getParametersCall).getEntity().getContent(),
                    getParametersCall::releaseConnection);
        } catch (Exception e) {
            LOG.error("Exception while processing GET dynamic parameters : " + e.getMessage(), e);
            throw e;
        }

    }
}
