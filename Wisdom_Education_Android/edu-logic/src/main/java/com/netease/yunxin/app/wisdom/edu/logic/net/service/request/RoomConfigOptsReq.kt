/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.request

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomConfig

data class RoomConfigOptsReq(
    val configId: Int? = null,
    val roomConfig: NEEduRoomConfig? = null,
    val roomName: String,
)
