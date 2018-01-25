package com.clustertest;

import com.clustertest.client.ClusterClient;
import com.clustertest.cluster.InstanceRegistry;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class ClusterBootstrap {
    private ClusterContext clusterContext;

    public ClusterBootstrap(ClusterContext clusterContext) {
        this.clusterContext = clusterContext;
    }

    public void contextInitialized(ServletContext servletContext) {
        initClusterContext();
        servletContext.setAttribute(ClusterContext.class.getName(), clusterContext);
    }

    public void contextDestroyed(ServletContext servletContext) {
        servletContext.removeAttribute(ClusterContext.class.getName());
        destroyClusterContext();
    }

    private void initClusterContext() {
        clusterContext.initialize();

        ClusterClient clusterClient = clusterContext.getClusterClient();
        clusterClient.openForTraffic();
    }

    private void destroyClusterContext() {
        clusterContext.shutdown();
    }
}
