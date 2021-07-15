/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomConfig
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduSequenceList
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduSnapshotRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.UserMsgReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.RoomConfigOptsReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduRoomConfigRes
import retrofit2.http.*

internal interface RoomService {

    /**
     * 房间配置
     *
     * @param appId
     * @param roomUuid
     * @param roomConfigOptionsReq
     * @return
     */
    @PUT("/scene/apps/{appKey}/v1/rooms/{roomUuid}")
    fun config(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Body roomConfigOptionsReq: RoomConfigOptsReq,
    ): LiveData<NEResult<NEEduRoomConfigRes>>

    /**
     * 获取房间配置
     *
     * @param appId
     * @param roomUuid
     * @return
     */
    @GET("/scene/apps/{appKey}/v1/rooms/{roomUuid}/config")
    fun getConfig(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
    ): LiveData<NEResult<NEEduRoomConfig>>

    /**
     * 查询房间快照
     */
    @GET("/scene/apps/{appKey}/v1/rooms/{roomUuid}/snapshot")
    fun fetchSnapshot(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
    ): LiveData<NEResult<NEEduSnapshotRes>>

    /**
     * 收件箱拉取列表
     */
    @GET("/scene/apps/{appKey}/v1/rooms/{roomId}/sequence/nextId={nextId}")
    fun fetchNextSequences(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Query("nextId") nextId: Long,
    ): LiveData<NEResult<NEEduSequenceList>>

    /**
     * 发送自定义的点对点消息
     */
    @POST("/scene/apps/{appKey}/v1/rooms/{roomUuid}/users/{toUserUuid}/messages/peer")
    fun sendP2PMessage(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Path("toUserUuid") toUserUuid: String,
        @Body userMsgReq: UserMsgReq,
    ): LiveData<NEResult<String>>

    /**
     * 房间状态
     */
    @PUT("/scene/apps/{appKey}/v1/rooms/{roomId}/states/{key}")
    fun updateRoomStates(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
        @Body commonReq: CommonReq,
    ): LiveData<NEResult<Void>>

    /**
     * 删除房间状态
     */
    @DELETE("/scene/apps/{appKey}/v1/rooms/{roomId}/states/{key}")
    fun deleteRoomStates(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
    ): LiveData<NEResult<String>>

    /**
     * 房间属性
     */
    @PUT("/scene/apps/{appKey}/v1/rooms/{roomId}/properties/{key}")
    fun updateRoomProperties(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
        @Body commonReq: CommonReq,
    ): LiveData<NEResult<Void>>

    /**
     * 删除房间属性
     */
    @DELETE("/scene/apps/{appKey}/v1/rooms/{roomId}/properties/{key}")
    fun deleteRoomProperties(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
    ): LiveData<NEResult<String>>
}