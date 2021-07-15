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
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.RoomConfigOptsReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.UserMsgReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduRoomConfigRes
import java.lang.reflect.Method

/**
 * Created by hzsunyj on 2021/5/26.
 */
object PassthroughRoomService : RoomService, BaseService {

    private val roomService = getService(RoomService::class.java)

    override fun config(
        appKey: String,
        roomUuid: String,
        roomConfigOptionsReq: RoomConfigOptsReq,
    ): LiveData<NEResult<NEEduRoomConfigRes>> {
        return roomService.config(appKey, roomUuid, roomConfigOptionsReq)
    }

    override fun getConfig(appKey: String, roomUuid: String): LiveData<NEResult<NEEduRoomConfig>> {
        return roomService.getConfig(appKey, roomUuid)
    }

    override fun fetchSnapshot(appKey: String, roomUuid: String): LiveData<NEResult<NEEduSnapshotRes>> {
        return roomService.fetchSnapshot(appKey, roomUuid)
    }

    override fun fetchNextSequences(
        appKey: String,
        roomId: String,
        nextId: Long,
    ): LiveData<NEResult<NEEduSequenceList>> {
        return roomService.fetchNextSequences(appKey, roomId, nextId)
    }

    override fun sendP2PMessage(
        appKey: String,
        roomUuid: String,
        toUserUuid: String,
        userMsgReq: UserMsgReq,
    ): LiveData<NEResult<String>> {
        return roomService.sendP2PMessage(appKey, roomUuid, toUserUuid, userMsgReq)
    }

    override fun updateRoomStates(
        appKey: String,
        roomId: String,
        key: String,
        commonReq: CommonReq,
    ): LiveData<NEResult<Void>> {
        var method: Method? = findMethod(BaseService.UPDATE_ROOM_STATES)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appKey, roomId, key, commonReq)
        } else {
            roomService.updateRoomStates(appKey, roomId, key, commonReq)
        }
    }

    override fun deleteRoomStates(appKey: String, roomId: String, key: String): LiveData<NEResult<String>> {
        val method: Method? = findMethod(BaseService.DELETE_ROOM_STATES)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appKey, roomId, key)
        } else {
            return roomService.deleteRoomStates(appKey, roomId, key)
        }
    }

    override fun updateRoomProperties(
        appKey: String,
        roomId: String,
        key: String,
        commonReq: CommonReq,
    ): LiveData<NEResult<Void>> {
        val method: Method? = findMethod(BaseService.UPDATE_ROOM_PROPERTIES)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appKey, roomId, key, commonReq)
        } else {
            return roomService.updateRoomProperties(appKey, roomId, key, commonReq)
        }
    }

    override fun deleteRoomProperties(appKey: String, roomId: String, key: String): LiveData<NEResult<String>> {
        val method: Method? = findMethod(BaseService.DELETE_ROOM_PROPERTIES)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appKey, roomId, key)
        } else {
            return roomService.deleteRoomProperties(appKey, roomId, key)
        }
    }
}