package com.netease.edu.sample.parameter;

/**
 * https://doc.yunxin.163.com/docs/jcyOTA0ODM/DUyOTg1NDI?platformId=50326#3%20%E5%BD%95%E5%88%B6%E6%96%87%E4%BB%B6%E4%B8%8B%E8%BD%BD%E4%BF%A1%E6%81%AF
 */
public class AudioVideoRecordFileInfoCallbackParam {

    private Integer eventType;

    private Data data;

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String vid;

        private String pieceIndex;

        private String filename;

        private String size;

        private String type;

        private Boolean mix;

        private Long uid;

        private String url;

        private String channelId;

        private String md5;

        private Long timestamp;

        private Boolean substream;

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public String getPieceIndex() {
            return pieceIndex;
        }

        public void setPieceIndex(String pieceIndex) {
            this.pieceIndex = pieceIndex;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
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

        public Boolean getMix() {
            return mix;
        }

        public void setMix(Boolean mix) {
            this.mix = mix;
        }

        public Long getUid() {
            return uid;
        }

        public void setUid(Long uid) {
            this.uid = uid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Boolean getSubstream() {
            return substream;
        }

        public void setSubstream(Boolean substream) {
            this.substream = substream;
        }
    }
}
