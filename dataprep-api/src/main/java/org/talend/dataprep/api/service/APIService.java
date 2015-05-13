package org.talend.dataprep.api.service;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class APIService {

    public static final HystrixCommandGroupKey PREPARATION_GROUP = HystrixCommandGroupKey.Factory.asKey("preparation"); //$NON-NLS-1$

    public static final HystrixCommandGroupKey TRANSFORM_GROUP = HystrixCommandGroupKey.Factory.asKey("transform"); //$NON-NLS-1$

    public static final HystrixCommandGroupKey DATASET_GROUP = HystrixCommandGroupKey.Factory.asKey("dataset"); //$NON-NLS-1$

    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    protected static final Logger LOG = LoggerFactory.getLogger(APIService.class);

    @Autowired
    private WebApplicationContext context;

    private CloseableHttpClient httpClient;

    public APIService() {
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(50);
        httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }

    @PreDestroy
    private void shutdown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOG.error("Unable to close HTTP client on shutdown.", e);
        }
        this.connectionManager.shutdown();
    }

    public PoolingHttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    protected <T extends HystrixCommand> T getCommand(Class<T> clazz, Object... args) {
        try {
            return context.getBean(clazz, args);
        } catch (BeansException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_FIND_COMMAND, e, TDPExceptionContext.build().put("class", clazz).put("args", args));
        }
    }

    protected HttpClient getClient() {
        return httpClient;
    }
}
