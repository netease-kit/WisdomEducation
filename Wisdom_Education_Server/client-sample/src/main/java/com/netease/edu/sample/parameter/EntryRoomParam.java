package com.netease.edu.sample.parameter;

import java.util.Map;

public class EntryRoomParam {
    private String userName;
    private String role;
    private Map<String, Map<String, Object>> properties;
    private Map<String, Map<String, Object>> streams;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

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
