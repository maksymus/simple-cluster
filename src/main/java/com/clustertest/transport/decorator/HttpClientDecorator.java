package com.clustertest.transport.decorator;

import com.clustertest.cluster.Instance;
import com.clustertest.cluster.Instances;
import com.clustertest.transport.HttpClient;
import com.clustertest.transport.HttpResponse;
import org.glassfish.jersey.internal.util.Producer;

import java.util.List;
import java.util.function.Function;

public abstract class HttpClientDecorator implements HttpClient {

    protected abstract <R> R execute(Function<? super HttpClient, R> executor);

    @Override
    public HttpResponse<Void> register(Instance instance) {
        return execute((HttpClient delegate) -> delegate.register(instance));
    }

    @Override
    public HttpResponse<Void> cancel(Instance instance) {
        return execute((HttpClient delegate) -> delegate.cancel(instance));
    }

    @Override
    public HttpResponse<Void> heartbeat(Instance instance) {
        return execute((HttpClient delegate) -> delegate.heartbeat(instance));
    }

    @Override
    public HttpResponse<Instances>  getInstances() {
        return execute((HttpClient delegate) -> delegate.getInstances());
    }
}
