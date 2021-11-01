package com.netease.edu.sample.parameter;

/**
 * https://dev.yunxin.163.com/docs/product/IM%E5%8D%B3%E6%97%B6%E9%80%9A%E8%AE%AF/%E6%9C%8D%E5%8A%A1%E7%AB%AFAPI%E6%96%87%E6%A1%A3/%E6%B6%88%E6%81%AF%E6%8A%84%E9%80%81?#%E9%9F%B3%E8%A7%86%E9%A2%91/%E7%99%BD%E6%9D%BF%E6%96%87%E4%BB%B6%E4%B8%8B%E8%BD%BD%E4%BF%A1%E6%81%AF%E6%8A%84%E9%80%81
 */
public class RecordFileInfoCallbackParam {

    /**
     * 是否是此通通话的发起者，若是则为true，若不是则没有此字段，可转为Boolean值
     */
    private Boolean caller;

    /**
     * 通道号，可转为Long值
     */
    private String channelid;

    /**
     * 文件名，直接存储，混合录制文件filename带有"-mix"标记
     */
    private String filename;

    /**
     * 文件的md5值
     */
    private String md5;

    /**
     * 文件大小，单位为字符，可转为Long值
     */
    private String size;

    /**
     * 文件的类型（扩展名），包括：实时音频录制文件(aac)、白板录制文件(gz)、实时视频录制文件(mp4)、互动直播视频录制文件(flv)
     */
    private String type;

    /**
     * 文件的下载地址
     */
    private String url;

    /**
     * 用户帐号，若该文件为混合录制文件，则该字段为"0"
     */
    private String user;

    /**
     * 是否为混合录制文件，true：混合录制文件；false：单人录制文件
     */
    private Boolean mix;

    /**
     * 点播文件id，注意白板录制文件(gz)无此字段。通过该参数可以调用点播接口查询相关信息。
     */
    private String vid;

    /**
     * 录制文件的切片索引，如果单通通话录制时长超过切片时长，则录制文件会被且被切割成多个文件
     */
    private String pieceindex;

    /**
     * 文件生成的系统时间
     */
    private String timestamp;

    public Boolean getCaller() {
        return caller;
    }

    public void setCaller(Boolean caller) {
        this.caller = caller;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getMix() {
        return mix;
    }

    public void setMix(Boolean mix) {
        this.mix = mix;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getPieceindex() {
        return pieceindex;
    }

    public void setPieceindex(String pieceindex) {
        this.pieceindex = pieceindex;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
