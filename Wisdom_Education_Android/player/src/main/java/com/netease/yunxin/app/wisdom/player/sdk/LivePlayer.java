/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk;

import android.graphics.Bitmap;

import com.netease.neliveplayer.proxy.gslb.NEGslbResultListener;
import com.netease.neliveplayer.proxy.gslb.NEGslbServerModel;
import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.model.NEAudioPcmConfig;
import com.netease.neliveplayer.sdk.model.NEAudioTrackInfo;
import com.netease.neliveplayer.sdk.model.NEMediaRealTimeInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.AutoRetryConfig;
import com.netease.yunxin.app.wisdom.player.sdk.model.StateInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoScaleMode;
import com.netease.yunxin.app.wisdom.player.view.IRenderView;

/**
 * Interfaces of the live streaming player powered by CommsEase
 * Live streaming related APIs based on the SDK
 * <p>
 *
 * @author netease
 */

public abstract class LivePlayer {


    public enum STATE {

        /**
         * The initial state of the player
         */
        IDLE,

        /**
         * The player is preparing
         */
        PREPARING,

        /**
         * The player is ready
         */
        PREPARED,

        /**
         * The player is playing video files
         */
        PLAYING,

        /**
         * [VOD only] The player is paused
         */
        PAUSED,

        /**
         * An error occurred in playback and the player stops
         */
        ERROR,

        /**
         * The player stops (released) and has the same state as IDLE
         */
        STOPPED,
    }

    /**
     * Register or unregister the observer for player states or events
     *
     * @param observer The player observer
     * @param register true: register the observer; false: unregister the observer
     */
    public abstract void registerPlayerObserver(LivePlayerObserver observer, boolean register);

    /**
     * Register the callback for the event of getting the audio PCM data and audio parameters. The data must be received by OnAudioFrameFilterListener
     *
     * @param config   The configuration parameters for audio PCM data callback
     * @param listener The callback result
     * @return A value of non-zero indicates failure
     */
    public abstract int registerAudioFrameFilterListener(NEAudioPcmConfig config, NELivePlayer.OnAudioFrameFilterListener listener, boolean register);

    /**
     * Register the callback for the event of getting the video frame data. The data must be received by OnVideoFrameFilterListener
     * Support only software decoding
     *
     * @param format   The video data format, such as NELP_YUV420
     * @param listener The callback result
     * @return A value of non-zero indicates failure
     */
    public abstract int registerVideoFrameFilterListener(int format, NELivePlayer.OnVideoFrameFilterListener listener, boolean register);


    /**
     * Register the current position callback
     * The method is called after the start method is called
     *
     * @param interval The callback interval in milliseconds
     * @param listener The callback listener
     * @param register true: register the listener; false: unregister the listener
     */
    public abstract void registerPlayerCurrentPositionListener(final long interval,
                                                               NELivePlayer.OnCurrentPositionListener listener,
                                                               boolean register);


    /**
     * Register the real-time timestamp callback
     * The method is called after the start method is called
     *
     * @param interval The callback interval in milliseconds
     * @param listener The callback listener
     * @param register true: register the listener; false: unregister the listener
     */
    public abstract void registerPlayerCurrentRealTimestampListener(final long interval, NELivePlayer.OnCurrentRealTimeListener listener, boolean register);


    /**
     * Register the player current sync timestamp callback
     * The method is called after the start method is called
     *
     * @param interval The callback interval in milliseconds
     * @param listener The callback listener
     * @param register true: register the listener; false: unregister the listener
     */
    public abstract void registerPlayerCurrentSyncTimestampListener(final long interval, NELivePlayer.OnCurrentSyncTimestampListener listener, boolean register);

    /**
     * register the player current sync content callback
     * The method is called after the start method is called
     *
     * @param listener The callback listener
     * @param register true: register the listener; false: unregister the listener
     */
    public abstract void registerPlayerCurrentSyncContentListener(NELivePlayer.OnCurrentSyncContentListener listener, boolean register);

    /**
     * Sync the timestamp between the current player with the target player
     * Before the feature is enabled, the two streams must have the consistent timestamp.
     * The current playback position is synchronized with the target player based on the timestamp
     * <p>
     * The method is called before the prepare operation is complete
     *
     * @param player the target player instance
     */
    public abstract void syncClockTo(LivePlayer player);

    /**
     * Asynchronous initialization and auto start
     */
    public abstract void start();

    /**
     * Set up SurfaceView or TextureView for rendering
     * The method can be called before or after the player starts
     *
     * @param renderView     SurfaceView or TextureView
     * @param videoScaleMode The scale mode. The default value is proportional scaling
     */
    public abstract void setupRenderView(IRenderView renderView, VideoScaleMode videoScaleMode);

