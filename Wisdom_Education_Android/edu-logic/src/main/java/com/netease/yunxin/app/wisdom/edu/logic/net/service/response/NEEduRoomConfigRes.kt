/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomConfig
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomStates

data class NEEduRoomConfigRes(
    val roomName: String,
    val roomUuid: String,
    var states: NEEduRoomStates?,
    var properties: MutableMap<String, Any>?,
    val config: NEEduRoomConfig,
)