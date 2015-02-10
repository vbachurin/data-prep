package org.talend.dataprep.api.service.command;

import com.netflix.hystrix.HystrixCommand;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class TransformCommand extends ChainedCommand<InputStream, InputStream> {

    private final String transformServiceUrl;

    private final String actions;

    private final HttpClient client;

    public TransformCommand(HttpClient client, String transformServiceUrl, HystrixCommand<InputStream> content, String actions) {
        super(content);
        this.transformServiceUrl = transformServiceUrl;
        this.actions = actions;
        this.client = client;
    }

    @Override
    protected InputStream getFallback() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    protected InputStream run() throws Exception {
        String uri = transformServiceUrl + "/?actions=" + Base64.getEncoder().encodeToString(actions.getBytes("UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
        HttpPost transformationCall = new HttpPost(uri);
        transformationCall.setEntity(new InputStreamEntity(getInput()));
        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(), transformationCall::releaseConnection);
    }
}
