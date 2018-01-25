package com.clustertest.client;

import com.clustertest.client.resolver.ClusterEndpoint;
import com.clustertest.client.resolver.ClusterResolver;
import com.clustertest.configuration.ClusterConfigurationProperties;
import com.clustertest.transport.HttpClient;
import com.clustertest.transport.decorator.RetriableHttpClient;
import com.clustertest.transport.decorator.SessionHttpClient;
import com.clustertest.transport.decorator.TransportHttpClient;
import com.clustertest.transport.jersey.JerseyHttpClient;
import org.glassfish.jersey.internal.util.Producer;

import java.util.function.Function;

public class ClusterClientTransport {
    private ClusterResolver clusterResolver;

    private HttpClient registrationClient;

    private HttpClient queryClient;

    private ClusterConfigurationProperties configProperties;

    public ClusterClientTransport(ClusterConfigurationProperties configProperties) {
        this.configProperties = configProperties;
        this.clusterResolver = new ClusterResolver<>(configProperties);
        init();
    }

    public HttpClient getRegistrationClient() {
        return registrationClient;
    }

    public HttpClient getQueryClient() {
        return queryClient;
    }

    private void init() {
        //TODO temp implementation - should use injection for low level transport
        // low level impl transport factory
        Function<ClusterEndpoint, HttpClient> transportFactory = JerseyHttpClient::newClient;

        // implementation independent
        Producer<HttpClient> httpClientTransportFactory = () -> new TransportHttpClient(transportFactory, clusterResolver);
        Producer<HttpClient> retriableHttpClientFactory = () -> new RetriableHttpClient(httpClientTransportFactory);
        Producer<HttpClient> sessionHttpClientFactory = () -> new SessionHttpClient(retriableHttpClientFactory);

        this.registrationClient = sessionHttpClientFactory.call();
        this.queryClient = sessionHttpClientFactory.call();
    }

    public static void main(String[] args) {
        ClusterConfigurationProperties configurationProperties = new ClusterConfigurationProperties();
        configurationProperties.setNodes("http://localhost:9090/cluster");
        ClusterClientTransport transport = new ClusterClientTransport(configurationProperties);
//        transport.getQueryClient().register();
    }
}
