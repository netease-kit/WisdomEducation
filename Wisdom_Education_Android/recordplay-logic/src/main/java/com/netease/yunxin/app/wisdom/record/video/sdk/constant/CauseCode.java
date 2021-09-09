/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.sdk.constant;

/**
 * 播放器错误码/导致播放器停止的原因
 * <p>
 *
 * @author netease
 */

public interface CauseCode {

    /**
     * 视频解析出错
     */
    int CODE_VIDEO_PARSER_ERROR = -10001;

    /**
     * 视频准备超时，可能视频源无法连接成功。超时后播放器状态会从PREPARING状态切到ERROR状态。
     */
    int CODE_VIDEO_PREPARING_TIMEOUT = -10002;

    /**
     * 视频被用户手动暂停
     */
    int CODE_VIDEO_PAUSED_BY_MANUAL = -10101;

    /**
     * 视频被用户手动停止(销毁)
     */
    int CODE_VIDEO_STOPPED_BY_MANUAL = -10102;

    /**
     * 系统网络断开导致视频停止播放(不销毁仅重置播放器)
     */
    int CODE_VIDEO_STOPPED_AS_NET_UNAVAILABLE = -10103;

    /**
     * 视频因为播放完成而停止
     */
    int CODE_VIDEO_STOPPED_AS_ON_COMPLETION = -10104;

    /**
     * HTTP连接失败，参考{@link com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_HTTP_CONNECT_ERROR = -1001;
    /**
     * RTMP连接失败，参考{@link com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_RTMP_CONNECT_ERROR = -1002;
    /**
     * 解析失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_STREAM_PARSE_ERROR = -1003;
    /**
     * 缓冲失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_BUFFERING_ERROR = -1004;
    /**
     * 音频相关操作初始化失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_AUDIO_OPEN_ERROR = -2001;
    /**
     * 视频相关操作初始化失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_VIDEO_OPEN_ERROR = -2002;
    /**
     * 没有音视频流，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_STREAM_IS_NULL = -3001;
    /**
     * 音频解码失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_AUDIO_DECODE_ERROR = -4001;
    /**
     * 视频解码失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_VIDEO_DECODE_ERROR = -4002;
    /**
     * 音频播放失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_AUDIO_RENDER_ERROR = -5001;
    /**
     * 视频播放失败，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_VIDEO_RENDER_ERROR = -5002;
    /**
     * 未知错误，参考{@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_UNKNOWN_ERROR = -10000;
}
