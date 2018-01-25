package com.clustertest.client.resolver;

import java.net.MalformedURLException;
import java.net.URL;

public class ClusterEndpoint {
    private URL url;
    String serviceUrl;
    private String address;
    private int port;

    public ClusterEndpoint(String serviceUrl)  {
        try {
            this.url = new URL(serviceUrl);
            this.serviceUrl = serviceUrl;
            this.address = url.getHost();
            this.port = url.getPort();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid service url: " + serviceUrl, e);
        }
    }

    public URL getUrl() {
        return url;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }
}
