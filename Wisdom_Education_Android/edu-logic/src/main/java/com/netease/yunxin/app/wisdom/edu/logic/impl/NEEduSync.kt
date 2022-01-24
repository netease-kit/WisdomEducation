/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.impl

import android.util.ArrayMap
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.base.util.observeForeverOnce
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.kit.alog.ALog

/**
 * 
 */
internal class NEEduSync(val neEduManager: NEEduManagerImpl) {


    private var lastSequenceId: Long = -1 // The last sequence ID of the local cache

    var syncing: Boolean = false

    /**The ID set of a sync process*/
    private var sequenceList = mutableListOf<Long>()

    /**CMD data set of a sync process*/
    private val sequenceData: ArrayMap<Long, NEEduCMDBody> = ArrayMap()

    val errorLD: MediatorLiveData<Int> = MediatorLiveData()

    /**
     *cache cmd data
     */
    private fun cacheCmdData(cmdBodyList: MutableList<NEEduCMDBody>) {
        synchronized(sequenceList) {
            val iterable = cmdBodyList.iterator()
            while (iterable.hasNext()) {
                val next = iterable.next()
                if (next.type == NEEduNotifyType.R.value && next.roomUuid == NEEduManagerImpl.getRoom().roomUuid) {
                    next.sequence?.let {
                        if (next.sequence > lastSequenceId) { // Sequence sorting one by one to the last sequence.
                            sequenceList.add(next.sequence)
                            sequenceData[next.sequence] = next
                        }
                    }
                }
            }
        }
        handleCmdData()
    }

    /**
     *cache cmd data
     */
    private fun cacheCmdData(cmdBody: NEEduCMDBody) {
        cmdBody.sequence?.let {
            synchronized(sequenceList) {
                sequenceList.add(cmdBody.sequence)
                sequenceData[cmdBody.sequence] = cmdBody
            }
        }
    }

    private fun handleCmdData() {
        if (lastSequenceId == -1L || sequenceList.isEmpty()) {
            return
        }
        val tempList = mutableListOf<Long>()
        val tempMap: ArrayMap<Long, NEEduCMDBody> = ArrayMap()
        synchronized(sequenceList) {
            sequenceList.sort()
            tempList.addAll(sequenceList)
            tempMap.putAll(sequenceData)
            clear()
        }
        tempList.sort()
        tempList.forEach {
            if (it > lastSequenceId) {
                lastSequenceId = it
                neEduManager.cmdDispatcher?.dispatchCMD(tempMap[it]!!)
            }
        }
    }

    private fun clear() {
        sequenceList.clear()
        sequenceData.clear()
    }

    /**
     * Handle notifications from the server
     *
     * If the initial value lastSequenceId==-1, no snapshots are pulled. The notifications are saved in local cache
     */
    fun handle(cmdBody: NEEduCMDBody): Boolean {
        if (cmdBody.roomUuid != NEEduManagerImpl.getRoom().roomUuid) {
            return false
        }
        if (cmdBody.type == NEEduNotifyType.R.value) {
            cmdBody.sequence?.let {
                when {
                    lastSequenceId == -1L -> {
                        cacheCmdData(cmdBody)
                        return false
                    }
                    cmdBody.sequence - lastSequenceId == 1L -> { //
                        lastSequenceId = cmdBody.sequence
                        return true
                    }
                    cmdBody.sequence <= lastSequenceId -> { // error loss, if the sequence is less than lastSequenceId in the local cache，drop the message
                        return false
                    }
                    cmdBody.sequence - lastSequenceId >= 10L -> { // If the sequence is 10 greater than lastSequenceId in the local cache, query the full data of the snapshot
                        snapshot(NEEduManagerImpl.getRoom().roomUuid)
                        return false
                    }
                    else -> { // If the sequce is greater than lastSequenceId but the difference is less than or equal to 10, update to the newest sequence using the sequence API.
                        fetchNextSequences(lastSequenceId + 1)
                        return false
                    }
                }
            }
        }

        return false
    }

    private fun fetchNextSequences(nextId: Long) {
        ALog.i("fetchNextSequences $lastSequenceId $nextId")
        val fetchNextSequences =
            neEduManager.getRoomService().fetchNextSequences(NEEduManagerImpl.getRoom().roomUuid, nextId)
        fetchNextSequences.observeForeverOnce { it ->
            if (it.success()) {
                it.data?.let {
                    cacheCmdData(it.list)
                }
            } else {// fail, to snapshot
                snapshot(NEEduManagerImpl.getRoom().roomUuid)
            }
        }
    }

    fun snapshot(roomUuid: String) {
        if (syncing) {
            return
        }
        syncing = true
        ALog.i("snapshot $lastSequenceId")
        val snapshot = neEduManager.getRoomService().snapshot(roomUuid)
        snapshot.observeForeverOnce { t ->
            syncing = false
            if (!t.success()) {
                if (t.code == NEEduHttpCode.ROOM_NOT_EXIST.code) {
                    errorLD.postValue(t.code)
                }
                return@observeForeverOnce
            }
            // network break, close class room, but no response from clients, create the same classId room, when network
            // reconnect, request snapshot, response new class snapshot, but client still old class snapshot
            if (t.data != null && neEduManager.getRoom().rtcCid != t.data!!.snapshot.room.rtcCid) {
                errorLD.postValue(NEEduHttpCode.ROOM_NOT_EXIST.code)
                return@observeForeverOnce
            }
            t.data?.let {
                dispatchSnapshotEvent(it)
            }
            handleCmdData()
        }
    }

    private fun dispatchSnapshotEvent(it: NEEduSnapshotRes) =
        if (it.sequence <= lastSequenceId) {
            // Do nothing. In this case, the message is updated to the top. Wait for next sync
        } else {
            lastSequenceId = it.sequence
            if (it.snapshot.members.size > 0) {
                if (neEduManager.isLiveClass()) {
                    neEduManager.getMemberService().updateMemberJoin(it.snapshot.members, true)
                } else {
                    neEduManager.getRtcService().updateSnapshotMember(it.snapshot.members)
                    val lastJoinList: MutableList<NEEduMember> = mutableListOf()
                    lastJoinList.addAll(neEduManager.getMemberService().getMemberList())
                    neEduManager.getRtcService().updateMemberJoin(it.snapshot.members, false)
                    neEduManager.getMemberService().updateMemberJoin(it.snapshot.members, false)
                    it.snapshot.members.firstOrNull { t -> NEEduManagerImpl.isSelf(t.userUuid) }
                        ?.also { member ->
                            member.properties?.let { it ->
                                val propertiesDiff =
                                    it.diff(lastJoinList.firstOrNull { it1 -> it1 == member }?.properties)
                                neEduManager.getMemberService().updateMemberPropertiesChange(
                                    member, member
                                        .properties!!
                                )
                                if (lastJoinList.isNotEmpty() || !neEduManager.roomConfig.is1V1()) {
                                    neEduManager.getBoardService().updatePermission(member, propertiesDiff)
                                    neEduManager.getShareScreenService()
                                        .updatePermission(member, propertiesDiff)
                                }
                            }
                        }
                    neEduManager.getHandsUpService().updateMemberJoin(it.snapshot.members, false)
                    neEduManager.getShareScreenService().updateMemberJoin(it.snapshot.members, false)
                }
            }
            it.snapshot.room.states?.let { t ->
                neEduManager.getRoomService().updateRoomStatesChange(t, false)
                t.muteChat?.let {
                    neEduManager.getIMService()
                        .updateMuteAllChat(t.muteChat?.value == NEEduStateValue.OPEN)
                }
            }
        }
}