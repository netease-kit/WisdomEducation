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
 * Methods for audio and videos
 *
 */
abstract class NEEduRtcService : INEEduService() {

    /**
     * Set to enable or disable audio or video without plugging or unplugging devices
     *
     * @param videoEnabled Enable or disable local video
     * @param videoEnabled Enable or disable local audio
     */
    abstract fun localUserVideoAudioEnable(
        videoEnabled: Boolean,
        audioEnabled: Boolean,
    ): LiveData<Pair<NEResult<Void>, NEResult<Void>>>

    /**
     * Enable or disable video without plugging or unplugging devices
     *
     * @param videoEnabled Enable or disable local video
     */
    abstract fun localUserVideoEnable(videoEnabled: Boolean): LiveData<NEResult<Void>>

    /**
     * enable or disable audio without plugging or unplugging devices
     *
     * @param audioEnabled Enable or disable local audio
     */
    abstract fun localUserAudioEnable(audioEnabled: Boolean): LiveData<NEResult<Void>>

    /**
     * The teacher enables or disables remote videos
     *
     * @param userId
     * @param videoEnabled
     * @return
     */
    abstract fun remoteUserVideoEnable(
        userId: String,
        videoEnabled: Boolean,
    ): LiveData<NEResult<Void>>

    /**
     * The teacher enables or disables remote audios
     *
     * @param userId
     * @param audioEnabled
     * @return
     */
    abstract fun remoteUserAudioEnable(
        userId: String,
        audioEnabled: Boolean,
    ): LiveData<NEResult<Void>>


    /**
     * Mute all member audios
     *
     * @param roomUuid
     * @param state
     * @return
     */
    abstract fun muteAllAudio(roomUuid: String, state: Int): LiveData<NEResult<Void>>

    /**
     * The mute state changes
     */
    abstract fun onMuteAllAudio(): LiveData<Boolean>

    /**
     * Update the mute all audio status
     */
    internal abstract fun updateMuteAllAudio(muteState: NEEduState)

    /**
     * Set audio by unplugging or plugging devices
     */
    abstract fun updateRtcAudio(member: NEEduMember)

    /**
     * Enable or diable local video by unplugging or plugging devices
     */
    abstract fun enableLocalVideo(member: NEEduMember)

    /**
     * Set the member stream
     */
    abstract fun updateRtcVideo(rtcView: ViewGroup?, member: NEEduMember)

    /**
     * Set substream video
     */
    abstract fun updateRtcSubVideo(rtcView: ViewGroup?, member: NEEduMember)

    /**
     * The stream state changes
     */
    abstract fun onStreamChange(): LiveData<Pair<NEEduMember, Boolean>>

    /**
     * Stream of a specified member changes
     */
    internal abstract fun updateStreamChange(member: NEEduMember, updateVideo: Boolean)

    /**
     * Remove the member streams
     */
    internal abstract fun updateStreamRemove(member: NEEduMember, updateVideo: Boolean)


    /**
     * The member snapshot, collecting the resources consumed by members that does not exist
     */
    internal abstract fun updateSnapshotMember(list: MutableList<NEEduMember>)

    /**
     * The stream changes if the member joins the room
     */
    internal abstract fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean)

    /**
     * The stream changes if the member leaves the room
     */
    internal abstract fun updateMemberLeave(list: MutableList<NEEduMember>)

    /**
     * The stream changes if the member leaves the stage
     */
    internal abstract fun updateMemberOffStageStreamChange(member: NEEduMember)

    /**
     * Leave the room
     */
    abstract fun leave()
}

