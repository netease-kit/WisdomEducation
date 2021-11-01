package com.netease.edu.sample.pojo;

import java.util.Map;

public class RoomDto {
    private String roomName;
    private String roomUuid;
    private String rtcCid;
    private Map<String, Map<String, Object>> properties;
    private Map<String, Map<String, Object>>  states;
    private RoomConfig config;

    public RoomDto() {
    }


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    public Map<String, Map<String, Object>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Map<String, Object>> properties) {
        this.properties = properties;
    }

    public Map<String, Map<String, Object>> getStates() {
        return states;
    }

    public void setStates(Map<String, Map<String, Object>> states) {
        this.states = states;
    }

    public String getRtcCid() {
        return rtcCid;
    }

    public void setRtcCid(String rtcCid) {
        this.rtcCid = rtcCid;
    }

    public RoomConfig getConfig() {
        return config;
    }

    public void setConfig(RoomConfig config) {
        this.config = config;
    }

}
