package org.talend.dataprep.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;

import com.netflix.hystrix.HystrixCommandGroupKey;

public class APIService {

    public static final HystrixCommandGroupKey TRANSFORM_GROUP = HystrixCommandGroupKey.Factory.asKey("transform"); //$NON-NLS-1$

    public static final HystrixCommandGroupKey DATASET_GROUP = HystrixCommandGroupKey.Factory.asKey("dataset"); //$NON-NLS-1$

    protected static final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    protected static final Log LOG = LogFactory.getLog(APIService.class);

    @Value("${transformation.service.url}")
    protected String transformServiceUrl;

    @Value("${dataset.service.url}")
    protected String contentServiceUrl;

    public APIService() {
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(50);
    }

    void setDataSetServiceURL(String dataSetServiceURL) {
        this.contentServiceUrl = dataSetServiceURL;
    }

    void setTransformationServiceURL(String transformationServiceURL) {
        this.transformServiceUrl = transformationServiceURL;
    }

}
