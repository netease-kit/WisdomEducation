package com.netease.edu.sample.parameter;

import java.util.Map;

public class DeleteMemberParam {
    private Map<String, Map<String, Object>> properties;
    private Map<String, Map<String, Object>> streams;

    public Map<String, Map<String, Object>> getStreams() {
        return streams;
    }

    public void setStreams(Map<String, Map<String, Object>> streams) {
        this.streams = streams;
    }

    public Map<String, Map<String, Object>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Map<String, Object>> properties) {
        this.properties = properties;
    }
}
