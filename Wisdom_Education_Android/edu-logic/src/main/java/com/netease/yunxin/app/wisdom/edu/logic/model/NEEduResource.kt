/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

data class NEEduResource(
    val live: Boolean? = false,
    val rtc: Boolean? = true,
    val chatroom: Boolean? = true,
    val whiteboard: Boolean? = true
)