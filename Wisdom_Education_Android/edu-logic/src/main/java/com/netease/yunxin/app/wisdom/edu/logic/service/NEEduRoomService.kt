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
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduEntryRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduRoomConfigRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions

/**
 * 提供可供 App 调用的课堂相关方法
 *
 */
abstract class NEEduRoomService : INEEduService() {

    /**
     * 配置房间，房间不存在则新建，存在则返回房间信息，加入房间之前，房间必须存在
     *
     * @param options 配置课堂房间的参数信息
     * @param callbackNE 结果回调
     */
    internal abstract fun config(options: NEEduClassOptions): LiveData<NEResult<NEEduRoomConfigRes>>

    /**
     * 获得房间配置
     *
     * @param roomUuid
     * @return
     */
    internal abstract fun getConfig(roomUuid: String): LiveData<NEResult<NEEduRoomConfig>>

    /**
     * 加入房间
     *
     * @param options 加入房间必要的参数信息
     * @param callbackNE 结果回调
     */
    internal abstract fun entryClass(options: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>>

    internal abstract fun updateCurrentRoomInfo(room: NEEduRoom)

    /**
     * 当前课堂详情变化通知
     * @param callbackNE 回调，数据类型为 [Room]
     */
    abstract fun onCurrentRoomInfo(): LiveData<NEEduRoom>

    /**
     * 用户退出房间
     *
     */
    abstract fun leaveClassroom()

    /**
     * 老师开始课堂
     * @param callbackNE 方法执行回调
     *
     */
    abstract fun startClass(roomUuid: String): LiveData<NEResult<Void>>

    /**
     * 老师结束课堂
     * @param callbackNE 方法执行回调
     *
     */
    abstract fun finishClass(roomUuid: String): LiveData<NEResult<Void>>
    /**
     * 快照数据
     */
    internal abstract fun snapshot(roomUuid: String): LiveData<NEResult<NEEduSnapshotRes>>

    /**
     *获取接下来的数据
     */
    internal abstract fun fetchNextSequences(roomUuid: String, nextId: Long): LiveData<NEResult<NEEduSequenceList>>

    /**
     * 下行 merge: true,表示与之前的状态合并， false： 状态替换
     */
    internal abstract fun updateRoomStatesChange(eduRoomStates: NEEduRoomStates, merge: Boolean)

    /**
     * 房间状态回调
     */
    abstract fun onRoomStatesChange(): MediatorLiveData<NEEduRoomStates>

    /**
     * 上行
     *
     * @param roomUuid
     * @param key
     * @param value
     * @return
     */
    internal abstract fun updateProperties(roomUuid: String, key: String, value: Int): LiveData<NEResult<Void>>

    /**
     *网络变更通知
     */
    abstract fun onNetworkQualityChange(): LiveData<Array<out NERtcNetworkQualityInfo>>
}
