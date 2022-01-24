/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.player;

import android.content.Context;

import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.NEMediaDataSource;
import com.netease.neliveplayer.sdk.model.NESDKConfig;
import com.netease.yunxin.app.wisdom.player.sdk.LivePlayer;
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayer;
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKOptions;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoOptions;

import java.util.ArrayList;
import java.util.Map;

public class PlayerManagerImpl {

    /**
     * Instantiate the SDK. To use the player, the player must be Instantiated.
     *
     * @param context The context for API call
     * @param options SDK configuration
     */
    public static void init(Context context, SDKOptions options) {
        NESDKConfig sdkConfig = new NESDKConfig();
        if (options != null) {
            sdkConfig.thirdUserId = options.thirdUserId;
            sdkConfig.refreshPreLoadDuration = options.refreshPreLoadDuration;
            sdkConfig.isCloseTimeOutProtect = options.isCloseTimeOutProtect;
            sdkConfig.dynamicLoadingConfig = options.dynamicLoadingConfig;
            sdkConfig.dataUploadListener = options.dataUploadListener;
            sdkConfig.logListener = options.logListener;
            sdkConfig.supportDecodeListener = options.supportDecodeListener;
            sdkConfig.isCloseTimeOutProtect = false;//Handle the timeout in preparation using the player SDK. If developers want to handle the timeout, set the value to true, and see BaseLivePlayer#preparingTimeoutTask in playkit.
            sdkConfig.privateConfig = options.privateConfig;
        }
        NELivePlayer.init(context, sdkConfig);
    }

    /**
     * Check if the so library is ready.
     * The API is called when the init interface is configured with dynamic load.
     *
     * @return Check if the so library is ready.
     */
    public static boolean isDynamicLoadReady() {
        return NELivePlayer.isDynamicLoadReady();
    }

    /**
     * Get the SDK information
     *
     * @return The information about the SDK instance
     */
    public static SDKInfo getSDKInfo(Context context) {
        if (NELivePlayer.getSDKInfo(context) == null) {
            return null;
        }
        SDKInfo sdkInfo = new SDKInfo();
        sdkInfo.version = NELivePlayer.getSDKInfo(context).version;
        sdkInfo.deviceId = NELivePlayer.getSDKInfo(context).deviceId;
        return sdkInfo;
    }

    /**
     * Constuct the object of the player instance
     *
     * @param context   The context information
     * @param videoPath The path of the video asset
     * @param options   The playback options
     * @return The object of the player instance
     */
    public static LivePlayer buildLivePlayer(Context context, String videoPath, VideoOptions options) {
        return new LivePlayerImpl(context, videoPath, options);
    }

    /**
     * Constuct the object of the player instance
     *
     * @param context   The context information
     * @param videoPath The path of the video asset
     * @param options   The playback options
     * @return The object of the player instance
     */
    public static VodPlayer buildVodPlayer(Context context, String videoPath, VideoOptions options) {
        return new LivePlayerImpl(context, videoPath, options);
    }

    /**
     * Constuct the object of the player instance
     *
     * @param context         The context information
     * @param mediaDataSource The video asset. The custom data source is required using NEMediaDataSource
     * @param options         The playback options
     * @return The object of the player instance
     */
    public static VodPlayer buildVodPlayer(Context context, NEMediaDataSource mediaDataSource, VideoOptions options) {
        return new LivePlayerImpl(context, mediaDataSource, options);
    }


    /**
     * Add URLs for preloading data
     *
     * @param urls URLs for preloading data
     */
    public static void addPreloadUrls(ArrayList<String> urls) {
        NELivePlayer.addPreloadUrls(urls);
    }

    /**
     * Remove URLs for preloading data
     *
     * @param urls URLs for preloading data
     */
    public static void removePreloadUrls(ArrayList<String> urls) {
        NELivePlayer.removePreloadUrls(urls);

    }

    /**
     * Query URLs for preloading data
     *
     * @return Map<String, Integer> String indicates URLs. Integer indicates the status. For information about status codes, see {@link com.netease.yunxin.app.wisdom.record.video.sdk.constant.PreloadStatusType}
     */
    public static Map<String, Integer> queryPreloadUrls() {
        return NELivePlayer.queryPreloadUrls();
    }

    /**
     * Refresh the preload URLs
     */
    public static void refreshPreloadUrls() {
        NELivePlayer.refreshPreloadUrls();
    }

}
