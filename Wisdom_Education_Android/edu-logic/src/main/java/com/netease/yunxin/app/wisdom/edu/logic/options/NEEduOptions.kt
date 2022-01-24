/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.options

import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundServiceConfig

/**
 * SDK global configuration
 *
 * @property appKey The AppKey that you can view in the CommsEase console.
 * @property authorization The authentication parameter in the request header for server API calls.
 * @property baseUrl The URL of the app server. The URL must be replaced with the URL of the private server for on-premises deployment
 * @property reuseIM Specifies whether to reuse the persistent connection channel supported by NIM-SDK. By default, the parameter is disabled. The parameter is enabled only if the application requires separate use of NIM-SDK. Otherwise, the parameter is ignored.
 * @property foregroundServiceConfig The configuration for foreground services
 * @property useIMAssetServerAddressConfig  Whether to enable IM privatization. If IM privatization is enabled, the "/assets/server.conf" configuration file is loaded first. If the configuration file fails to be loaded, the privatization disable.
 * @property useRtcAssetServerAddressConfig Whether to enable Rtc privatization. If IM privatization is enabled, the "/assets/rtc_server.conf" configuration file is loaded first. If the configuration file fails to be loaded, the privatization disable.
 * @property useWbAssetServerAddressConfig Whether to enable whiteboard privatization. If whiteboard privatization is enabled, the "/assets/wb_server.conf" configuration file is loaded first. If the configuration file fails to be loaded, the privatization disable.
 */
class NEEduOptions(
    val appKey: String,
    val authorization: String,
    val baseUrl: String,
    val reuseIM: Boolean? = false,
    val foregroundServiceConfig: NEEduForegroundServiceConfig? = null,
    var useIMAssetServerAddressConfig: Boolean? = false,
    var useRtcAssetServerAddressConfig: Boolean? = false,
    var useWbAssetServerAddressConfig: Boolean? = false,
)