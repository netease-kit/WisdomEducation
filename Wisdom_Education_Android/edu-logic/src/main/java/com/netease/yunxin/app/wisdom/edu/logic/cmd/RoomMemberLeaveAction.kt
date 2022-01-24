/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember

/**
 * 
 */
class RoomMemberLeaveAction(
    appKey: String,
    roomUuid: String,
    val members: MutableList<NEEduMember>,
    val operatorMember: NEEduMember,
) : CMDAction(appKey, roomUuid)