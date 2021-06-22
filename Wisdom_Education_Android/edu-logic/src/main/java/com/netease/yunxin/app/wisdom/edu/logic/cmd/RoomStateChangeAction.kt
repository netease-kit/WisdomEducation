/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomStates
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduState

/**
 * Created by hzsunyj on 2021/5/17.
 */
class RoomStateChangeAction(
    appId: String,
    roomUuid: String,
    var states: NEEduRoomStates,
    val operatorMember: NEEduMember,
) : CMDAction(appId, roomUuid)