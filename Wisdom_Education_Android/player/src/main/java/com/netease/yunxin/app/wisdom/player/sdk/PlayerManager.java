/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk;

import android.content.Context;

import com.netease.neliveplayer.sdk.NEMediaDataSource;
import com.netease.yunxin.app.wisdom.player.PlayerManagerImpl;
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKOptions;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoOptions;

import java.util.ArrayList;
import java.util.Map;

/**
 * Player management class
 * Global operations based on the player SDK
 * <p>
 *
 * @author netease
 */
public class PlayerManager {
    /**
     * Initialize the player SDK. To use the player, the player SDK must be initialized.
     *
     * @param context The context for the method call
     * @param config  The SDK configuration
     */
    public static void init(Context context, SDKOptions config) {
        PlayerManagerImpl.init(context, config);
    }

    /**
     * Checke whether the so library is ready
     * The interface is used when dynamic loading is required using the init operation
     *
     * @return the result
     */
    public static boolean isDynamicLoadReady() {
        return PlayerManagerImpl.isDynamicLoadReady();
    }

    /**
     * Get the SDK information
     *
     * @return the SDK information
     */
    public static SDKInfo getSDKInfo(Context context) {
        return PlayerManagerImpl.getSDKInfo(context);
    }


    /**
     * Create a player object
     *
     * @param context   Context information
     * @param videoPath Path of the video asset
     * @param options   Playback options
     * @return The player object
     */
    public static LivePlayer buildLivePlayer(Context context, String videoPath, VideoOptions options) {
        return PlayerManagerImpl.buildLivePlayer(context, videoPath, options);
    }

    /**
     * Create a player object
     *
     * @param context   Context information
     * @param videoPath Path of the video asset
     * @param options   Playback options
     * @return The player object
     */
    public static VodPlayer buildVodPlayer(Context context, String videoPath, VideoOptions options) {
        return PlayerManagerImpl.buildVodPlayer(context, videoPath, options);
    }

    /**
     * Create a player object
     *
     * @param context         Context information
     * @param mediaDataSource Custome media data source
     * @param options         Playback options
     * @return The player object
     */
    public static VodPlayer buildVodPlayer(Context context, NEMediaDataSource mediaDataSource, VideoOptions options) {
        return PlayerManagerImpl.buildVodPlayer(context, mediaDataSource, options);
    }

    /**
     * Add preload URLs
     *
     * @param urls Stream URLs
     */
    public static void addPreloadUrls(ArrayList<String> urls) {
        PlayerManagerImpl.addPreloadUrls(urls);
    }

    /**
     * Remove preload URLs
     *
     * @param urls Stream URLs
     */
    public static void removePreloadUrls(ArrayList<String> urls) {
        PlayerManagerImpl.removePreloadUrls(urls);

    }

    /**
     * Query the result of preload URLs
     *
     * @return Map<String, Integer> String represents URLs and Integer represents states. For more information, see {@link com.netease.neliveplayer.sdk.constant.NEPreloadStatusType}
     */
    public static Map<String, Integer> queryPreloadUrls() {
        return PlayerManagerImpl.queryPreloadUrls();
    }

    /**
     * Refreshe preload URLs
     */
    public static void refreshPreloadUrls() {
        PlayerManagerImpl.refreshPreloadUrls();
    }
}
