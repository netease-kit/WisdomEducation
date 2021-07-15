/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.netease.lava.nertc.sdk.video.NERtcVideoView
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.logic.net.service.BaseService
import com.netease.yunxin.app.wisdom.edu.logic.net.service.RoomServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.StreamServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.UserServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.BatchReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduMemberPropertiesType
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduUpdateMemberPropertyReq
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduRtcService
import com.netease.yunxin.app.wisdom.edu.logic.service.widget.NEEduRtcVideoViewPool

/**
 * Created by hzsunyj on 2021/5/20.
 */
internal class NEEduRtcServiceImpl : NEEduRtcService() {

    private val streamChangeLD: MediatorLiveData<Pair<NEEduMember, Boolean>> = MediatorLiveData()

    private val gson = Gson()

    // remember last value , otherwise when network reconnect will notify more
    private var lastMuteAllAudioTime: Long? = null

    private val muteAllAudioLD: MediatorLiveData<Boolean> = MediatorLiveData()

    override fun localUserVideoEnable(videoEnabled: Boolean): LiveData<NEResult<Void>> {
        return if (videoEnabled) {
            StreamServiceRepository.updateStreamInfo(
                NEEduManagerImpl.eduEntryRes.room.roomUuid, NEEduManagerImpl.getEntryMember().userUuid,
                NEEduStreamType.VIDEO.type, CommonReq(value = NEEduStateValue.OPEN)
            ).map {
                NEResult(it.code)
            }
        } else {
            StreamServiceRepository.deleteStream(
                NEEduManagerImpl.eduEntryRes.room.roomUuid,
                NEEduManagerImpl.getEntryMember().userUuid, NEEduStreamType.VIDEO.type
            ).map {
                NEResult(it.code)
            }
        }
    }

    override fun localUserAudioEnable(audioEnabled: Boolean): LiveData<NEResult<Void>> {
        return if (audioEnabled) {
            StreamServiceRepository.updateStreamInfo(
                NEEduManagerImpl.eduEntryRes.room.roomUuid, NEEduManagerImpl.getEntryMember().userUuid,
                NEEduStreamType.AUDIO.type, CommonReq(value = NEEduStateValue.OPEN)
            ).map {
                NEResult(it.code)
            }
        } else {
            StreamServiceRepository.deleteStream(
                NEEduManagerImpl.eduEntryRes.room.roomUuid,
                NEEduManagerImpl.getEntryMember().userUuid, NEEduStreamType.AUDIO.type
            ).map {
                NEResult(it.code)
            }
        }
    }

    override fun localUserVideoAudioEnable(
        videoEnabled: Boolean,
        audioEnabled: Boolean,
    ): LiveData<Pair<NEResult<Void>, NEResult<Void>>> {

        val bodyParam = JsonArray()
        val method = if (videoEnabled) NEEduHttpMethod.PUT.method else NEEduHttpMethod.DELETE.method
        val operationId =
            if (videoEnabled) BaseService.UPDATE_STREAM_INFO else BaseService.DELETE_STREAM
        val videoParam =
            NEEduBatchStream(
                method,
                operation = NEEduBatchParamKey.MEMBER_STREAMS,
                operationId,
                userUuid = NEEduManagerImpl.getEntryMember().userUuid,
                key = NEEduStreamType.VIDEO.type,
                value = if (videoEnabled) NEEduState(value = NEEduStateValue.OPEN) else null
            )
        bodyParam.add(gson.toJsonTree(videoParam))
        val method1 = if (audioEnabled) NEEduHttpMethod.PUT.method else NEEduHttpMethod.DELETE.method
        val operationId1 =
            if (audioEnabled) BaseService.UPDATE_STREAM_INFO else BaseService.DELETE_STREAM
        val audioParam =
            NEEduBatchStream(
                method1,
                operation = NEEduBatchParamKey.MEMBER_STREAMS,
                operationId1,
                userUuid = NEEduManagerImpl.getEntryMember().userUuid,
                key = NEEduStreamType.AUDIO.type,
                value = if (audioEnabled) NEEduState(value = NEEduStateValue.OPEN) else null
            )
        bodyParam.add(gson.toJsonTree(audioParam))
        val body = BatchReq(bodyParam)
        return StreamServiceRepository.batchStreams(NEEduManagerImpl.eduEntryRes.room.roomUuid, body).map {
            if (!it.success() || it.data == null || it.data!!.list.size != 2) {
                Pair(first = NEResult(it.code), second = NEResult(it.code))
            } else {
                val observableType = StreamServiceRepository.getMethodObservableType(operationId)
                val data: NEResult<Any> = BaseService.gson.fromJson(
                    BaseService.gson.toJson(it.data!!.list[0]),
                    TypeToken.get(observableType).type
                )
                val observableType1 = StreamServiceRepository.getMethodObservableType(operationId1)
                val data1: NEResult<Any> = BaseService.gson.fromJson(
                    BaseService.gson.toJson(
                        it.data!!
                            .list[1]
                    ), TypeToken.get(observableType1).type
                )
                Pair(first = NEResult(data.code), second = NEResult(data1.code))
            }
        }
    }

