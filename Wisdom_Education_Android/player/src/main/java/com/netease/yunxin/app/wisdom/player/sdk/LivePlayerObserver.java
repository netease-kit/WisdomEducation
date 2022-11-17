/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk;


import com.netease.yunxin.app.wisdom.player.sdk.model.MediaInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.StateInfo;

/**
 * Observers for live streaming player states and events
 * Live streaming player states and events based on the player SDK
 * <p>
 *
 * @author netease
 */

public interface LivePlayerObserver {

    /**
     * [Important] The player instance is being initialized
     * Waiting animation can be added at the moment
     */
    void onPreparing();

    /**
     * [Important]The player is initialized and ready for playback
     * Waiting animation can be removed at the moment
     */
    void onPrepared(MediaInfo mediaInfo);

    /**
     * [Important]An error occurred while playing
     * Developers can end the playback, prompt the error message and restart the playback
     *
     * @param code  Error code {@link com.netease.neliveplayer.sdk.constant.NEErrorType}
     *              code=-9999 indicates failure to parse the video stream. The audio stream is playing without video images. Developers can handle the error with operations such as exit or restart the playback.
     * @param extra extra information about the error
     */
    void onError(int code, int extra);

    /**
     * [Important]The first video frame. If the first frame is loaded, the playback starts
     */
    void onFirstVideoRendered();

    /**
     * The first audio frame
     */
    void onFirstAudioRendered();

    /**
     * Video buffering starts
     */
    void onBufferingStart();

    /**
     * Video buffering ends
     */
    void onBufferingEnd();

    /**
     * Video buffering progress
     */
    void onBuffering(int percent);

    /**
     * Check whether the hardware decoding is enabled
     * The value parameter indicates whether hardware decoding is enabled. 1: hardware is enabled, other values: software decoding is enabled.
     */
    void onVideoDecoderOpen(int value);

    /**
     * The player state callback
     *
     * @param stateInfo The current state of the player and the reason
     */
    void onStateChanged(StateInfo stateInfo);

    /**
     * HTTP response information
     *
     * @param code   Status code
     * @param header Header information
     */
    void onHttpResponseInfo(int code, String header);

}
