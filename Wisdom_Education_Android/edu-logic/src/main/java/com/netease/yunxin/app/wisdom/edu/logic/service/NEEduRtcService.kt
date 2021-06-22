/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduState

/**
 * 提供可供 App 调用的音视频相关方法
 *
 */
abstract class NEEduRtcService : INEEduService() {

    /**
     * 设置本地的音视频开关
     *
     * @param videoEnabled 本地视频开关
     * @param videoEnabled 本地音频开关
     */
    abstract fun localUserVideoAudioEnable(
        videoEnabled: Boolean,
        audioEnabled: Boolean,
    ): LiveData<Pair<NEResult<Void>, NEResult<Void>>>

    /**
     * 设置本地的音视频开关
     *
     * @param videoEnabled 本地视频开关
     */
    abstract fun localUserVideoEnable(videoEnabled: Boolean): LiveData<NEResult<Void>>

    /**
     * 设置本地的音视频开关
     *
     * @param audioEnabled 本地音频开关
     */
    abstract fun localUserAudioEnable(audioEnabled: Boolean): LiveData<NEResult<Void>>

    abstract fun remoteUserVideoEnable(
        userId: String,
        videoEnabled: Boolean,
    ): LiveData<NEResult<Void>>

    abstract fun remoteUserAudioEnable(
        userId: String,
        videoEnabled: Boolean,
    ): LiveData<NEResult<Void>>


    /**
     * 全体静音
     *
     * @param roomUuid
     * @param state
     * @return
     */
    abstract fun muteAllAudio(roomUuid: String, state: Int): LiveData<NEResult<Void>>

    /**
     * 静音状态发生变化
     */
    abstract fun onMuteAllAudio(): LiveData<Boolean>

    /**
     * 全局静音变更
     */
    internal abstract fun updateMuteAllAudio(muteState: NEEduState)

    /**
     * 变更rtc audio
     */
    abstract fun updateRtcAudio(member: NEEduMember)

    /**
     * 变更rtc video
     */
    abstract fun enableLocalVideo(member: NEEduMember)

    /**
     * 变更rtc video状态
     */
    abstract fun updateRtcVideo(rtcView: ViewGroup?, member: NEEduMember)

    /**
     * 变更rtc sub video状态
     */
    abstract fun updateRtcSubVideo(rtcView: ViewGroup?, member: NEEduMember)

    /**
     * 流状态发生变化
     */
    abstract fun onStreamChange(): LiveData<Pair<NEEduMember, Boolean>>

    /**
     * 对应的成员流的变更
     */
    internal abstract fun updateStreamChange(member: NEEduMember, updateVideo: Boolean)

    /**
     * 成员流的移除
     */
    internal abstract fun updateStreamRemove(member: NEEduMember, updateVideo: Boolean)


    /**
     * 成员快照，主要回收其他不存在的人的资源
     */
    internal abstract fun updateSnapshotMember(list: MutableList<NEEduMember>)

    /**
     * 成员离开流变更
     */
    internal abstract fun updateMemberLeave(list: MutableList<NEEduMember>)

    /**
     * 成员下台流变更
     */
    abstract fun updateMemberOffStageStreamChange(member: NEEduMember)

    /**
     * 离开音视频房间
     */
    abstract fun leave()
}

