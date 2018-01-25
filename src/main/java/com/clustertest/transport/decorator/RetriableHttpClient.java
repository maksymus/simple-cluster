package com.clustertest.transport.decorator;

import com.clustertest.client.resolver.ClusterEndpoint;
import com.clustertest.client.resolver.ClusterResolver;
import com.clustertest.transport.HttpClient;
import org.glassfish.jersey.internal.util.Producer;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Retries connect n times.
 */
public class RetriableHttpClient extends HttpClientDecorator {
    private final int maxRetries = 3;
    Producer<HttpClient> clientFactory;

    public RetriableHttpClient(Producer<HttpClient> clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    protected <R> R execute(Function<? super HttpClient, R> executor) {
        for (int retryCount = 0; retryCount < maxRetries; retryCount++) {
            HttpClient httpClient = clientFactory.call();
            return executor.apply(httpClient);
        }

        throw new RuntimeException("transport exception: not able to call web service - retries failed");
    }
}
