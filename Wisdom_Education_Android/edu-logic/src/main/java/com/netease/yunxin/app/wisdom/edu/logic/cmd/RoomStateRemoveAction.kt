/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomStates

/**
 * 
 */
class RoomStateRemoveAction(
    appKey: String,
    roomUuid: String,
    var states: NEEduRoomStates,
    val operatorMember: NEEduMember,
) : CMDAction(appKey, roomUuid)