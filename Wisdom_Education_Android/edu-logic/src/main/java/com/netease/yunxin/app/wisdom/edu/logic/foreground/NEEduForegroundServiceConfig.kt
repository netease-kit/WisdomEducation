/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.foreground

import androidx.annotation.DrawableRes

/**
 * Created by hzsunyj on 2021/5/27.
 */
class NEEduForegroundServiceConfig {
    companion object {
        const val DEFAULT_CONTENT_TITLE = "智慧云课堂"

        const val DEFAULT_CONTENT_TEXT = "智慧云课堂正在进行中"

        const val DEFAULT_CONTENT_TICKER = "智慧云课堂"

        const val DEFAULT_CHANNEL_ID = "ne_edu_wisdom_channel"

        const val DEFAULT_CHANNEL_NAME = "智慧云课堂通知"

        const val DEFAULT_CHANNEL_DESC = "智慧云课堂通知"
    }

    /**
     * 前台服务通知标题
     */
    var contentTitle = DEFAULT_CONTENT_TITLE

    /**
     * 前台服务通知內容
     */
    var contentText = DEFAULT_CONTENT_TEXT

    /**
     * 前台服务通知图标，如果不设置默认显示应用图标
     */
    @DrawableRes
    var smallIcon = 0

    /**
     * 入口页面
     */
    var launchActivityName: String? = null

    /**
     * 前台服务通知提示
     */
    var ticker = DEFAULT_CONTENT_TICKER

    /**
     * 前台服务通知通道id
     */
    var channelId = DEFAULT_CHANNEL_ID

    /**
     * 前台服务通知通道名称
     */
    var channelName = DEFAULT_CHANNEL_NAME

    /**
     * 前台服务通知通道描述
     */
    var channelDesc = DEFAULT_CHANNEL_DESC
}