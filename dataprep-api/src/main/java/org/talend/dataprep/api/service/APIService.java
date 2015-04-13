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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.exception.Exceptions;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class APIService {

    public static final HystrixCommandGroupKey PREPARATION_GROUP = HystrixCommandGroupKey.Factory.asKey("preparation"); //$NON-NLS-1$

    public static final HystrixCommandGroupKey TRANSFORM_GROUP = HystrixCommandGroupKey.Factory.asKey("transform"); //$NON-NLS-1$

    public static final HystrixCommandGroupKey DATASET_GROUP = HystrixCommandGroupKey.Factory.asKey("dataset"); //$NON-NLS-1$

    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    protected static final Logger LOG = LoggerFactory.getLogger( APIService.class );

    @Value("${transformation.service.url}")
    protected String transformServiceUrl;

    @Value("${dataset.service.url}")
    protected String contentServiceUrl;

    @Value("${preparation.service.url}")
    protected String preparationServiceURL;

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
            throw Exceptions.Internal(APIMessages.UNABLE_TO_FIND_COMMAND, clazz, args.length, e);
        }
    }

    void setDataSetServiceURL(String dataSetServiceURL) {
        this.contentServiceUrl = dataSetServiceURL;
    }

    void setTransformationServiceURL(String transformationServiceURL) {
        this.transformServiceUrl = transformationServiceURL;
    }

    void setPreparationServiceURL(String preparationServiceURL) {
        this.preparationServiceURL = preparationServiceURL;
    }

    protected HttpClient getClient() {
        return httpClient;
    }
}
