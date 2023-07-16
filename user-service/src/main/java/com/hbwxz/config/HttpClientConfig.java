package com.hbwxz.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * 使用HttpClient替换RestTemplate底层的connection请求
 * @author Night
 * @date 2023/7/15 23:53
 */
@Slf4j
@Configuration
public class HttpClientConfig {
    // 定义我们的connection 超时时间 30秒
    // 定义我们的request 请求超时时间 30秒
    // 定义我们的socket 超时时间 60秒
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int REQUEST_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 60000;

    // 连接池的相关配置pooling的一个管理
    private static final int MAX_TOTAL_CONNECTIONS = 50;

    // 默认的连接池中的 keep-live 线程的配置
    // 如果我们的header中携带了超时时间，则优先使用header中的超时时间，否则使用这里的默认超时时间  20秒
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20000;

    // 清理我们的空闲线程的一个定时任务
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

    /**
     * CloseableHttpClient 可关闭式的HTTPClient
     * @return
     */
    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingHttpClientConnectionManager()) // pooling线程池的设置
                .setKeepAliveStrategy(connectionKeepAliveStrategy()) // keep-live的设置
                .build();
    }

    /**
     * 进行https和http的注册
     * 最终是为了加入我们的MAX_TOTAL_CONNECTIONS
     * @return
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        // https
        SSLContextBuilder builder = new SSLContextBuilder();
        // 如果需要key store (trustStore)

        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.error("loadTrustMaterial failed,error details = {}", e);
        }

        SSLConnectionSocketFactory sslConnectionSocketFactory = null;

        try {
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("SSLConnectionSocketFactory create failed,error details = {}", e);
        }

        // SSL的创建是为了注册
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 设置最大连接数
        poolingHttpClientConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        return poolingHttpClientConnectionManager;
    }

    /**
     * 默认的连接池中的 keep-live 线程的配置
     * 如果我们的header中携带了超时时间，则优先使用header中的超时时间，否则使用这里的默认超时时间  20秒
     * @return
     */
    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            // 如果我们的header中携带了超时时间，则优先使用header中的超时时间，否则使用这里的默认超时时间
            // 如果我们的header中没有携带超时时间，则使用默认的超时时间
            BasicHeaderElementIterator headerElementIterator = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (headerElementIterator.hasNext()) {
                HeaderElement headerElement = headerElementIterator.nextElement();
                String param = headerElement.getName();
                String value = headerElement.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
        };
    }

    /**
     * 定时清理空闲的 connection + 定时清理过期的 connection
     * 根源来自于我们的连接池 poolingHttpClientConnectionManager
     * @param poolingHttpClientConnectionManager
     * @return
     */
    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 10000)
            public void run() {
                try {
                    if (poolingHttpClientConnectionManager != null) {
                        log.info("run IdleConnectionMonitor - Closing expired and idle connections...");
                        // 关闭过期的连接
                        poolingHttpClientConnectionManager.closeExpiredConnections();
                        // 关闭空闲的连接
                        poolingHttpClientConnectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS);
                        log.info("run IdleConnectionMonitor - Closing expired and idle connections finished.");
                    } else {
                        log.warn("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                    }
                } catch (Exception e) {
                    log.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
                }
            }
        };
    }
}
