/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoom
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStreams


data class NEEduEntryRes(
    val room: NEEduRoom,
    val member: NEEduEntryMember,
) {
    fun isHost(): Boolean {
        return member.isHost()
    }
}

class NEEduEntryMember(
    val rtcKey: String,
    val rtcToken: String,
    role: String,
    userName: String,
    userUuid: String,
    rtcUid: Long,
    time: Long,
    streams: NEEduStreams,
    properties: NEEduMemberProperties?,
) : NEEduMember(role, userName, userUuid, rtcUid, time, streams, properties) {
}
