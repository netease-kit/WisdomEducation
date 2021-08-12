/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.options

import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundServiceConfig

/**
 * SDK 全局配置
 *
 * @property appKey 应用的 AppKey。可以在网易云信控制台中查看。
 * @property authorization 调用服务端接口时，请求头中的校验参数。
 * @property baseUrl 应用服务器地址。私有化配置时需替换为私有化部署地址
 * @property reuseIM 配置是否复用底层NIM-SDK的长连接通道，默认关闭。仅当应用中同时还需独立接入和使用NIM-SDK，才需要开启该配置，其他情况下请忽略该配置。
 * @property foregroundServiceConfig 前台服务配置项
 */
class NEEduOptions(
    val appKey: String,
    val authorization: String,
    val baseUrl: String,
    val reuseIM: Boolean? = false,
    val foregroundServiceConfig: NEEduForegroundServiceConfig? = null,
)