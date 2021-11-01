package com.netease.edu.sample.parameter;

/**
 * https://dev.yunxin.163.com/docs/product/IM%E5%8D%B3%E6%97%B6%E9%80%9A%E8%AE%AF/%E6%9C%8D%E5%8A%A1%E7%AB%AFAPI%E6%96%87%E6%A1%A3/%E6%B6%88%E6%81%AF%E6%8A%84%E9%80%81?#%E9%9F%B3%E8%A7%86%E9%A2%91/%E7%99%BD%E6%9D%BF%E6%97%B6%E9%95%BF%E6%B6%88%E6%81%AF%E6%8A%84%E9%80%81
 */
public class DurationCallbackParam {

    /**
     * 通道号
     */
    private String channelId;

    /**
     * 通道名称 -> 对应会议 chat_room_id ，更改后对应 meeting_unique_id
     */
    private String channelName;

    /**
     * 白板总时长
     */
    private String duration;

    /**
     * 白板开始时间
     */
    private String createtime;

    /**
     * 类型，DataTunnel 白板事件，其他还有 AUDIO,VEDIO
     */
    private String type;

    /**
     * 状态 SUCCESS 成功
     */
    private String status;

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
}
