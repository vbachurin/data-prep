// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.configuration;

import java.util.concurrent.TimeUnit;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Http client bean name.
 */
@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
public class HttpClient {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    /** Maximum connection pool size. */
    @Value("${http.pool.size:50}")
    private int maxPoolSize;

    /**
     * Maximum connection allowed per route. see
     * https://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d5e393
     */
    @Value("${http.pool.maxPerRoute:50}")
    private int maxPerRoute;

    /** Default keep alive time for http connections if the optional keep alive is not set in the http headers. */
    @Value("${http.defaultKeepAlive:60}") // default is 60 seconds
    private int defaultKeepAlive;

    /**
     * @return the http connection manager.
     */
    @Bean(destroyMethod = "shutdown")
    public PoolingHttpClientConnectionManager getConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxPoolSize);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        // create a thread to monitor idle connections
        IdleConnectionJanitor staleMonitor = new IdleConnectionJanitor(connectionManager);
        staleMonitor.start();
        return connectionManager;
    }

    /**
     * @param connectionManager the connection manager.
     * @return the http client to use backed by a pooling connection manager.
     */
    @Bean(destroyMethod = "close")
    public CloseableHttpClient getHttpClient(PoolingHttpClientConnectionManager connectionManager) {
        return HttpClientBuilder.create() //
                .setConnectionManager(connectionManager) //
                .setKeepAliveStrategy(getKeepAliveStrategy()) //
                .setDefaultRequestConfig(getRequestConfig()) //
                .setRedirectStrategy(new RedirectTransferStrategy()) //
                .build();
    }

    /**
     * @return the http request configuration to use.
     */
    private RequestConfig getRequestConfig() {
        return RequestConfig.custom() //
                .setContentCompressionEnabled(true)
                .build();
    }

    /**
     * @return The connection keep alive strategy.
     */
    private ConnectionKeepAliveStrategy getKeepAliveStrategy() {

        return (response, context) -> {
            // Honor 'keep-alive' header
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch(NumberFormatException ignore) {
                        // let's move on the next header value
                        break;
                    }
                }
            }
            // otherwise use the default value
            return defaultKeepAlive * 1000;
        };
    }

    /**
     * Default redirection strategy that does not follow redirection.
     */
    static class RedirectTransferStrategy extends DefaultRedirectStrategy {

        /**
         * @see DefaultRedirectStrategy#isRedirected(HttpRequest, HttpResponse, HttpContext)
         */
        @Override
        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            return false;
        }

    }

    /**
     * Inner class in charge of cleaning idle connections.
     */
    static class IdleConnectionJanitor extends Thread {

        /** The connection manager to clean up every once in a while. */
        private final PoolingHttpClientConnectionManager connectionManager;
        /** Flag to notify this thread to shutdown. */
        private volatile boolean shutdown;

        /**
         * Constructor.
         * @param connectionManager the connection manager to monitor.
         */
        public IdleConnectionJanitor(PoolingHttpClientConnectionManager connectionManager) {
            super("Idle connections janitor");
            this.connectionManager = connectionManager;
        }

        /**
         * Close idle connections every 30 seconds.
         */
        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(30000); // every 30 seconds, close idle (inactive for more than 60 seconds) connections
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(60, TimeUnit.SECONDS);
                        LOGGER.debug("connection pool status {}", connectionManager.getTotalStats());
                    }
                }
            } catch (InterruptedException ex) {
                LOGGER.debug("Idle connections janitor (unexpected?) shutdown", ex);
                shutdown();
            }
        }

        /**
         * Shut down this janitor.
         */
        private void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }
}
