/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

/**
 * the information data returned by the login interface
 *
 * @property userUuid user's unique id
 * @property userToken user's token
 * @property imToken im token
 * @property imKey im key
 * @property rtcKey rtc key
 */
data class NEEduLoginRes(
    val userUuid: String,
    val userToken: String,
    val imToken: String,
    val imKey: String,
    val rtcKey: String,
)
