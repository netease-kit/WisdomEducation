package com.netease.edu.sample.parameter;

public class WhiteboardG2RecordFileInfoCallbackParam {

    private String eventType;

    private RecordFileData data;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public RecordFileData getData() {
        return data;
    }

    public void setData(RecordFileData data) {
        this.data = data;
    }

    public static class RecordFileData {

        /**
         * 点播文件id，注意白板录制文件(gz)无此字段。通过该参数可以调用点播接口查询相关信息。
         */
        private String vid;


        /**
         * 文件名，直接存储，混合录制文件filename带有"-mix"标记
         */
        private String fileName;

        private Long recordTime;

        private Long size;

        private String channelName;

        private String channelId;

        private String url;

        private String md5;

        private Long timestamp;

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getRecordTime() {
            return recordTime;
        }

        public void setRecordTime(Long recordTime) {
            this.recordTime = recordTime;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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
    }
}
