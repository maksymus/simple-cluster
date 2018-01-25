package com.clustertest.cluster;

import com.clustertest.configuration.ClusterConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Component
public class InstanceManager {
    private Instance myInstance;
    private ClusterConfigurationProperties configProperties;

    public InstanceManager(ClusterConfigurationProperties configProperties) {
        this.configProperties = configProperties;
        this.myInstance = init(this.configProperties);
    }

    private Instance init(ClusterConfigurationProperties configProperties) {
        String name = configProperties.getInstance().getName();
        int port = configProperties.getInstance().getPort();

        if (name == null || name.isEmpty())
            throw new RuntimeException("init failure: name property is missing");

        Instance instance = new Instance();
        instance.setName(name);
        instance.setPort(port);

        InetAddress inetAddress = getInetAddress();
        instance.setAddress(inetAddress.getHostAddress());
        instance.setHostname(inetAddress.getHostName());

        return instance;
    }

    public Instance getMyInstance() {
        return myInstance;
    }

    private InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface =  networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress =  inetAddresses.nextElement();
                        if (inetAddress instanceof Inet4Address)
                            return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Failed to get inet address", e);
        }

        throw new RuntimeException("Failed to get inet address: no address found");
    }
}
