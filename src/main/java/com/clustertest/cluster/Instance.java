package com.clustertest.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Objects;

@JsonRootName("instance")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {
    private String address;
    private String hostname;
    private int port;
    private String name;

    public Instance() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instance instance = (Instance) o;
        return port == instance.port &&
                Objects.equals(address, instance.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
