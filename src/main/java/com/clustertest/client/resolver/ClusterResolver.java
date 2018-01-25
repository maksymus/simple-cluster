package com.clustertest.client.resolver;

import com.clustertest.client.ClusterClient;
import com.clustertest.configuration.ClusterConfigurationProperties;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClusterResolver <T extends ClusterEndpoint> {
    private ClusterConfigurationProperties configProperties;

    public ClusterResolver(ClusterConfigurationProperties configProperties) {
        this.configProperties = configProperties;
    }

    public List<T> getEndpoints() {
        String configNodes = configProperties.getNodes();
        if (configNodes == null)
            throw new IllegalArgumentException("Missing service urls");

        List<ClusterEndpoint> endpoints = Arrays.stream(configNodes.split(","))
                .map(node -> createEndpoint(node))
                .filter(Objects::nonNull)
                .filter(endpoint -> !isSelf(endpoint))
                .collect(Collectors.toList());

        if (endpoints.isEmpty())
            throw new IllegalArgumentException("Missing service urls");

        return (List<T>) endpoints;
    }
    
    private boolean isSelf(ClusterEndpoint clusterEndpoint) {
        if (configProperties.getInstance().getPort() != clusterEndpoint.getPort())
            return false;

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface =  networkInterfaces.nextElement();
                if (networkInterface.isUp()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress =  inetAddresses.nextElement();
                        if (Objects.equals(inetAddress.getHostAddress(), clusterEndpoint.getAddress()))
                            return true;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return true;
    } 

    private ClusterEndpoint createEndpoint(String serviceUrl) {
        try {
            return new ClusterEndpoint(serviceUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        ClusterConfigurationProperties configProps = new ClusterConfigurationProperties();
        ClusterResolver<ClusterEndpoint> clusterResolver = new ClusterResolver<>(configProps);

        configProps.setNodes("http://localhost:9090/cluster, http://localhost:9091/cluster");

        List<ClusterEndpoint> endpoints = clusterResolver.getEndpoints();
        for(ClusterEndpoint endpoint : endpoints) {
            System.out.println(endpoint.getAddress() + ":" +endpoint.getPort());
        }
    }
}
