package com.clustertest.client;

import com.clustertest.cluster.InstanceManager;
import com.clustertest.cluster.InstanceRegistry;
import com.clustertest.cluster.Instances;
import com.clustertest.configuration.ClusterConfigurationProperties;
import com.clustertest.transport.HttpClient;
import com.clustertest.transport.HttpResponse;
import com.clustertest.transport.HttpStatus;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TimerTask;
import java.util.concurrent.*;

@Component
public class ClusterClient {
    private final ScheduledExecutorService schedulerService;
    private final ExecutorService heartbeatExecutor;
    private final ExecutorService cacheRefreshExecutor;
    private final ClusterClientTransport clusterClientTransport;

    private ClusterConfigurationProperties configProperties;

    private InstanceManager instanceManager;
    private InstanceRegistry instanceRegistry;

    public ClusterClient(InstanceManager instanceManager, InstanceRegistry instanceRegistry,
                         ClusterConfigurationProperties configProperties) {
        this.configProperties = configProperties;
        this.instanceManager = instanceManager;
        this.instanceRegistry = instanceRegistry;

        schedulerService = new ScheduledThreadPoolExecutor(3,
                new ThreadFactoryBuilder().setNameFormat("cluster-scheduler-%d").setDaemon(true).build());

        heartbeatExecutor = new ThreadPoolExecutor(0, 100, 0, TimeUnit.SECONDS, new SynchronousQueue(),
                new ThreadFactoryBuilder().setNameFormat("cluster-heartbeat-%d").setDaemon(true).build());
        cacheRefreshExecutor = new ThreadPoolExecutor(0, 100, 0, TimeUnit.SECONDS, new SynchronousQueue(),
                new ThreadFactoryBuilder().setNameFormat("cluster-cacherefresh-%d").setDaemon(true).build());

        clusterClientTransport = new ClusterClientTransport(configProperties);
    }

    public void startup() {
        fetchRegistry();
        initScheduledTasks();
    }

    public void openForTraffic() {
        // TODO change instance status to UP
    }

    public void shutdown() {
        System.out.println("====== shutdown()");

        schedulerService.shutdown();

        HttpClient registrationClient = clusterClientTransport.getRegistrationClient();
        registrationClient.cancel(instanceManager.getMyInstance());
    }

    private void fetchRegistry() {
        System.out.println("====== fetchRegistry()");
        int registrySyncRetries = configProperties.getRegistrySyncRetries();

        for (int i = 0; i < registrySyncRetries; i++) {
            try {
                HttpResponse<Instances> response = clusterClientTransport.getQueryClient().getInstances();
                instanceRegistry.update(response.getData().getInstances());
                System.out.println("fetch data: " + response.getData());
                break;
            } catch (Exception e) {
                System.out.println("failed to fetch instances: " + e.getMessage());
            }
        }
    }

    private void initScheduledTasks() {
        schedulerService.schedule(new TimerSupervisorTask(schedulerService, cacheRefreshExecutor,
                        10, TimeUnit.SECONDS, new CacheRefreshThread()),
                10, TimeUnit.SECONDS);
        schedulerService.schedule(new TimerSupervisorTask(schedulerService, heartbeatExecutor,
                        1, TimeUnit.SECONDS, new HeartbeatThread()),
                1, TimeUnit.SECONDS);
    }

    // send heartbeat
    private boolean renew() {
        System.out.println("====== renew()");
        HttpClient registrationClient = clusterClientTransport.getRegistrationClient();
        HttpResponse<Void> heartbeatResponse = registrationClient.heartbeat(instanceManager.getMyInstance());

        if (heartbeatResponse.getStatus() == HttpStatus.NOT_FOUND) {
            return register();
        }

        return heartbeatResponse.getStatus() == HttpStatus.OK;
    }

    private boolean register() {
        System.out.println("====== register()");
        HttpClient registrationClient = clusterClientTransport.getRegistrationClient();
        HttpResponse<Void> registerResponse = registrationClient.register(instanceManager.getMyInstance());
        return registerResponse.getStatus() == HttpStatus.OK;
    }

    // Master thread spawning children threads
    // - heartbeat
    // - sync data
    private static class TimerSupervisorTask extends TimerTask {
        private ScheduledExecutorService schedulerService;
        private ExecutorService executor;
        private final int timeout;
        private final TimeUnit timeUnit;
        private final Runnable task;

        public TimerSupervisorTask(ScheduledExecutorService schedulerService, ExecutorService executor,
                                   int timeout, TimeUnit timeUnit, Runnable task) {
            this.schedulerService = schedulerService;
            this.executor = executor;
            this.timeout = timeout;
            this.timeUnit = timeUnit;
            this.task = task;
        }

        @Override
        public void run() {
            Future<?> future = null;
            try {
                future = executor.submit(task);
                future.get(timeout, timeUnit);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.out.println(task + ": failed to fetch instances: " + e.getMessage());
            } finally {
                if (future != null)
                    future.cancel(true);

                if (!schedulerService.isShutdown()) {
                    schedulerService.schedule(this, timeout, timeUnit);
                }
            }
        }
    }

    private class CacheRefreshThread implements Runnable {
        @Override
        public void run() {
            fetchRegistry();
        }
    }

    private class HeartbeatThread implements Runnable {
        @Override
        public void run() {
            renew();
        }
    }
}