    /**
     * Set the scale mode for View
     * The method applies after View takes effect.
     *
     * @param videoScaleMode The scale mode
     */
    public abstract void setVideoScaleMode(VideoScaleMode videoScaleMode);

    /**
     * The method must be called when onStop() of Activity is invoked
     *
     * @param isLive Check whether the activity is live streaming
     */
    public abstract void onActivityStop(boolean isLive);

    /**
     * The method must be called when onResume() of Activity is invokedActivity
     *
     * @param isLive Check whether the activity is live streaming
     */
    public abstract void onActivityResume(boolean isLive);

    /**
     * hide View
     */
    public abstract void hideView();

    /**
     * display View
     */
    public abstract void showView();

    /**
     * Check whether playback is running
     *
     * @return check whether playback is running. true: playing, false: not playing
     */
    public abstract boolean isPlaying();

    /**
     * Mute or unmute
     *
     * @param mute true: muted，false: unmuted
     */
    public abstract void setMute(boolean mute);

    /**
     * Mirroring
     *
     * @param isMirror Whether to create a mirror. true: create a mirror，false: do not create a mirror
     */
    public abstract void setMirror(boolean isMirror);

    /**
     * Set volume. Value range: 0.0 to 1.0. 0.0: muted. 1.0: the maximum volume
     *
     * @param volume
     */
    public abstract void setVolume(float volume);

    /**
     * Release the player. No observers will be destroyed
     * If a player is released, you must initialize the player again by calling asyncInit.
     */
    public abstract void stop();

    /**
     * Get the current player state.
     *
     * @return The current state of the player: state {@link STATE} and reason {@link com.netease.yunxin.app.wisdom.player.sdk.constant.CauseCode}
     */
    public abstract StateInfo getCurrentState();

    /**
     * Get the timestamp of current position in milliseconds. The method is called when the onPrepare operation is complete and sends a notification
     *
     * @return The timestamp of current position -1: failure
     */
    public abstract long getCurrentPosition();

    /**
     * Get the real-time timestamp of the live stream
     *
     * @return The timestamp compared to the start time of the live stream
     */
    public abstract long getCurrentSyncTimestamp();

    /**
     * Screenshot is supported by software decoding. hardware decoding does not support screenshot. When the onPrepare operation is complete and sends a notification
     *
     * @return If screen capture fails, a value of null is returned. the bitmap object is returned if the operation is successful.
     */
    public abstract Bitmap getSnapshot();

    /**
     * The interface is not invoked when the player starts playback for the first time. The interface is called when the playback is complete or change to the next video and the player loads the next video file
     * The interface is called by the player SDK
     *
     * @param url The playback URL
     */
    public abstract void switchContentUrl(String url);

    /**
     * Switch the URLs of video fules
     * The method is called when the player is reset or initialized again
     *
     * @param path The URL of the video file to be played
     */
    public abstract void switchContentPath(String path);

    /**
     * Query the scheduling address based on URLs
     *
     * @param url
     * @param listener The listner
     */
    public abstract void queryPreloadUrlResult(String url, NEGslbResultListener listener);

    /**
     * Switch URLs of video files
     * <p>
     * Get and set the URL.
     *
     * @param session The identifier returned in the response
     * @param result  The URL to be set
     */
    public abstract void switchWithGslbResult(Object session, NEGslbServerModel result);

    /**
     * Get the current playback URL
     *
     * @return
     */
    public abstract NEGslbServerModel getCurrentServerModel();


    /**
     * Get the information about the current audio track. The method is called after the prepare operation is complete
     *
     * @return The array that contains audio track information or null
     */
    public abstract NEAudioTrackInfo[] getAudioTracksInfo();

    /**
     * Select the index of the selected audio track. The method is called after the prepare operation is complete
     *
     * @return Index of the current audio track or -1
     */
    public abstract int getSelectedAudioTrack();

    /**
     * Switch audio tracks. The method is called after the prepare operation is complete
     *
     * @param index The index of the audio track to switch to. The number of audio tracks is determined by the array retuned by getSelectedAudioTrack.
     * @return A value of 0 indicates success. Otherwise, the operation failed.
     */
    public abstract int setSelectedAudioTrack(int index);

    /**
     * Configure auto retries
     *
     * @param config Configuration parameter
     */
    public abstract void setAutoRetryConfig(AutoRetryConfig config);

    /**
     * Get the real-time playback statistics
     *
     * @return playback statistics
     */
    public abstract NEMediaRealTimeInfo getMediaRealTimeInfo();

}
