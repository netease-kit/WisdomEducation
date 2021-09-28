/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomStates.Companion.STATE_STEP
import com.netease.yunxin.app.wisdom.edu.logic.net.service.RoomServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.UserServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.JoinClassroomReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.RoomConfigOptsReq
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduEntryRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduRoomConfigRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduRoomService

/**
 * Created by hzsunyj on 2021/5/17.
 */
internal class NEEduRoomServiceImpl : NEEduRoomService() {

    private val roomStatesLD: MediatorLiveData<NEEduRoomStates> = MediatorLiveData()

    private val roomLD: MediatorLiveData<NEEduRoom> = MediatorLiveData()

    private lateinit var eduRoom: NEEduRoom

    override fun config(options: NEEduClassOptions): LiveData<NEResult<NEEduRoomConfigRes>> {
        val roomConfigOptsReq = RoomConfigOptsReq(
            configId = options.sceneType.configId(),
            roomName = options.className,
            config = options.config
        )
        return RoomServiceRepository.config(options.classId, roomConfigOptsReq)
    }

    override fun getConfig(roomUuid: String): LiveData<NEResult<NEEduRoomConfig>> {
        return RoomServiceRepository.getConfig(roomUuid)
    }

    override fun entryClass(options: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>> {
        val joinClassroomReq = JoinClassroomReq(
            userName = options.nickName,
            role = options.roleType.value,
            streams = options.sceneType.streams(options.roleType)
        )
        return UserServiceRepository.joinClassroom(options.classId, joinClassroomReq).map {
            if (it.success()) {
                eduRoom = it.data!!.room
            }
            it
        }
    }

    override fun updateCurrentRoomInfo(room: NEEduRoom) {
        roomLD.postValue(room)
    }

    override fun onCurrentRoomInfo(): MediatorLiveData<NEEduRoom> {
        return roomLD
    }

    override fun leaveClassroom() {
        TODO("Not yet implemented")
    }

    override fun startClass(roomUuid: String): LiveData<NEResult<Void>> {
        val commonReq = CommonReq(NEEduRoomStep.START.ordinal)
        return RoomServiceRepository.updateRoomStates(roomId = roomUuid, commonReq = commonReq, key = STATE_STEP)
    }

    override fun finishClass(roomUuid: String): LiveData<NEResult<Void>> {
        val commonReq = CommonReq(NEEduRoomStep.END.ordinal)
        return RoomServiceRepository.updateRoomStates(roomId = roomUuid, commonReq = commonReq, key = STATE_STEP)
    }

    override fun updateProperties(roomUuid: String, key: String, value: Int): LiveData<NEResult<Void>> {
        val commonReq = CommonReq(value = value)
        return RoomServiceRepository.updateRoomProperties(roomId = roomUuid, commonReq = commonReq, key = key)
    }

    override fun onNetworkQualityChange(): LiveData<Array<out NERtcNetworkQualityInfo>> {
        return NEEduManagerImpl.rtcManager.networkQualityLD
    }

    override fun snapshot(roomUuid: String): LiveData<NEResult<NEEduSnapshotRes>> {
        return RoomServiceRepository.fetchSnapshot(roomUuid).map {
            if (it.success()) {
                eduRoom = it.data!!.snapshot.room
                updateCurrentRoomInfo(eduRoom)
                eduRoom.states?.let { _ ->
                    eduRoom.states!!.updateDuration(it.ts!!)
                    updateRoomStatesChange(eduRoom.states!!, false)
                }
            }
            it
        }
    }

    override fun fetchNextSequences(
        roomUuid: String,
        nextId: Long,
    ): LiveData<NEResult<NEEduSequenceList>> {
        return RoomServiceRepository.fetchNextSequences(roomUuid, nextId)
    }

    /**
     * 变更房间状态
     */
    override fun updateRoomStatesChange(eduRoomStates: NEEduRoomStates, merge: Boolean) {
        if (!merge) {
            eduRoom.states = eduRoomStates
        } else {
            if (eduRoom.states == null) {
                eduRoom.states = eduRoomStates
            } else {
                eduRoom.states!!.merge(eduRoomStates)
            }
        }
        roomStatesLD.postValue(eduRoomStates)
    }

    override fun onRoomStatesChange(): MediatorLiveData<NEEduRoomStates> {
        return roomStatesLD
    }
}