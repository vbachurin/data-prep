package org.talend.dataprep.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.WebApplicationContext;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class APIService {

    public static final HystrixCommandGroupKey PREPARATION_GROUP = HystrixCommandGroupKey.Factory.asKey("preparation"); //$NON-NLS-1$

    public static final HystrixCommandGroupKey TRANSFORM_GROUP = HystrixCommandGroupKey.Factory.asKey("transform"); //$NON-NLS-1$

    public static final HystrixCommandGroupKey DATASET_GROUP = HystrixCommandGroupKey.Factory.asKey("dataset"); //$NON-NLS-1$

    protected static final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    protected static final Log LOG = LogFactory.getLog(APIService.class);

    @Autowired
    private WebApplicationContext context;

    @Value("${transformation.service.url}")
    protected String transformServiceUrl;

    @Value("${dataset.service.url}")
    protected String contentServiceUrl;

    @Value("${preparation.service.url}")
    protected String preparationServiceURL;

    public APIService() {
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(50);
    }

    protected  <T extends HystrixCommand> T getCommand(Class<T> clazz, Object... args) {
        return context.getBean(clazz, args);
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
        return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }
}
