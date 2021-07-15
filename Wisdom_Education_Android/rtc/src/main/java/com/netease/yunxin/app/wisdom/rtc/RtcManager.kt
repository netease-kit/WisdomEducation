/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.rtc

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.projection.MediaProjection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.lava.nertc.sdk.NERtcCallbackEx
import com.netease.lava.nertc.sdk.NERtcConstants
import com.netease.lava.nertc.sdk.NERtcEx
import com.netease.lava.nertc.sdk.NERtcParameters
import com.netease.lava.nertc.sdk.stats.*
import com.netease.lava.nertc.sdk.video.*
import com.netease.yunxin.kit.alog.ALog

/**
 * Created by hzsunyj on 2021/5/13.
 */
object RtcManager : NERtcCallbackEx, NERtcStatsObserver {

    private const val TAG = "RtcManager";

    private var rtcEngine: NERtcEx? = null

    private lateinit var engine: NERtcEx

    val errorLD: MediatorLiveData<Int> = MediatorLiveData<Int>()

    val networkQualityLD: MediatorLiveData<Array<out NERtcNetworkQualityInfo>> = MediatorLiveData()

    private val rtcVideoPendingMap = mutableMapOf<Long, NERtcVideoView>()
    private val rtcSubVideoPendingMap = mutableMapOf<Long, NERtcVideoView>()
    private val rtcAudioPendingList: MutableList<Long> = mutableListOf()

    private val userList: MutableList<Long> = mutableListOf()

    /// 音视频每一通是一个实例，用完就需要销毁
    fun initEngine(context: Context, appKey: String): LiveData<Boolean> {
        val initLD: MediatorLiveData<Boolean> = MediatorLiveData()
        rtcEngine = NERtcEx.getInstance()
        rtcEngine?.let {
            val parameters = NERtcParameters()
            parameters.set(NERtcParameters.KEY_AUTO_SUBSCRIBE_AUDIO, false)
            parameters.set(NERtcParameters.KEY_SERVER_RECORD_AUDIO, true)
            parameters.set(NERtcParameters.KEY_SERVER_RECORD_VIDEO, true)
            parameters.set(NERtcParameters.KEY_SERVER_RECORD_MODE, NERtcConstants.ServerRecordMode.MIX_AND_SINGLE)
            it.setParameters(parameters)
            try {
                it.init(context, appKey, this, null)
                val neRtcVideoConfig = NERtcVideoConfig()
                neRtcVideoConfig.width = 320
                neRtcVideoConfig.height = 240
                neRtcVideoConfig.frameRate = NERtcEncodeConfig.NERtcVideoFrameRate.FRAME_RATE_FPS_15
                neRtcVideoConfig.degradationPrefer =
                    NERtcVideoConfig.NERtcDegradationPreference.DEGRADATION_MAINTAIN_FRAMERATE
                it.setLocalVideoConfig(neRtcVideoConfig)
                it.enableLocalVideo(false)
                it.enableLocalAudio(false)
                it.enableAudioVolumeIndication(true, 2000)
                it.setAudioProfile(NERtcConstants.AudioProfile.STANDARD_EXTEND, NERtcConstants.AudioScenario.SPEECH)
            } catch (e: Exception) {
                ALog.e(TAG, "init engine exception ${e?.message}")
                rtcEngine = null
            }
        }
        if (rtcEngine != null) {
            engine = rtcEngine!!
            engine.setStatsObserver(this)
        }
        initLD.postValue(rtcEngine != null)
        return initLD
    }

    fun join(rtcToken: String?, channelName: String, rtcUid: Long) {
        engine.joinChannel(rtcToken, channelName, rtcUid)
    }

    fun leave() {
        engine.leaveChannel()
    }

    fun release() {
        if (this::engine.isInitialized) {
            engine.release()
        }
        errorLD.postValue(null)
        networkQualityLD.postValue(null)
        rtcSubVideoPendingMap.clear()
        rtcAudioPendingList.clear()
        rtcVideoPendingMap.clear()
        userList.clear()
    }

