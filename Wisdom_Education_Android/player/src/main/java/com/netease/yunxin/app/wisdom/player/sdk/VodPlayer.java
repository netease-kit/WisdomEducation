/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk;

import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.yunxin.app.wisdom.player.sdk.model.DataSourceConfig;


/**
 * VOD player interfaces
 * VOD-related interfaces based on the player SDK
 * <p>
 *
 * @author netease
 */

public abstract class VodPlayer extends LivePlayer {


    /**
     * [VOD only] Get the video duration. The interface can be called after the notification sent by onPrepared callback is received
     *
     * @return The video duration in milliseconds
     */
    public abstract long getDuration();

    /**
     * [VOD only] Get the current position. The interface can be called after the notification sent by onPrepared callback is received
     *
     * @return Get the current position in milliseconds
     */
    public abstract long getCurrentPosition();

    /**
     * [VOD only] Get the current playback position in percentage. The interface can be called after the notification sent by onPrepared callback is received
     *
     * @return The current playback position in percentage
     */
    public abstract float getCurrentPositionPercent();

    /**
     * [VOD only] Get the cached playback position. The interface can be called after the notification sent by onPrepared callback is received
     *
     * @return The cached playback position in milliseconds
     */
    public abstract long getCachedPosition();

    /**
     * [VOD only] Set the playback position. The interface can be called after the notification sent by onPrepared callback is received
     *
     * @param position Specify the position
     */
    public abstract void seekTo(long position);

    /**
     * [VOD only] Set the playback speed. The default value is 1.0. The value range: 0.5 to 2.0.
     *
     * @param speed The playback speed. Frequently used values: 0.5, 1.0, 1.3, 1.5, and2.0
     */
    public abstract void setPlaybackSpeed(float speed);

    /**
     * [VOD only] Pause the current playback. Resumes playback by calling start.
     */
    public abstract void pause();


    /**
     * [VOD only] Set the buffer size. The interface is invoked after setBufferStrategy is called. The value must be specified agian if the player is reset
     * NELPANTIJITTER is the anti-jitter mode. In this mode, you can set the buffer size. The default value is 150M
     * This interface is VOD only. The value range: 5M to 100M
     *
     * @param size The buffer size in bytes. To set the value to 1M, enter 1*1024*1024
     */
    public abstract void setBufferSize(int size);

    /**
     * [VOD only] Set the number of loops
     *
     * @param loopCount 0: no loop；-1: infinite loops；1: one loop，2: two loops, and the like
     */
    public abstract void setLoopCount(int loopCount);

    /**
     * [VOD only] Set the number of loops
     *
     * @return The number of loops
     */
    public abstract int getLoopCount();

    /**
     * [VOD only] Set the local subtitle file
     * Only the SRT format is supported. SRT files supports only UTF-8 encoding.
     * To close the subtitle, set the value to null.
     * The special characters in the subtitle must be converted to valid UTF-8 characters in the application layer
     *
     * @param path The local subtitle path
     */
    public abstract void setSubtitleFile(String path);

    /**
     * [VOD only] Register a listener for displaying subtitles
     * The listener can be active after the subtitle path is specified. For more information, see {@link VodPlayer#setSubtitleFile}
     *
     * @param listener The listner for displaying subtitles
     * @param register true: register the listener; false: unregister the listener
     */
    public abstract void registerPlayerSubtitleListener(NELivePlayer.OnSubtitleListener listener, boolean register);

    /**
     * [VOD only] The interface is not invoked when the player starts playback for the first time. The interface is called when the playback is complete or change to the next video and the player loads the next video file
     *
     * @param url    Playback URL
     * @param config Configuration (local cache options and decryption settings）
     */
    public abstract void switchContentUrl(String url, DataSourceConfig config);


}
