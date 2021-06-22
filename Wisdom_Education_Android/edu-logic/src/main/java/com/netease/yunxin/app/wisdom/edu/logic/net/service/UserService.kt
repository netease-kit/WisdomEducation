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
import retrofit2.http.*

internal interface UserService {

    /**
     * 用户加入房间
     */
    @POST("/scene/apps/{appId}/v1/rooms/{roomUuid}/entry")
    fun joinClassroom(
        @Path("appId") appId: String,
        @Path("roomUuid") roomUuid: String,
        @Body eduJoinClassroomReq: JoinClassroomReq,
    ): LiveData<NEResult<NEEduEntryRes>>

    /**
     * 房间成员属性
     */
    @PUT("/scene/apps/{appId}/v1/rooms/{roomId}/members/{userUuid}/properties/{key}")
    fun updateProperty(
        @Path("appId") appId: String,
        @Path("roomId") roomId: String,
        @Path("userUuid") userUuid: String,
        @Path("key") key: String,
        @Body req: NEEduUpdateMemberPropertyReq,
    ): LiveData<NEResult<Void>>

    /**
     * 房间成员属性和流集合操作
     */
    @POST("/scene/apps/{appId}/v1/rooms/{roomId}/users/{userUuid}/info")
    fun updateInfo(
        @Path("appId") appId: String,
        @Path("roomId") roomId: String,
        @Path("userUuid") userUuid: String,
    ): LiveData<NEResult<String>>

    /**
     * 录制回放
     */
    @POST("/scene/apps/{appId}/v1/rooms/{roomId}/users/{userUuid}/record/playback")
    fun recordPlayback(
        @Path("appId") appId: String,
        @Path("roomId") roomId: String,
        @Path("userUuid") userUuid: String,
    ): LiveData<NEResult<String>>
}