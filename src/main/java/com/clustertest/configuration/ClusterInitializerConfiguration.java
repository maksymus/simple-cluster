package com.clustertest.configuration;

import com.clustertest.ClusterBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ClusterInitializerConfiguration implements ServletContextAware, SmartLifecycle, Ordered {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClusterBootstrap clusterBootstrap;

    private ServletContext servletContext;

    private ExecutorService executorService;
    private boolean running = false;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void start() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            clusterBootstrap.contextInitialized(servletContext);
            running = true;
        });
    }

    @Override
    public void stop() {
        System.out.println("!!!!! stopping");

        running = false;
        clusterBootstrap.contextDestroyed(servletContext);
        executorService.shutdown();
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
