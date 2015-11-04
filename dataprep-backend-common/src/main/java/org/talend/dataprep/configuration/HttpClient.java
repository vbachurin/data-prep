package org.talend.dataprep.configuration;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Http client bean name.
 */
@Configuration
public class HttpClient {

    /** This class' logger. */
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    @Value("${http.pool.size:50}")
    private int maxPoolSize;

    /**
     * @return the http connection manager.
     */
    @Bean(destroyMethod = "shutdown")
    public PoolingHttpClientConnectionManager getConnectionManager() {
        return new PoolingHttpClientConnectionManager();
    }

    /**
     *
     * @param connectionManager the connection manager.
     * @return the http client to use.
     */
    @Bean(destroyMethod = "close")
    public CloseableHttpClient getHttpClient(PoolingHttpClientConnectionManager connectionManager) {
        connectionManager.setMaxTotal(maxPoolSize);
        return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }

}
