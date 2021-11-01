package com.netease.edu.sample.parameter;


import com.netease.edu.sample.pojo.RoomConfig;

import java.util.Map;

public class PutRoomParam {
    private String roomName;
    private Long configId;
    private RoomConfig config;
    private Map<String, Map<String, Object>> properties;

    //{roomName: "i", roleConfig: {host: {limit: 1}, broadcaster: {limit: 16}}}

    public Map<String, Map<String, Object>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Map<String, Object>> properties) {
        this.properties = properties;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public RoomConfig getConfig() {
        return config;
    }

    public void setConfig(RoomConfig config) {
        this.config = config;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }
}
