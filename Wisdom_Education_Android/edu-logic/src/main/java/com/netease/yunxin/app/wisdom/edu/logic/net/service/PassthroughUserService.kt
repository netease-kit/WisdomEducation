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
import java.lang.reflect.Method

/**
 * Created by hzsunyj on 2021/5/26.
 */
object PassthroughUserService : UserService, BaseService {

    private val userService = getService(UserService::class.java)

    override fun joinClassroom(
        appId: String,
        roomUuid: String,
        eduJoinClassroomReq: JoinClassroomReq,
    ): LiveData<NEResult<NEEduEntryRes>> {
        return userService.joinClassroom(appId, roomUuid, eduJoinClassroomReq)
    }

    override fun updateProperty(
        appId: String,
        roomId: String,
        userUuid: String,
        key: String,
        req: NEEduUpdateMemberPropertyReq,
    ): LiveData<NEResult<Void>> {
        val method: Method? = findMethod(BaseService.UPDATE_PROPERTY)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appId, roomId, userUuid, key, req)
        } else {
            return userService.updateProperty(appId, roomId, userUuid, key, req)
        }
    }

    override fun updateInfo(appId: String, roomId: String, userUuid: String): LiveData<NEResult<String>> {
        val method: Method? = findMethod(BaseService.UPDATE_INFO)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appId, roomId, userUuid)
        } else {
            return userService.updateInfo(appId, roomId, userUuid)
        }
    }

    override fun recordPlayback(appId: String, roomId: String, userUuid: String): LiveData<NEResult<String>> {
        return userService.recordPlayback(appId, roomId, userUuid)
    }
}