    /**
     * must join success
     */
    override fun onJoinChannel(code: Int, chenelId: Long, elapsed: Long) {
        ALog.i(TAG, "onJoinChannel $code")
        if (code != 0) {
            errorLD.postValue(code)
            return
        }
        engine.isSpeakerphoneOn = true
    }

    override fun onLeaveChannel(p0: Int) {
    }

    override fun onUserJoined(p0: Long) {
        ALog.i(TAG, "onUserJoined $p0")
        userList.add(p0)
    }

    override fun onUserLeave(p0: Long, p1: Int) {
        ALog.i(TAG, "onUserLeave $p0 reason $p1")
        userList.remove(p0)
        rtcAudioPendingList.remove(p0)
        rtcVideoPendingMap.remove(p0)
        rtcSubVideoPendingMap.remove(p0)
    }

    override fun onUserAudioStart(p0: Long) {
        ALog.i(TAG, "onUserAudioStart $p0")
        if (rtcAudioPendingList.contains(p0)) {
            engine.subscribeRemoteAudioStream(p0, true)
            rtcAudioPendingList.remove(p0)
        }
    }

    override fun onUserAudioStop(p0: Long) {
        ALog.i(TAG, "onUserAudioStop $p0")
        rtcAudioPendingList.remove(p0)
    }

    override fun onUserVideoStart(p0: Long, p1: Int) {
        ALog.i(TAG, "onUserVideoStart $p0 maxProfile $p1")
        if (rtcVideoPendingMap.containsKey(p0)) {
            engine.setupRemoteVideoCanvas(rtcVideoPendingMap[p0], p0)
            engine.subscribeRemoteVideoStream(p0, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true)
            rtcVideoPendingMap.remove(p0)
        }
    }

    override fun onUserVideoStop(p0: Long) {
        ALog.i(TAG, "onUserVideoStop $p0")
        rtcVideoPendingMap.remove(p0)
    }

    override fun onDisconnect(p0: Int) {
        if (p0 != 0) {
            ALog.e(TAG, "onDisconnect $p0")
            errorLD.postValue(p0)
        }
    }

    override fun onClientRoleChange(p0: Int, p1: Int) {
    }

    override fun onUserSubStreamVideoStart(p0: Long, p1: Int) {
        ALog.i(TAG, "onUserSubStreamVideoStop $p0 profile $p1")
        if (rtcSubVideoPendingMap.containsKey(p0)) {
            engine.setupRemoteSubStreamVideoCanvas(rtcSubVideoPendingMap[p0], p0)
            engine.subscribeRemoteSubStreamVideo(p0, true)
            rtcSubVideoPendingMap.remove(p0)
        }
    }

    override fun onUserSubStreamVideoStop(p0: Long) {
        ALog.i(TAG, "onUserSubStreamVideoStop $p0")
        rtcSubVideoPendingMap.remove(p0)
    }

    override fun onUserAudioMute(p0: Long, p1: Boolean) {
    }

    override fun onUserVideoMute(p0: Long, p1: Boolean) {
    }

    override fun onFirstAudioDataReceived(p0: Long) {
    }

    override fun onFirstVideoDataReceived(p0: Long) {
    }

    override fun onFirstAudioFrameDecoded(p0: Long) {
    }

    override fun onFirstVideoFrameDecoded(p0: Long, p1: Int, p2: Int) {
    }

    override fun onUserVideoProfileUpdate(p0: Long, p1: Int) {
    }

    override fun onAudioDeviceChanged(p0: Int) {
    }

    override fun onAudioDeviceStateChange(p0: Int, p1: Int) {
    }

    override fun onVideoDeviceStageChange(p0: Int) {
    }

    override fun onConnectionTypeChanged(p0: Int) {
    }

    override fun onReconnectingStart() {
    }

