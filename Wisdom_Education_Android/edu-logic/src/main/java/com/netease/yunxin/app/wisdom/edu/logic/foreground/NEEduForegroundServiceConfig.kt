/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.foreground

import androidx.annotation.DrawableRes

/**
 * 
 */
class NEEduForegroundServiceConfig {
    companion object {
        const val DEFAULT_CONTENT_TITLE = "Wisdom Education"

        const val DEFAULT_CONTENT_TEXT = "Wisdom Education is running"

        const val DEFAULT_CONTENT_TICKER = "Wisdom Education"

        const val DEFAULT_CHANNEL_ID = "ne_edu_wisdom_channel"

        const val DEFAULT_CHANNEL_NAME = "Wisdom Education Notification"

        const val DEFAULT_CHANNEL_DESC = "Wisdom Education Notification"
    }

    /**
     * Notification title
     */
    var contentTitle = DEFAULT_CONTENT_TITLE

    /**
     * Notification contents
     */
    var contentText = DEFAULT_CONTENT_TEXT

    /**
     * Notification icon. If unspecified, the default app icon is used.
     */
    @DrawableRes
    var smallIcon = 0

    /**
     * Entry point page
     */
    var launchActivityName: String? = null

    /**
     * Notification tip
     */
    var ticker = DEFAULT_CONTENT_TICKER

    /**
     * Notification channel ID
     */
    var channelId = DEFAULT_CHANNEL_ID

    /**
     * Notification channel name
     */
    var channelName = DEFAULT_CHANNEL_NAME

    /**
     * Notification channel description
     */
    var channelDesc = DEFAULT_CHANNEL_DESC
}