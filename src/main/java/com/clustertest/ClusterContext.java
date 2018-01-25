package com.clustertest;

import com.clustertest.client.ClusterClient;
import com.clustertest.cluster.InstanceRegistry;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

@Component
public class ClusterContext {
    private final ClusterClient clusterClient;

    public ClusterContext(ClusterClient clusterClient) {
        this.clusterClient = clusterClient;
    }

    public ClusterClient getClusterClient() {
        return clusterClient;
    }

    void initialize() {
        clusterClient.startup();
    }

    void shutdown() {
        clusterClient.shutdown();
    }
}
