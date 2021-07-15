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
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.UserMsgReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.RoomConfigOptsReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduRoomConfigRes

/**
 */
object RoomServiceRepository : BaseRepository() {

    private val roomService = getDelegateService(RoomService::class.java)

    fun config(
        roomUuid: String,
        roomConfigOptsReq: RoomConfigOptsReq,
    ): LiveData<NEResult<NEEduRoomConfigRes>> {
        return roomService.config(appKey, roomUuid, roomConfigOptsReq)
    }

    fun getConfig(
        roomUuid: String,
    ): LiveData<NEResult<NEEduRoomConfig>> {
        return roomService.getConfig(appKey, roomUuid)
    }

    fun sendP2PMessage(
        roomUuid: String,
        toUserUuid: String,
        userMsgReq: UserMsgReq,
    ): LiveData<NEResult<String>> {
        return roomService.sendP2PMessage(appKey, roomUuid, toUserUuid, userMsgReq)
    }

    fun fetchSnapshot(
        roomUuid: String,
    ): LiveData<NEResult<NEEduSnapshotRes>> {
        return roomService.fetchSnapshot(appKey, roomUuid)
    }

    fun fetchNextSequences(
        roomUuid: String,
        nextId: Long,
    ): LiveData<NEResult<NEEduSequenceList>> {
        return roomService.fetchNextSequences(appKey, roomUuid, nextId)
    }

    fun updateRoomStates(
        roomId: String,
        key: String,
        commonReq: CommonReq,
    ): LiveData<NEResult<Void>> {
        return roomService.updateRoomStates(appKey, roomId, key, commonReq)
    }

    fun deleteRoomStates(
        roomId: String,
        key: String,
    ): LiveData<NEResult<String>> {
        return roomService.deleteRoomStates(appKey, roomId, key)
    }

    fun updateRoomProperties(
        roomId: String,
        key: String,
        commonReq: CommonReq,
    ): LiveData<NEResult<Void>> {
        return roomService.updateRoomProperties(appKey, roomId, key, commonReq)
    }

    fun deleteRoomProperties(
        roomId: String,
        key: String,
    ): LiveData<NEResult<String>> {
        return roomService.deleteRoomProperties(appKey, roomId, key)
    }
}