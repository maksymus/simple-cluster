package com.clustertest.transport.decorator;

import com.clustertest.client.resolver.ClusterEndpoint;
import com.clustertest.client.resolver.ClusterResolver;
import com.clustertest.transport.HttpClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Resolves and point and manages endpoints
 */
public class TransportHttpClient extends HttpClientDecorator {

    private final Function<ClusterEndpoint, HttpClient> transportClientFactory;
    private final ClusterResolver<ClusterEndpoint> endpointResolver;

    private AtomicReference<HttpClient> delegate = new AtomicReference<>();

    public TransportHttpClient(Function<ClusterEndpoint, HttpClient> transportClientFactory,
                               ClusterResolver<ClusterEndpoint> endpointResolver) {
        this.transportClientFactory = transportClientFactory;
        this.endpointResolver = endpointResolver;
    }

    @Override
    protected <R> R execute(Function<? super HttpClient, R> executor) {
        HttpClient httpClient = delegate.get();
        if (httpClient == null) {
            List<ClusterEndpoint> candidateEndpoints = getCandidateEndpoints();
            // TODO validate candidate list and check quarantined
            httpClient = transportClientFactory.apply(candidateEndpoints.get(0));
            delegate.compareAndSet(null, httpClient);
        }

        try {
            return executor.apply(delegate.get());
        } catch (Exception e) {
            // TODO blacklist endpoint
            throw new RuntimeException("transport exception: not able to call web service", e);
        }
    }

    private List<ClusterEndpoint> getCandidateEndpoints() {
        return endpointResolver.getEndpoints();
    }
}
