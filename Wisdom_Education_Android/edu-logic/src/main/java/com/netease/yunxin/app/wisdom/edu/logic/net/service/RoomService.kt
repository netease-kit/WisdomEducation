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
     * Room configuration
     *
     * @param roomUuid
     * @param roomConfigOptionsReq
     * @return
     */
    @PUT("scene/apps/{appKey}/v1/rooms/{roomUuid}")
    fun config(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Body roomConfigOptionsReq: RoomConfigOptsReq,
    ): LiveData<NEResult<NEEduRoomConfigRes>>

    /**
     * Get the room configuration
     *
     * @param roomUuid
     * @return
     */
    @GET("scene/apps/{appKey}/v1/rooms/{roomUuid}/config")
    fun getConfig(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
    ): LiveData<NEResult<NEEduRoomConfig>>

    /**
     * Query room snapshots
     */
    @GET("scene/apps/{appKey}/v1/rooms/{roomUuid}/snapshot")
    fun fetchSnapshot(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
    ): LiveData<NEResult<NEEduSnapshotRes>>

    /**
     * Get the list from the inbox
     */
    @GET("scene/apps/{appKey}/v1/rooms/{roomId}/sequence")
    fun fetchNextSequences(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Query("nextId") nextId: Long,
    ): LiveData<NEResult<NEEduSequenceList>>

    /**
     * Send custom peer-to-peer messages
     */
    @POST("scene/apps/{appKey}/v1/rooms/{roomUuid}/users/{toUserUuid}/messages/peer")
    fun sendP2PMessage(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Path("toUserUuid") toUserUuid: String,
        @Body userMsgReq: UserMsgReq,
    ): LiveData<NEResult<String>>

    /**
     * Room states
     */
    @PUT("scene/apps/{appKey}/v1/rooms/{roomId}/states/{key}")
    fun updateRoomStates(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
        @Body commonReq: CommonReq,
    ): LiveData<NEResult<Void>>

    /**
     * Delete room state
     */
    @DELETE("scene/apps/{appKey}/v1/rooms/{roomId}/states/{key}")
    fun deleteRoomStates(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
    ): LiveData<NEResult<String>>

    /**
     * Room properties
     */
    @PUT("scene/apps/{appKey}/v1/rooms/{roomId}/properties/{key}")
    fun updateRoomProperties(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
        @Body commonReq: CommonReq,
    ): LiveData<NEResult<Void>>

    /**
     * Delete room properties
     */
    @DELETE("scene/apps/{appKey}/v1/rooms/{roomId}/properties/{key}")
    fun deleteRoomProperties(
        @Path("appKey") appKey: String,
        @Path("roomId") roomId: String,
        @Path("key") key: String,
    ): LiveData<NEResult<String>>
}