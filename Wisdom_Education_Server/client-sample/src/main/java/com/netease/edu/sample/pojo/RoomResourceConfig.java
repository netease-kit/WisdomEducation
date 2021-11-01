package com.netease.edu.sample.pojo;

import org.apache.commons.lang3.BooleanUtils;

public class RoomResourceConfig {
    private Boolean rtc;
    private Boolean chatroom;
    private Boolean live;
    private Boolean whiteboard;

    public RoomResourceConfig() {
    }
    public RoomResourceConfig(RoomResourceConfig roomResourceConfig) {
        if(roomResourceConfig != null){
            this.rtc = roomResourceConfig.getRtc();
            this.chatroom = roomResourceConfig.getChatroom();
            this.live = roomResourceConfig.getLive();
            this.whiteboard = roomResourceConfig.getWhiteboard();
        }
    }
    public Boolean getRtc() {
        return rtc;
    }

    public Boolean getChatroom() {
        return chatroom;
    }

    public Boolean getLive() {
        return live;
    }

    public Boolean getWhiteboard() {
        return whiteboard;
    }

    public void setRtc(Boolean rtc) {
        this.rtc = rtc;
    }

    public void setChatroom(Boolean chatroom) {
        this.chatroom = chatroom;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public void setWhiteboard(Boolean whiteboard) {
        this.whiteboard = whiteboard;
    }

    public boolean rtc(){
        return BooleanUtils.isNotFalse(rtc);
    }
    public boolean chatroom(){
        return BooleanUtils.isNotFalse(chatroom);
    }
    public boolean live(){
        return BooleanUtils.isTrue(live);
    }
    public boolean whiteboard(){
        return BooleanUtils.isNotFalse(whiteboard);
    }
}
