/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.rtc

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.media.projection.MediaProjection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.lava.nertc.impl.RtcCode
import com.netease.lava.nertc.sdk.*
import com.netease.lava.nertc.sdk.live.AddLiveTaskCallback
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamLayout
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamTaskInfo
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamUserTranscoding
import com.netease.lava.nertc.sdk.stats.*
import com.netease.lava.nertc.sdk.video.*
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.kit.alog.ALog

/**
 *
 */
object RtcManager : NERtcCallbackEx, NERtcStatsObserver {

    private const val TAG = "RtcManager"

    private var rtcEngine: NERtcEx? = null

    private lateinit var engine: NERtcEx

    val errorLD: MediatorLiveData<Int> = MediatorLiveData<Int>()

    val networkQualityLD: MediatorLiveData<Array<out NERtcNetworkQualityInfo>> = MediatorLiveData()

    private val rtcVideoPendingMap = mutableMapOf<Long, NERtcVideoView>()
    private val rtcSubVideoPendingMap = mutableMapOf<Long, NERtcVideoView>()
    private val rtcAudioPendingList: MutableList<Long> = mutableListOf()

    private val userList: MutableList<Long> = mutableListOf()

    /// Each call instantiates an instance. If the call ends, the instance will be destroyed.
    fun initEngine(
        context: Context,
        appKey: String,
        rtcAddresses: NERtcServerAddresses?
    ): LiveData<Boolean> {
        val initLD: MediatorLiveData<Boolean> = MediatorLiveData()
        val option = NERtcOption()
        rtcAddresses?.let { option.serverAddresses = it }
        option.logLevel = NERtcConstants.LogLevel.INFO
        rtcEngine = NERtcEx.getInstance()
        rtcEngine?.let {
            val parameters = NERtcParameters()
            parameters.set(NERtcParameters.KEY_AUTO_SUBSCRIBE_AUDIO, false)
//            parameters.set(NERtcParameters.KEY_SERVER_RECORD_AUDIO, false)
//            parameters.set(NERtcParameters.KEY_SERVER_RECORD_VIDEO, false)
            parameters.set(NERtcParameters.KEY_SERVER_RECORD_MODE, NERtcConstants.ServerRecordMode.MIX_AND_SINGLE)
            it.setParameters(parameters)
            try {
                it.init(context, appKey, this, option)
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
                ALog.e(TAG, "init engine exception ${e.message}")
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

    // 添加推流任务的异步callback
    var addLiveTaskCallback: AddLiveTaskCallback = AddLiveTaskCallback { taskId, errCode ->
        if (errCode == RtcCode.LiveCode.OK) {
            ALog.i(TAG, "添加推流任务成功 : taskId $taskId")
        } else {
            ALog.i(TAG, "添加推流任务失败 : taskId $taskId , errCode : $errCode")
        }
    }

    fun join(rtcToken: String?, channelName: String, rtcUid: Long, pushUrl: String?) {
        engine.joinChannel(rtcToken, channelName, rtcUid)
        pushUrl?.let {
            // 加入房间前设置直播模式
            // 0 - COMMUNICATION（通信模式），  1 - LIVE_BROADCASTING（直播模式）

            // 加入房间前设置直播模式
            // 0 - COMMUNICATION（通信模式），  1 - LIVE_BROADCASTING（直播模式）
            NERtcEx.getInstance().setChannelProfile(1)

            val enableVideo = true

            // 加入房间后添加推流任务。
            // 初始化推流任务

            // 加入房间后添加推流任务。
            // 初始化推流任务
            val liveTask1 = NERtcLiveStreamTaskInfo()
            //taskID 可选字母、数字，下划线，不超过64位
            //taskID 可选字母、数字，下划线，不超过64位
            liveTask1.taskId = java.lang.String.valueOf(Math.abs(pushUrl.hashCode()))
            // 设置推互动直播推流地址，一个推流任务对应一个推流房间
            // 设置推互动直播推流地址，一个推流任务对应一个推流房间
            liveTask1.url = pushUrl
            // 设置是否进行互动直播录制，请注意与音视频通话录制区分。
            // 设置是否进行互动直播录制，请注意与音视频通话录制区分。
            liveTask1.serverRecordEnabled = false
            // 设置推音视频流还是纯音频流
            // 设置推音视频流还是纯音频流
            liveTask1.liveMode =
                if (enableVideo) NERtcLiveStreamTaskInfo.NERtcLiveStreamMode.kNERtcLsModeVideo else NERtcLiveStreamTaskInfo.NERtcLiveStreamMode.kNERtcLsModeAudio

            //设置整体布局

            //设置整体布局
            val layout = NERtcLiveStreamLayout()
            layout.userTranscodingList = ArrayList()
            layout.width = 720 //整体布局宽度

            layout.height = 1280 //整体布局高度

            layout.backgroundColor = Color.parseColor("#3399ff") // 整体背景色

            liveTask1.layout = layout

            // 设置直播成员布局

            // 设置直播成员布局
            val user1 = NERtcLiveStreamUserTranscoding()
            user1.uid = rtcUid // 用户id

            user1.audioPush = true // 推流是否发布user1 的音频

            user1.videoPush = enableVideo // 推流是否发布user1的视频

            if (user1.videoPush) { // 如果发布视频，需要设置一下视频布局参数
                // user1 视频的缩放模式， 详情参考NERtcLiveStreamUserTranscoding 的API 文档
                user1.adaption = NERtcLiveStreamUserTranscoding.NERtcLiveStreamVideoScaleMode.kNERtcLsModeVideoScaleCropFill
                user1.x = 10 // user1 的视频布局x偏移，相对整体布局的左上角
                user1.y = 10 // user1 的视频布局y偏移，相对整体布局的左上角
                user1.width = 180 // user1 的视频布局宽度
                user1.height = 320 //user1 的视频布局高度
            }

            layout.userTranscodingList.add(user1)

//        // 设置第n位直播成员布局
//
//        // 设置第n位直播成员布局
//        val usern = NERtcLiveStreamUserTranscoding()
//        usern.uid = uidn
//        usern.audioPush = true
//        usern.videoPush = enableVideo
//        if (usern.videoPush) {
//            usern.adaption = NERtcLiveStreamUserTranscoding.NERtcLiveStreamVideoScaleMode.kNERtcLsModeVideoScaleCropFill
//            usern.x = user1.x + user1.width + 10
//            usern.y = user1.y + user1.height + 10
//            usern.width = 320
//            usern.height = 640
//        }
//        layout.userTranscodingList.add(usern)

            // 调用 addLiveStreamTask 接口添加推流任务

            // 调用 addLiveStreamTask 接口添加推流任务
            val ret = NERtcEx.getInstance().addLiveStreamTask(liveTask1, addLiveTaskCallback)
            if (ret != 0) {
                ALog.w("调用添加推流任务接口执行失败 ， ret : $ret")
            }
        }
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
    override fun onJoinChannel(code: Int, chenelId: Long, elapsed: Long, uid: Long) {
        ALog.i(TAG, "onJoinChannel $code")
        if (code != 0) {
            errorLD.postValue(code)
            return
        }
        engine.isSpeakerphoneOn = true
    }

    override fun onLeaveChannel(p0: Int) {
        ALog.i(TAG, "onLeaveChannel $p0")
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

    override fun onAudioDeviceStateChange(deviceType: Int, deviceState: Int) {
        val tipResId = if (deviceType == NERtcConstants.AudioDeviceType.RECORD) {
            R.string.audio_collection_device_abnormal
        } else {
            R.string.audio_player_device_abnormal
        }
        if (deviceState == NERtcConstants.AudioDeviceState.INIT_ERROR
            && deviceState == NERtcConstants.AudioDeviceState.START_ERROR
            && deviceState == NERtcConstants.AudioDeviceState.UNKNOWN_ERROR) {
            ToastUtil.showLong(tipResId)
        }
    }

    override fun onVideoDeviceStageChange(deviceState: Int) {
        if (deviceState == NERtcConstants.VideoDeviceState.DISCONNECTED
            && deviceState == NERtcConstants.VideoDeviceState.FREEZED
            && deviceState == NERtcConstants.VideoDeviceState.UNKNOWNERROR) {
            ToastUtil.showLong(R.string.video_device_exception)
        }
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

    override fun onLocalPublishFallbackToAudioOnly(p0: Boolean, p1: NERtcVideoStreamType?) {
    }

    override fun onRemoteSubscribeFallbackToAudioOnly(p0: Long, p1: Boolean, p2: NERtcVideoStreamType?) {
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
            engine.subscribeRemoteVideoStream(
                uid,
                NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh,
                remote != null
            )
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