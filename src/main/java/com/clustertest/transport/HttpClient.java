package com.clustertest.transport;

import com.clustertest.cluster.Instance;
import com.clustertest.cluster.Instances;

import java.util.List;

public interface HttpClient {
    HttpResponse<Void> register(Instance instance);
    HttpResponse<Void> cancel(Instance instance);
    HttpResponse<Void> heartbeat(Instance instance);
    HttpResponse<Instances> getInstances();
}
