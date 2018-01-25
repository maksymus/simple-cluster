package com.clustertest.transport.jersey;

import com.clustertest.client.resolver.ClusterEndpoint;
import com.clustertest.cluster.Instance;
import com.clustertest.cluster.Instances;
import com.clustertest.transport.HttpClient;
import com.clustertest.transport.HttpResponse;
import com.clustertest.transport.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache4.ApacheHttpClient4;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Low level jersey 1 impl.
 */
public class JerseyHttpClient implements HttpClient {
    public static final String INSTANCE_PATH = "/instance";

    private Client jerseyClient;
    private String serviceUrl;

    public JerseyHttpClient(Client jerseyClient, String serviceUrl) {
        this.jerseyClient = jerseyClient;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public HttpResponse<Void> register(Instance instance) {
        WebResource.Builder requestBuilder = jerseyClient.resource(serviceUrl)
                .path(INSTANCE_PATH)
                .path(instance.getName())
                .getRequestBuilder();

        ClientResponse response = requestBuilder
//                .header("Accept-Encoding", "gzip")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, instance);

        return new HttpResponse.HttpResponseBuilder<Void>()
                .withStatus(HttpStatus.fromStatusCode(response.getStatus()))
                .build();
    }

    @Override
    public HttpResponse<Void> cancel(Instance instance) {
        WebResource.Builder requestBuilder = jerseyClient.resource(serviceUrl)
                .path(INSTANCE_PATH)
                .path(instance.getName())
                .getRequestBuilder();

        ClientResponse response = requestBuilder
//                .header("Accept-Encoding", "gzip")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);

        return new HttpResponse.HttpResponseBuilder<Void>()
                .withStatus(HttpStatus.fromStatusCode(response.getStatus()))
                .build();
    }

    @Override
    public HttpResponse<Void> heartbeat(Instance instance) {
        WebResource.Builder requestBuilder = jerseyClient.resource(serviceUrl)
                .path(INSTANCE_PATH)
                .path(instance.getName())
                .getRequestBuilder();

        ClientResponse response = requestBuilder
//                .header("Accept-Encoding", "gzip")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);

        return new HttpResponse.HttpResponseBuilder<Void>()
                .withStatus(HttpStatus.fromStatusCode(response.getStatus()))
                .build();
    }

    @Override
    public HttpResponse<Instances> getInstances() {
        WebResource.Builder requestBuilder = jerseyClient.resource(serviceUrl)
                .path("/instance")
                .getRequestBuilder();

        ClientResponse response = requestBuilder
//                .header("Accept-Encoding", "gzip")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        Instances data = response.getStatus() == ClientResponse.Status.OK.getStatusCode() && response.hasEntity() ?
                response.getEntity(Instances.class) : null;

        return new HttpResponse.HttpResponseBuilder<Instances>()
                .withStatus(HttpStatus.fromStatusCode(response.getStatus()))
                .withData(data)
                .build();
    }

    public static HttpClient newClient(ClusterEndpoint clusterEndpoint) {
        return new JerseyHttpClient(JerseyClientBuilder.build(), clusterEndpoint.getServiceUrl());
    }

    private static class JerseyClientBuilder {
        public static Client build() {
            return ApacheHttpClient4.create(new DefaultClientConfig());
        }
    }

    public static void main(String[] args) throws IOException {
//        HttpClient httpClient = JerseyHttpClient.newClient(new ClusterEndpoint("http://localhost:9998/cluster"));
//
//        HttpResponse<Instances> response = httpClient.getInstances();
//        System.out.println(response.getData());

//        HttpResponse<Void> registerResponse = httpClient.register(new Instance("inst1", "localhost", "", 9999));
//        System.out.println(registerResponse.getStatus());

//        Instances instances1 = new Instances();
//
//        List<Instance> instances = new ArrayList<>();
//        Instance instance = new Instance();
//        instance.setName("name");
//        instance.setPort(1234);
//
//        instances.add(instance);
//        instances1.getInstances().add(instance);
//
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(mapper.writeValueAsString(instances1));
    }
}
