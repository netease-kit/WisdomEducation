/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk;


import com.netease.neliveplayer.sdk.constant.NEKeyVerifyResultType;

/**
 * VOD player states/event callback observer
 * Player states or event callbacks based on the player SDK
 * <p>
 *
 * @author netease
 */

public interface VodPlayerObserver extends LivePlayerObserver {

    /**
     * [VOD only] The current position callback. The callback returns current positions every 1 second for progress bar control
     *
     * @param currentPosition The current position
     * @param duration        The video duration
     * @param percent         The percentage of progress [0.00-100.00]
     * @param cachedPosition  The cached position
     */
    void onCurrentPlayProgress(long currentPosition, long duration, float percent, long cachedPosition);

    /**
     * [VOD only] If the seekTo operation to the specified pisition succeeds, the callback is triggered
     */
    void onSeekCompleted();

    /**
     * [VOD only] The playback is complete.
     * Users can be notified that playback is complete
     */
    void onCompletion();


    /**
     * The callback is triggered if audio and video are not synced
     * If the callback is trigger, the application layer can respond as required
     * In this case, hardware decoding is recommended. If software decoding is enabled, audio and video may be not synced on some devices
     */
    void onAudioVideoUnsync();

    /**
     * [VOD only] indicates the network connection is unreliable
     * If multiple resolutions are available, it is recommended that a lower resolution is used if auto selecting resolution is disabled
     * Users can be indicated on UI that the network connection is weak
     */
    void onNetStateBad();

    /**
     * [VOD only] The result of getting the key
     *
     * @param ret The verification result {@link NEKeyVerifyResultType}
     */
    void onDecryption(int ret);
}
