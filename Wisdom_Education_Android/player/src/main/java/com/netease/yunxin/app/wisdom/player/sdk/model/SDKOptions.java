/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.model;

import com.netease.neliveplayer.proxy.config.NEPlayerConfig;
import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.model.NEDynamicLoadingConfig;

/**
 * SDK configuration class
 *
 * @author netease
 */

public class SDKOptions {
    /**
     * The device ID passed from the application layer to the SDK. The player can query the playback data and log using the ID
     */
    public String thirdUserId;

    /**
     * Refresh interval for preloading
     * Unit: milliseconds. The default value is 30 minutes. Set to 30 * 60 * 100
     * Applicable interfaces {@link NELivePlayer#addPreloadUrls },{@link NELivePlayer#removePreloadUrls },{@link NELivePlayer#queryPreloadUrls }
     */
    public long refreshPreLoadDuration = 30 * 60 * 1000;

    /**
     * Check whether to protect preprocessing and cache due to timeout
     * The default value is false, and the preprocessing and cache is protected. You can also set the value to true to disable the feature.
     */
    public boolean isCloseTimeOutProtect;

    /**
     * The so library dynamic loading configuration
     */
    public NEDynamicLoadingConfig dynamicLoadingConfig;

    /**
     * Data upload callback
     * The SDK report statistics to the CommsEase. If the callback is registerd, the SDK will not resport statistics, instead, the upper layer of the application sends request to report statistics.
     */
    public NELivePlayer.OnDataUploadListener dataUploadListener;

    /**
     * Player log callback
     */
    public NELivePlayer.OnLogListener logListener;

    /**
     * Cheche whether H.265 is supported
     */
    public NELivePlayer.OnSupportDecodeListener supportDecodeListener;

    /**
     * The private configuration
     */
    public NEPlayerConfig privateConfig;
}