    override fun onReJoinChannel(p0: Int, p1: Long) {
        ALog.i(TAG, "onReJoinChannel $p0")
        if (p0 != 0) {
            errorLD.postValue(p0)
        }
    }

    override fun onAudioMixingStateChanged(p0: Int) {
    }

    override fun onAudioMixingTimestampUpdate(p0: Long) {
    }

    override fun onAudioEffectFinished(p0: Int) {
    }

    override fun onLocalAudioVolumeIndication(p0: Int) {
    }

    override fun onRemoteAudioVolumeIndication(p0: Array<out NERtcAudioVolumeInfo>?, p1: Int) {
    }

    override fun onLiveStreamState(p0: String?, p1: String?, p2: Int) {
    }

    override fun onConnectionStateChanged(p0: Int, p1: Int) {
    }

    override fun onCameraFocusChanged(p0: Rect?) {
    }

    override fun onCameraExposureChanged(p0: Rect?) {
    }

    override fun onRecvSEIMsg(p0: Long, p1: String?) {
    }

    override fun onAudioRecording(p0: Int, p1: String?) {
    }

    override fun onError(p0: Int) {
    }

    override fun onWarning(p0: Int) {
    }

    override fun onMediaRelayStatesChange(p0: Int, p1: String?) {
    }

    override fun onMediaRelayReceiveEvent(p0: Int, p1: Int, p2: String?) {
    }

    fun enableLocalVideo(enable: Boolean): Int {
        return engine.enableLocalVideo(enable)
    }

    fun setupLocalVideo(local: NERtcVideoView?): Int {
        //engine.enableLocalVideo(local != null)
        return engine.setupLocalVideoCanvas(local)
    }

    fun setupRemoteVideo(remote: NERtcVideoView?, uid: Long): Int {
        if (userList.contains(uid)) {
            engine.setupRemoteVideoCanvas(remote, uid)
            engine.subscribeRemoteVideoStream(uid,
                NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh,
                remote != null)
        } else if (remote != null) {
            rtcVideoPendingMap[uid] = remote
        }
        return 0
    }

    fun setupLocalSubVideo(local: NERtcVideoView?): Int {
        return engine.setupLocalSubStreamVideoCanvas(local)
    }

    fun setupRemoteSubVideo(remote: NERtcVideoView?, uid: Long): Int {
        if (userList.contains(uid)) {
            engine.setupRemoteSubStreamVideoCanvas(remote, uid)
            engine.subscribeRemoteSubStreamVideo(uid, remote != null)
        } else if (remote != null) {
            rtcSubVideoPendingMap[uid] = remote
        }
        return 0
    }

    fun setupLocalAudio(enable: Boolean) {
        engine.enableLocalAudio(enable)
    }

    fun setupRemoteAudio(rtcUid: Long, enable: Boolean) {
        if (userList.contains(rtcUid)) {
            engine.subscribeRemoteAudioStream(rtcUid, enable)
        } else if (enable) {
            rtcAudioPendingList.add(rtcUid)
        }
    }

    fun startScreenCapture(config: NERtcScreenConfig, intent: Intent, callback: MediaProjection.Callback): Int {
        return engine.startScreenCapture(config, intent, callback)
    }

    fun stopScreenCapture() {
        engine.stopScreenCapture()
    }

    override fun onRtcStats(p0: NERtcStats?) {
    }

    override fun onLocalAudioStats(p0: NERtcAudioSendStats?) {
    }

    override fun onRemoteAudioStats(p0: Array<out NERtcAudioRecvStats>?) {
    }

    override fun onLocalVideoStats(p0: NERtcVideoSendStats?) {
    }

    override fun onRemoteVideoStats(p0: Array<out NERtcVideoRecvStats>?) {
    }

    override fun onNetworkQuality(p0: Array<out NERtcNetworkQualityInfo>?) {
        p0?.let {
            networkQualityLD.postValue(p0)
        }
    }
}