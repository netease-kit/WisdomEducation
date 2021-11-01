package com.netease.edu.sample.pojo;

public class UserDto {

    //user: {userName: "1", userUuid: "9f88b977dfc8ce911fa808605f52a9ba", role: "host", muteChat: 0,…}
    private String userName;
    private String userUuid;
    private String userToken;
    private String imToken;
    private Long rtcUid;
    private String imKey;
    private String rtcKey;

    public UserDto() {
    }

    public UserDto(String userUuid, String userToken, String imToken, String imKey, String rtcKey) {
        this.userUuid = userUuid;
        this.imToken = imToken;
        this.imKey = imKey;
        this.rtcKey = rtcKey;
        this.userToken = userToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public Long getRtcUid() {
        return rtcUid;
    }

    public void setRtcUid(Long rtcUid) {
        this.rtcUid = rtcUid;
    }

    public String getImKey() {
        return imKey;
    }

    public void setImKey(String imKey) {
        this.imKey = imKey;
    }

    public String getRtcKey() {
        return rtcKey;
    }

    public void setRtcKey(String rtcKey) {
        this.rtcKey = rtcKey;
    }
}
