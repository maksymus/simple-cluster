package com.clustertest.configuration;

import com.clustertest.ClusterBootstrap;
import com.clustertest.ClusterContext;
import com.clustertest.client.ClusterClient;
import com.clustertest.cluster.InstanceManager;
import com.clustertest.cluster.InstanceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ClusterInitializerConfiguration.class)
public class ClusterConfiguration {

    @Bean
    public ClusterConfigurationProperties createConfigurationProperties() {
        return new ClusterConfigurationProperties();
    }
}
