/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.constant;

/**
 * Player error codes and cuases
 * <p>
 *
 * @author netease
 */

public interface CauseCode {

    /**
     * Video parser error
     */
    int CODE_VIDEO_PARSER_ERROR = -10001;

    /**
     * Video preparing timeout. If the connection times out, the player state changes from PREPARING to ERROR.
     */
    int CODE_VIDEO_PREPARING_TIMEOUT = -10002;

    /**
     * Video Paused manually
     */
    int CODE_VIDEO_PAUSED_BY_MANUAL = -10101;

    /**
     * Video stopped manually(released)
     */
    int CODE_VIDEO_STOPPED_BY_MANUAL = -10102;

    /**
     * Video stopped due to network error(the player is not released but reset)
     */
    int CODE_VIDEO_STOPPED_AS_NET_UNAVAILABLE = -10103;

    /**
     * Video stopped due to playback completion
     */
    int CODE_VIDEO_STOPPED_AS_ON_COMPLETION = -10104;

    /**
     * HTTP connection error. For more information, see {@link com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_HTTP_CONNECT_ERROR = -1001;
    /**
     * RTMP connection error. For more information, see {@link com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_RTMP_CONNECT_ERROR = -1002;
    /**
     * Parsing failed. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_STREAM_PARSE_ERROR = -1003;
    /**
     * Buffering error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_BUFFERING_ERROR = -1004;
    /**
     * Audio initialization error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_AUDIO_OPEN_ERROR = -2001;
    /**
     * Video initialization error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_VIDEO_OPEN_ERROR = -2002;
    /**
     * No audio stream. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_STREAM_IS_NULL = -3001;
    /**
     * Audio decoding error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_AUDIO_DECODE_ERROR = -4001;
    /**
     * Video decoding error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_VIDEO_DECODE_ERROR = -4002;
    /**
     * Audio rendering error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_AUDIO_RENDER_ERROR = -5001;
    /**
     * Video rendering error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_VIDEO_RENDER_ERROR = -5002;
    /**
     * Unknown error. For more information, see {@link  com.netease.neliveplayer.sdk.constant.NEErrorType}
     */
    int CODE_UNKNOWN_ERROR = -10000;
}
