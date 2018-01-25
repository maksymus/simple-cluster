package com.clustertest.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Configuration properties.
 */
@ConfigurationProperties(prefix = "cluster")
public class ClusterConfigurationProperties {
    private int registrySyncRetries = 0;

    /** cluster nodes coma separated */
    private String nodes;

    private Instance instance;

    public int getRegistrySyncRetries() {
        return registrySyncRetries;
    }

    public String getNodes() {
        return nodes;
    }

    public void setRegistrySyncRetries(int registrySyncRetries) {
        this.registrySyncRetries = registrySyncRetries;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    @Override
    public String toString() {
        return "ClusterConfigurationProperties{" +
                "registrySyncRetries=" + registrySyncRetries +
                ", nodes='" + nodes + '\'' +
                ", instance=" + instance +
                '}';
    }

    public static class Instance {
        private String name;
        private int port;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return "Instance{" +
                    "name='" + name + '\'' +
                    ", port=" + port +
                    '}';
        }
    }
}