    override fun remoteUserVideoEnable(userId: String, videoEnabled: Boolean): LiveData<NEResult<Void>> {
        val req =
            NEEduUpdateMemberPropertyReq(
                video = if (videoEnabled) NEEduStateValue.OPEN else NEEduStateValue.CLOSE,
                value = NEEduStateValue.OPEN
            )
        return UserServiceRepository.updateProperty(
            NEEduManagerImpl.eduEntryRes.room.roomUuid,
            userId,
            NEEduMemberPropertiesType.STREAMAV.type,
            req
        )
    }

    override fun remoteUserAudioEnable(userId: String, videoEnabled: Boolean): LiveData<NEResult<Void>> {
        val req =
            NEEduUpdateMemberPropertyReq(
                audio = if (videoEnabled) NEEduStateValue.OPEN else NEEduStateValue.CLOSE,
                value = NEEduStateValue.OPEN
            )
        return UserServiceRepository.updateProperty(
            NEEduManagerImpl.eduEntryRes.room.roomUuid,
            userId,
            NEEduMemberPropertiesType.STREAMAV.type,
            req
        )
    }

    override fun muteAllAudio(roomUuid: String, state: Int): LiveData<NEResult<Void>> {
        val commonReq = CommonReq(state)
        return RoomServiceRepository.updateRoomStates(
            roomId = roomUuid,
            commonReq = commonReq,
            key = NEEduRoomStates.STATE_MUTEAUDIO
        )
    }

    override fun onMuteAllAudio(): LiveData<Boolean> {
        return muteAllAudioLD
    }

    override fun updateMuteAllAudio(muteState: NEEduState) {
        if (muteState.time != lastMuteAllAudioTime) {
            lastMuteAllAudioTime = muteState.time
            muteAllAudioLD.value = muteState.value == NEEduStateValue.OPEN
        }
    }

    override fun updateRtcAudio(member: NEEduMember) {
        val enable = member.hasAudio()
        if (NEEduManagerImpl.isSelf(member.userUuid)) {
            NEEduManagerImpl.rtcManager.setupLocalAudio(enable)
        } else {
            NEEduManagerImpl.rtcManager.setupRemoteAudio(member.rtcUid, enable)
        }
    }

    override fun enableLocalVideo(member: NEEduMember) {
        NEEduManagerImpl.rtcManager.enableLocalVideo(member.hasVideo())
    }

    override fun updateRtcVideo(rtcView: ViewGroup?, member: NEEduMember) {
        var videoView: NERtcVideoView? = null
        if (rtcView == null || !member.hasVideo()) {
            NEEduRtcVideoViewPool.recycleRtcVideo(member.rtcUid)
        } else {
            videoView = NEEduRtcVideoViewPool.obtainRtcVideo(member.rtcUid)
            rtcView.removeAllViews()
            rtcView.addView(videoView)
        }
        if (NEEduManagerImpl.isSelf(member.userUuid)) {
            NEEduManagerImpl.rtcManager.setupLocalVideo(videoView)
        } else {
            NEEduManagerImpl.rtcManager.setupRemoteVideo(videoView, member.rtcUid)
        }
    }

    override fun updateRtcSubVideo(rtcView: ViewGroup?, member: NEEduMember) {
        var videoView: NERtcVideoView? = null
        if (rtcView == null || !member.hasSubVideo()) {
            NEEduRtcVideoViewPool.recycleRtcSubVideo(member.rtcUid)
        } else {
            videoView = NEEduRtcVideoViewPool.obtainRtcSubVideo(member.rtcUid)
            rtcView.removeAllViews()
            rtcView.addView(videoView)
        }
        if (NEEduManagerImpl.isSelf(member.userUuid)) {
            NEEduManagerImpl.rtcManager.setupLocalSubVideo(videoView)
        } else {
            NEEduManagerImpl.rtcManager.setupRemoteSubVideo(videoView, member.rtcUid)
        }
    }

    override fun onStreamChange(): LiveData<Pair<NEEduMember, Boolean>> {
        return streamChangeLD
    }

    override fun updateStreamChange(member: NEEduMember, updateVideo: Boolean) {
        streamChangeLD.value = Pair(member, updateVideo)
    }

    override fun updateStreamRemove(member: NEEduMember, updateVideo: Boolean) {
        streamChangeLD.postValue(Pair(member, updateVideo))
    }

    override fun updateSnapshotMember(list: MutableList<NEEduMember>) {
        NEEduRtcVideoViewPool.batchRecycleWithoutKeepMember(list)
    }

    override fun updateMemberLeave(list: MutableList<NEEduMember>) {
        list.forEach {
            it.streams?.reset()
            updateRtcAudio(it)
            updateRtcVideo(null, member = it)
            updateRtcSubVideo(null, member = it)
        }
    }

    override fun updateMemberOffStageStreamChange(member: NEEduMember) {
        member.streams?.reset()
        updateRtcAudio(member)
        updateRtcVideo(null, member = member)
        updateRtcSubVideo(null, member = member)
    }

    override fun leave() {
        NEEduManagerImpl.rtcManager.leave()
    }

}