/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.request

/**
 * 发送点对点消息
 *
 * @property type 消息类型
 * @property body
 */
data class UserMsgReq(var type: Int, val body: MutableMap<String, Any>?)

