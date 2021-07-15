/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.request

data class RoomMuteStateReq(
    val muteChat: RoleMuteConfig?, val muteVideo: RoleMuteConfig?,
    val muteAudio: RoleMuteConfig?
)

data class RoleMuteConfig(val host: String?, val broadcaster: String?, val audience: String?)