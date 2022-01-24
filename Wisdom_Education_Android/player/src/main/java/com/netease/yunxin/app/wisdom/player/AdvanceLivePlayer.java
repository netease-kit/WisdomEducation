/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.player;

import android.content.Context;

import com.netease.neliveplayer.sdk.NEMediaDataSource;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoOptions;
import com.netease.yunxin.kit.alog.ALog;


/**
 * @author netease
 * <p>
 * Player advanced extension class that wrapps the basic extension classes for switching between foreground and background:
 * <p>
 * Note:
 * The player is reset after the player persists for more than 3 minutes and switched back to foreground
 */

public class AdvanceLivePlayer extends BaseLivePlayer {

    /// constant
    // If the app does not enable continous playback in the background using service, set the value to 3*60*1000. The player is reset after the player persists for more than 3 minutes and switched back to foreground
    // If the app enables continous playback in the background using service, set the value to 30 * 60 * 1000 for the longest possible playback. The player is reset after the player persists for more than 30 minutes and switched back to foreground
    private static final long BACKGROUND_RESET_TIME = 30 * 60 * 1000;

    /// The foreground and background states and reconnection after the network is disconnected
    private boolean foreground = true; // Check whether app is running in the foreground(default)

    private long backgroundTime; // The time when the app is switched to the background

    AdvanceLivePlayer(Context context, String videoPath, VideoOptions options) {
        super(context, videoPath, options);
    }

    AdvanceLivePlayer(Context context, NEMediaDataSource mediaDataSource, VideoOptions options) {
        super(context, mediaDataSource, options);
    }

    /**
     * ***************************** abstract impl *************************
     */

    @Override
    void onChildInit() {
    }

    @Override
    void onChildDestroy() {
    }

    /**
     * ***************************** player interface *************************
     */

    @Override
    public void onActivityStop(boolean isLive) {
        super.onActivityStop(isLive);
        ALog.i("activity on stop");
        foreground = false; // Switched to background
        backgroundTime = System.currentTimeMillis();
        if (isLive) { //Live streaming
            if (options.hardwareDecode) {
                // If hardware decoding is enabled, the live streaming stops if the app is switched to background and resumed to start streaming after the app is switched back to foreground.
                ALog.i("force reset live player, as app use hardwareDecode! ");
                resetPlayer();
            } else {
                if (options.isPlayLongTimeBackground) {
                    ALog.i("no reset live player, as app use softwareDecode and isPlayLongTimeBackground is true! ");
                    //If software decoding is enabled, set isPlayLongTimeBackground to true，live streaming continues after the app is switched to background. The application layer uses the service to maintain continous playback
                } else {
                    ALog.i("force reset live player, as app use softwareDecode and isPlayLongTimeBackground is false! ");
                    //If software decoding is enabled, set isPlayLongTimeBackground to false, the streaming stops and the player is reset after the app is switched to background
                    resetPlayer();
                }
            }

        } else {  //VOD
            if (options.isPlayLongTimeBackground) {
                //If If hardware decoding is enabled, the player stops pulling streams if the app is switched to background and resumed to start pulling stream after the app is switched back to foreground.
                if (options.hardwareDecode) {
                    //If hardware decoding is enabled, set isPlayLongTimeBackground to true，the player stops pulling streams if the app is switched to background and resumed to start pulling stream after the app is switched back to foreground.
                    ALog.i("force reset vod player, as app use hardwareDecode and isPlayLongTimeBackground is true! ");
                    //Compatibility issues may occur if playback continues using hardware decoding
                    // If SurfaceView is used as display control, the following snippet is required, and background playback is not supported
                    // If TextureView is used as display control:
                    // a. add the following snippet, reset the player if hardware decoding is enabled, and background playback is not allowed
                    // b. do not use the following snippet and the player is not reset but implement background playback if hardware decoding is enabled. In this case, some devices may incur compatibility issues becasue surface in TextureView behaves differently in different mobile phones.
                    //                    savePlayPosition();
                    //                    resetPlayer();
                } else {
                    ALog.i("no reset vod player, as app use softwareDecode and isPlayLongTimeBackground is true! ");
                    //If software decoding is enabled, set isPlayLongTimeBackground to true，VOD streaming continues after the app is switched to background. The application layer uses the service to maintain continous playback
                }
            } else {
                //If software decoding is enabled, set isPlayLongTimeBackground to false, the streaming stops when the app is switched to background and resumes after the app is switched to background
                ALog.i("pause vod player, as app use softwareDecode or hardwareDecode and isPlayLongTimeBackground is false! ");
                pause();
            }
        }
    }

    @Override
    public void onActivityResume(boolean isLive) {
        super.onActivityResume(isLive);
        ALog.i("activity on resume");
        if (foreground) {
            ALog.i("activity on resume foreground is already true");
            return;
        }
        foreground = true; // Swiched back to foreground
        if (player == null) {
            return;
        }
        // Consider cases where the player is reset
        if (!hasReset.get()) {
            final STATE state = getCurrentState().getState();
            if (options.isPlayLongTimeBackground &&
                System.currentTimeMillis() - backgroundTime >= BACKGROUND_RESET_TIME) {
                // If the app persists in the background for a time longer than BACKGROUND_RESET_TIME and the player is not reset, reset the player. Case: the app persists for a time longer than the specified duration in the background, playback stops in some cases without notifications from callbacks, in this case, the player must be reset and pull streams again.
                ALog.i("force reset player, as app on background for a long time! ");
                savePlayerState();
                resetPlayer();
            } else if (state == STATE.PLAYING && !player.isPlaying()) {
                // The current state is inconsistent with the player state, reset the player immediately.
                ALog.i("force reset player, as current state is PLAYING, but player engine is not playing!");
                savePlayerState();
                resetPlayer();
            }
        }
        // Resumes to load video streams
        recoverPlayer();
    }


    /**
     * *********************************** core *******************************
     */

    private void recoverPlayer() {
        if (player == null) {
            return;
        }
        if (!hasReset.get() && getCurrentState().getState() != STATE.PAUSED) {
            return; // The player is not reset and the playback is not paused. 
        }
        // If the player is reset, the player must be initialized again. If the app is switched back to background, the service continues to pull streams. If the app is switched back to foreground, the player continues to render and pull streams after calling SurfaceView onCreate
        ALog.i("recover video from " + "activity on resume" + ", foreground=" + foreground);
        start();
    }
}
