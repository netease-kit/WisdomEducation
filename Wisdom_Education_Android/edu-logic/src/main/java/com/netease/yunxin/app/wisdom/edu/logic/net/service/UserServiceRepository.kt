/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.JoinClassroomReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduUpdateMemberPropertyReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduEntryRes


object UserServiceRepository : BaseRepository() {

    private val userService = getDelegateService(UserService::class.java)

    fun joinClassroom(
        roomUuid: String,
        eduJoinClassroomReq: JoinClassroomReq
    ): LiveData<NEResult<NEEduEntryRes>> {
        return userService.joinClassroom(
            appKey,
            roomUuid,
            eduJoinClassroomReq
        )
    }

    fun updateProperty(
        roomId: String,
        userUuid: String,
        key: String,
        req: NEEduUpdateMemberPropertyReq
    ): LiveData<NEResult<Void>> {
        return userService.updateProperty(appKey, roomId, userUuid, key, req)
    }

    fun updateInfo(
        roomId: String,
        userUuid: String
    ): LiveData<NEResult<String>> {
        return userService.updateInfo(appKey, roomId, userUuid)
    }

    fun recordPlayback(
        roomId: String,
        userUuid: String
    ): LiveData<NEResult<String>> {
        return userService.recordPlayback(appKey, roomId, userUuid)
    }

}