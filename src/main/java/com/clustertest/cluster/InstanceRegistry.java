package com.clustertest.cluster;

import com.clustertest.client.ClusterClient;
import com.clustertest.client.resolver.ClusterEndpoint;
import com.clustertest.configuration.ClusterConfigurationProperties;
import com.clustertest.transport.HttpClient;
import com.clustertest.transport.HttpResponse;
import org.apache.http.conn.util.InetAddressUtils;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holds data about peer instances.
 */
@Component
public class InstanceRegistry {
    private ClusterConfigurationProperties configProperties;

    /** keep list of all instances excluding self */
    private Set<Instance> registry = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public InstanceRegistry(ClusterConfigurationProperties configurationProperties) {
        this.configProperties = configurationProperties;
    }

    public void update(List<Instance> instances) {
        // TODO handle instance removal/down etc
        lock.readLock().lock();
        try {
            instances.stream().filter(instance -> !isSelf(instance))
                    .forEach(instance -> registry.add(instance));
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean isSelf(Instance instance) {
        if (configProperties.getInstance().getPort() != instance.getPort())
            return false;

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface =  networkInterfaces.nextElement();
                if (networkInterface.isUp()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress =  inetAddresses.nextElement();
                        if (Objects.equals(inetAddress.getHostAddress(), instance.getAddress()))
                            return true;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return true;
    }
}
