/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

/**
 * 获取im接口返回的信息
 *
 * @property userUuid
 * @property userToken 操作者userToken
 * @property imToken
 * @property imKey
 * @property rtcKey
 */
data class NEEduLoginRes(
    val userUuid: String,
    val userToken: String,
    val imToken: String,
    val imKey: String,
    val rtcKey: String,
)
