package com.netease.edu.sample.parameter;

/**
 * https://dev.yunxin.163.com/docs/product/IM%E5%8D%B3%E6%97%B6%E9%80%9A%E8%AE%AF/%E6%9C%8D%E5%8A%A1%E7%AB%AFAPI%E6%96%87%E6%A1%A3/%E6%B6%88%E6%81%AF%E6%8A%84%E9%80%81?kw=%E5%BD%95%E5%88%B6&pg=1&pid=0#%E9%9F%B3%E8%A7%86%E9%A2%91/%E7%99%BD%E6%9D%BF%E6%96%87%E4%BB%B6%E4%B8%8B%E8%BD%BD%E4%BF%A1%E6%81%AF%E6%8A%84%E9%80%81
 */
public class WhiteboardDurationCallbackParam {

    private String channelId;

    private String channelName;

    private String duration;

    private String members;

    private String createtime;

    private String type;

    private String status;

    private String eventType;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
