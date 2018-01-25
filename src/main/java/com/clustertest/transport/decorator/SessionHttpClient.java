package com.clustertest.transport.decorator;

import com.clustertest.transport.HttpClient;
import org.glassfish.jersey.internal.util.Producer;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Reconnect client after some duration.
 */
public class SessionHttpClient extends HttpClientDecorator {

    private final int maxSessionDurationMs = 30 * 1000;
    private Producer<HttpClient> clientFactory;

    private final AtomicReference<HttpClient> delegate = new AtomicReference<>();

    public SessionHttpClient(Producer<HttpClient> clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    protected <R> R execute(Function<? super HttpClient, R> executor) {
        HttpClient httpClient = delegate.get();
        if (httpClient == null) {
            delegate.compareAndSet(null, clientFactory.call());
            httpClient = delegate.get();
        }

        // TODO check timeout
        R result = executor.apply(httpClient);

        // TODO check session duration
        return result;
    }
}
