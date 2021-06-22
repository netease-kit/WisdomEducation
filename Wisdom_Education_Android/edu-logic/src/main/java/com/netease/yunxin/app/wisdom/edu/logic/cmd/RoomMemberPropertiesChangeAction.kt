/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties

class RoomMemberPropertiesChangeAction(
    appId: String,
    roomUuid: String,
    var properties: NEEduMemberProperties,
    val member: NEEduMember,
) : CMDAction(appId, roomUuid)