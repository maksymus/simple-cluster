package com.clustertest.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.ArrayList;
import java.util.List;

@JsonRootName("instances")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instances {

    private List<Instance> instances = new ArrayList<>();

    public List<Instance> getInstances() {
        return instances;
    }

    @Override
    public String toString() {
        return "Instances{" +
                "instances=" + instances +
                '}';
    }
}
