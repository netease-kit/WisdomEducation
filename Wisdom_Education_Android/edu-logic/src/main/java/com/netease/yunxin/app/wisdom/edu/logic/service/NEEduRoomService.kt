/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduRoomConfigRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions

/**
 * Methods for managing classes
 *
 */
abstract class NEEduRoomService : INEEduService() {

    /**
     * Configure a room. Create a room if the room does not exist. If the room already exists, return the room information. Before members join a room, the room must eixst.
     *
     * @param options The parameters used to configure the class
     */
    internal abstract fun config(options: NEEduClassOptions): LiveData<NEResult<NEEduRoomConfigRes>>

    /**
     * Get the class configuration
     *
     * @param roomUuid
     * @return
     */
    internal abstract fun getConfig(roomUuid: String): LiveData<NEResult<NEEduRoomConfig>>

    /**
     * join the class
     *
     * @param options Parameters required to join a class
     */
    internal abstract fun entryClass(options: NEEduClassOptions,isHasStreams:Boolean = false): LiveData<NEResult<NEEduEntryRes>>

    internal abstract fun updateCurrentRoomInfo(room: NEEduRoom)

    /**
     * Notification for class profile changes
     */
    abstract fun onCurrentRoomInfo(): LiveData<NEEduRoom>

    /**
     * Students leave the class
     *
     */
    abstract fun leaveClassroom(userUuid:String):LiveData<NEResult<Void>>

    /**
     * The teacher starts a class
     *
     */
    abstract fun startClass(roomUuid: String): LiveData<NEResult<Void>>

    /**
     * The teacher ends a class
     *
     */
    abstract fun finishClass(roomUuid: String): LiveData<NEResult<Void>>
    /**
     * Data snapshot
     */
    internal abstract fun snapshot(roomUuid: String): LiveData<NEResult<NEEduSnapshotRes>>

    /**
     * Get the subsequent data
     */
    internal abstract fun fetchNextSequences(roomUuid: String, nextId: Long): LiveData<NEResult<NEEduSequenceList>>

    /**
     * if the merge parameter is true, merge with the previous state. if false, replace the previous state
     */
    internal abstract fun updateRoomStatesChange(eduRoomStates: NEEduRoomStates, merge: Boolean)

    /**
     * Callback for class states
     */
    abstract fun onRoomStatesChange(): MediatorLiveData<NEEduRoomStates>

    /**
     * Send data from the client to the server.
     *
     * @param roomUuid
     * @param key
     * @param value
     * @return
     */
    internal abstract fun updateProperties(roomUuid: String, key: String, value: Int): LiveData<NEResult<Void>>

    /**
     * Notification for network changes
     */
    abstract fun onNetworkQualityChange(): LiveData<Array<out NERtcNetworkQualityInfo>>
}
