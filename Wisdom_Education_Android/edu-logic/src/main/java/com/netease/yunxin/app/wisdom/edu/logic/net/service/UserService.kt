/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduEntryRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.JoinClassroomReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduUpdateMemberPropertyReq
import retrofit2.http.*

internal interface UserService {

    /**
     * Join a room
     */
    @POST("scene/apps/{appKey}/v1/rooms/{roomUuid}/entry")
    fun joinClassroom(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Body eduJoinClassroomReq: JoinClassroomReq,
    ): LiveData<NEResult<NEEduEntryRes>>

    /**
     * Member properties
     */
    @PUT("scene/apps/{appKey}/v1/rooms/{roomId}/members/{userUuid}/properties/{key}")
    fun updateProperty(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("userUuid") userUuid: String,
        @Path("key") key: String,
        @Body req: NEEduUpdateMemberPropertyReq,
    ): LiveData<NEResult<Void>>

    /**
     * Member properties
     */
    @DELETE("scene/apps/{appKey}/v1/rooms/{roomId}/members/{userUuid}")
    fun leaveClassroom(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("userUuid") userUuid: String,
    ): LiveData<NEResult<Void>>

    /**
     * Member properties and streams operations
     */
    @POST("scene/apps/{appKey}/v1/rooms/{roomId}/users/{userUuid}/info")
    fun updateInfo(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("userUuid") userUuid: String,
    ): LiveData<NEResult<String>>
}