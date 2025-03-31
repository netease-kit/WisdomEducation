/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.netease.neliveplayer.proxy.config.NEPlayerConfig
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.setOnClickThrottleFirst
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduSeatEventListener
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ConfirmDialog
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.LiveClazzMembersFragment
import com.netease.yunxin.app.wisdom.player.sdk.PlayerManager
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayer
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayerObserver
import com.netease.yunxin.app.wisdom.player.sdk.model.*
import com.netease.yunxin.app.wisdom.player.sdk.view.AdvanceTextureView
import com.netease.yunxin.app.wisdom.whiteboard.WhiteboardManager
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.model.NEWbAuth
import com.netease.yunxin.kit.alog.ALog

class LiveClassActivity : BigClazzStudentActivity(),
    NEEduSeatEventListener {

    private val tag: String = "LiveClassActivity"
    private var player: VodPlayer? = null
    private var textureView: AdvanceTextureView? = null
    private var isReleasePlayer: Boolean = false
    private var isRtcMode = false
    private val lastJoinList: MutableList<NEEduMember> = mutableListOf()
    private val membersFragment: LiveClazzMembersFragment = LiveClazzMembersFragment()
    private var retryPlayCount = 1
    private val retryCount = 3


    companion object {
        val options by lazy {
            val options = VideoOptions()
            options.hardwareDecode = false
            options.isPlayLongTimeBackground = false
            options.bufferStrategy = VideoBufferStrategy.ANTI_JITTER
            options.isAutoStart = true
            options
        }

        fun start(context: Context) {
            val intent = Intent(context, LiveClassActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val playerObserver: VodPlayerObserver = object : VodPlayerObserver {
        override fun onCurrentPlayProgress(
            currentPosition: Long,
            duration: Long,
            percent: Float,
            cachedPosition: Long
        ) {
        }

        override fun onSeekCompleted() {
            ALog.i(tag, "onSeekCompleted")
        }

        override fun onCompletion() {
        }

        override fun onAudioVideoUnsync() {
            ALog.i(tag, "Audio and video are out of sync")
        }

        override fun onNetStateBad() {}
        override fun onDecryption(ret: Int) {}
        override fun onPreparing() {}
        override fun onPrepared(info: MediaInfo) {
            ALog.i(tag, "onPrepared $info")
        }

        override fun onError(code: Int, extra: Int) {
            ALog.i(tag, "Video play error:$code")
            if (retryPlayCount > retryCount) {
                ToastUtil.showLong(getString(R.string.play_error))
                return
            }
            retryPlayCount++
            binding.videoContainer.postDelayed({
                releasePlayer()
                initVideoPlayer()
                startPlayer()
            }, 1000)

        }

        override fun onFirstVideoRendered() {
            ALog.i(tag, "The first frame of video has been parsed")
            retryPlayCount = 0
        }

        override fun onFirstAudioRendered() {

        }

        override fun onBufferingStart() {
        }

        override fun onBufferingEnd() {
        }

        override fun onBuffering(percent: Int) {
        }

        override fun onVideoDecoderOpen(value: Int) {
            ALog.i(
                tag,
                "Use the decoder type: ${if (value == 1) "Hardware decode" else "Soft decode"}"
            )
        }

        override fun onStateChanged(stateInfo: StateInfo?) {
            ALog.i(tag, "onStateChanged ${stateInfo?.state}")
        }

        override fun onHttpResponseInfo(code: Int, header: String) {
            ALog.i(tag, "onHttpResponseInfo,code:$code header:$header")
        }
    }

    override fun initViews() {
        initVideoPlayer()
        super.initViews()
        binding.layoutWhiteboard.visibility = View.GONE
        binding.rcvMemberVideo.visibility = View.GONE
        registerSeatListener()
        initHandUpState()
    }

    override fun classStart(states: NEEduRoomStates) {
        super.classStart(states)
        getAvHandsUpView().visibility = View.VISIBLE
        startPlayer()
    }

    private fun initVideoPlayer() {
        isReleasePlayer = false
        val config = SDKOptions()
        config.privateConfig = NEPlayerConfig()
        PlayerManager.init(this, config)
        val sdkInfo: SDKInfo = PlayerManager.getSDKInfo(this)
        ALog.i(
            tag,
            "NESDKInfo:version" + sdkInfo.version.toString() + ",deviceId:" + sdkInfo.deviceId
        )
        initVideo()
    }

    private fun initVideo(url: String) {
        player = PlayerManager.buildVodPlayer(this, url, options)
        player?.registerPlayerObserver(playerObserver, true)
    }

    private fun initVideo() {
        eduRoom.run {
            if (PreferenceUtil.lowLatencyLive) {
                pullRtsUrl()
            } else {
                pullRtmpUrl()
            }
        }?.apply {
            initVideo(this)
            renderVideo(binding.videoContainer)
        }
    }

    private fun renderVideo(viewGroup: ViewGroup) {
        val videoView = pickVideoView()
        viewGroup.removeAllViews()
        viewGroup.addView(videoView)
        textureView = videoView
        player?.setupRenderView(textureView, VideoScaleMode.FIT)
    }

    private fun pickVideoView(): AdvanceTextureView {
        val videoView1 = AdvanceTextureView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        }
        if (videoView1.parent != null) {
            (videoView1.parent as ViewGroup).removeView(videoView1)
        }
        return videoView1
    }

    private fun startPlayer() {
        if (!isRtcMode) {
            binding.videoContainer.visibility = View.VISIBLE
            player?.start()
        }
    }

    private fun releasePlayer() {
        if (isReleasePlayer) {
            return
        }
        isReleasePlayer = true
        player?.apply {
            registerPlayerObserver(playerObserver, false)
            setupRenderView(null, VideoScaleMode.NONE)
            stop()
        }
        textureView?.releaseSurface()
        textureView = null
    }

    private fun initHandUpState() {
        eduManager.getSeatService().getSeatRequestList(eduRoom.roomUuid)
            .observe(this, Observer { result ->
                if (result.success()) {
                    result.data?.firstOrNull {
                        it.userUuid == entryMember.userUuid
                    }?.apply {
                        eduManager.getSeatService().cancelSeatRequest(eduRoom.roomUuid,entryMember.userName)
                            .observe(this@LiveClassActivity) {}
                    }
                }
            })
        eduManager.getSeatService().getSeatInfo(eduRoom.roomUuid).observe(this, Observer { result ->
            if (result.success()) {
                result.data?.seatIndexList?.firstOrNull {
                    it.userUuid == entryMember.userUuid
                }?.apply {
                    userUuid?.apply {
                        eduManager.getRoomService().leaveClassroom(this)
                            .observe(this@LiveClassActivity) {}
                    }
                }
            }
        })
    }

    private fun registerSeatListener() {
        eduManager.getSeatService().addSeatListener(this)
    }

    private fun unregisterSeatListener() {
        eduManager.getSeatService().removeSeatListener(this)
    }

    private fun resetHandsUpState() {
        getAvHandsUpView().visibility = View.VISIBLE
        getAvHandsUpView().isSelected = true
        getLeaveClazzView()?.visibility = View.GONE
        getAvHandsUpOffstageView().visibility = View.GONE
        getScreenShareView().visibility = View.GONE
        getScreenShareCoverView().visibility = View.GONE

        // Reset the state: streaming/whiteboard/raise hand/whiteboard/screenshare
        eduManager.getMemberService().getLocalUser()?.apply {
            if (hasSubVideo()) {
                stopLocalShareScreen()
            }
            eduManager.getRtcService().updateRtcAudio(this)
            eduManager.getRtcService().enableLocalVideo(this)
            eduManager.getBoardService().setEnableDraw(false)
        }

        getAudioView().visibility = View.GONE
        getVideoView().visibility = View.GONE
    }

    override fun switchStuLocalHandsUp() {
        eduManager.getMemberService().getLocalUser()?.apply {
            if (!getAvHandsUpView().isSelected) {
                ConfirmDialog.show(this@LiveClassActivity,
                    getString(R.string.cancel_hands_up),
                    getString(R.string.sure_to_cancel_your_hand),
                    cancelable = true,
                    cancelOnTouchOutside = true,
                    callback = object : ConfirmDialog.Callback {
                        override fun result(boolean: Boolean?) {
                            if (boolean == true)
                                cancelStudentHandsUp()
                        }
                    })

            } else {
                ConfirmDialog.show(this@LiveClassActivity,
                    this@LiveClassActivity.getString(R.string.hands_up_apply),
                    this@LiveClassActivity.getString(R.string.hands_up_apply_msg),
                    cancelable = true,
                    cancelOnTouchOutside = true,
                    callback = object : ConfirmDialog.Callback {
                        override fun result(boolean: Boolean?) {
                            if (boolean == true)
                                applyStudentHandsUp()
                        }
                    })
            }
        }
    }


    override fun applyStudentHandsUp() {
        eduManager.getSeatService().submitSeatRequest(eduRoom.roomUuid,entryMember.userName).observe(this) { t ->
            if (!t.success()) {
                ToastUtil.showShort(getString(R.string.operate_fail))
                ALog.w("fail to apply hands up ${t.code}")
            }
        }
    }

    override fun cancelStudentHandsUp() {
        eduManager.getSeatService().cancelSeatRequest(eduRoom.roomUuid,entryMember.userName).observe(this) { t ->
            if (!t.success()) {
                ToastUtil.showShort(getString(R.string.operate_fail))
                ALog.w("fail to cancel hands up ${t.code}")
            }
        }
    }

    override fun offStageStudentLocal() {
        eduManager.getRoomService().leaveClassroom(eduManager.getEntryMember().userUuid)
            .observe(this) { t ->
                if (!t.success()) {
                    ToastUtil.showShort(getString(R.string.operate_fail))
                    ALog.w("fail to leaveClassroom ${t.code}")
                } else {
                    resetHandsUpState()
                    NEEduManager.classOptions.roleType = NEEduRoleType.BROADCASTER
                    NEEduManager.classOptions.sceneType = NEEduSceneType.LIVE_SIMPLE
                    NEEduManager.classOptions.isRtcRoom = false
                    eduManager.getRtcService().leave()
                    cdnMode()
                }
            }
    }

    private fun showOffStageDialog() {
        ConfirmDialog.show(this,
            getString(R.string.off_stage),
            getString(R.string.offstage_confirm_message),
            cancelable = true,
            cancelOnTouchOutside = true,
            ok = getString(R.string.confirm),
            callback = object : ConfirmDialog.Callback {
                override fun result(boolean: Boolean?) {
                    if (boolean == true)
                        offStageStudentLocal()
                }
            })
    }


    override fun onSeatRequestSubmitted(user: String) {
        if (!clazzStart) return
        if (eduManager.isSelf(user)) {
            getAvHandsUpView().visibility = View.VISIBLE
            getAvHandsUpView().isSelected = false
            getLeaveClazzView()?.visibility = View.GONE
            getAvHandsUpOffstageView().visibility = View.GONE

            getAudioView().visibility = View.GONE
            getVideoView().visibility = View.GONE
        }
    }

    override fun onSeatRequestCancelled(user: String) {
        if (!clazzStart) return
        if (eduManager.isSelf(user)) {
            resetHandsUpState()
        }
    }

    override fun onSeatRequestApproved(user: String, operateBy: String) {
        if (!clazzStart) return
        if (eduManager.isSelf(user)) {
            switchRtcClass(user)
        }
    }

    override fun onSeatRequestRejected(user: String, operateBy: String) {
        if (!clazzStart) return
        if (eduManager.isSelf(user)) {
            ToastUtil.showLong(R.string.hands_up_has_been_rejected)
            resetHandsUpState()
        }
    }

    override fun onSeatLeave(user: String) {
    }

    override fun onSeatKicked(user: String, operateBy: String) {
        if (!clazzStart) return
        if (eduManager.isSelf(user)) {
            ToastUtil.showLong(R.string.teacher_finished_your_stage_operation)
            offStageStudentLocal()
        }
    }

    override fun onSeatListChanged(seats: List<NESeatItem>) {
        onStageListChange()
    }

    override fun getMembersFragment(): BaseFragment? {
        return membersFragment
    }

    private fun switchRtcClass(user: String) {
        lastJoinList.clear()
        lastJoinList.addAll(eduManager.getMemberService().getMemberList())
        NEEduManager.classOptions.roleType = NEEduRoleType.AUDIENCE
        NEEduManager.classOptions.isRtcRoom = true
        isRtcMode = true
        eduManager.enterNormalClass(NEEduManager.classOptions).observe(this, Observer {
            if (it.success()) {
                ALog.i(tag, "join room success")
                updateLocalUserVideoAudio(
                    videoEnabled = true,
                    audioEnabled = true
                ).observe(this, {})
                eduManager.getHandsUpService()
                    .handsUpStateChange(NEEduHandsUpStateValue.TEACHER_ACCEPT, user)
                    .observe(this,
                        Observer {
                            if (it.success()) {
                                ALog.i(tag, "update handup properties success")
                                rtcMode()
                            } else {
                                ALog.i(tag, "update handup properties success fail")
                            }
                        })
            } else {
                NEEduManager.classOptions.roleType = NEEduRoleType.BROADCASTER
                NEEduManager.classOptions.isRtcRoom = false
                isRtcMode = false
                ALog.i(tag, "join room flail")
            }
        })
    }

    override fun onScreenShareChange(t: List<NEEduMember>) {
        if(isRtcMode) {
            super.onScreenShareChange(t)
        }
    }

    private fun cdnMode() {
        isRtcMode = false
        binding.layoutWhiteboard.visibility = View.GONE
        binding.videoContainer.visibility = View.VISIBLE
        binding.rcvMemberVideo.visibility = View.GONE
        binding.layoutShareVideo.visibility = View.GONE
        binding.layoutShareVideo.removeAllViews()
        initVideoPlayer()
        startPlayer()
    }

    private fun rtcMode() {
        isRtcMode = true
        releasePlayer()
        rtcModeUI()
        reloadWhiteboard()
        eduManager.getMemberService().mergeMemberList(lastJoinList)
    }

    private fun rtcModeUI() {
        binding.layoutWhiteboard.visibility = View.VISIBLE
        binding.videoContainer.visibility = View.GONE
        binding.rcvMemberVideo.visibility = View.VISIBLE
        getAudioView().visibility = View.VISIBLE
        getVideoView().visibility = View.VISIBLE
        getAvHandsUpView().visibility = View.GONE
        getAvHandsUpOffstageView().apply {
            visibility = View.VISIBLE
            setOnClickThrottleFirst {
                showOffStageDialog()
            }
        }
        getLeaveClazzView()?.text = getString(R.string.leave_class)
    }

    private fun reloadWhiteboard() {
        eduManager.apply {
            val config = WhiteboardConfig(
                eduLoginRes.imKey,
                getEntryMember().rtcUid,
                buildWbAuth(getWbAuth()),
                eduLoginRes.userUuid,
                eduLoginRes.imToken,
                getRoom().whiteBoardCName()!!,
                "",
                isHost(),
                null
            )
            whiteboardFragment.setWhiteboardConfig(config)
            WhiteboardManager.reload()
        }
    }

    private fun buildWbAuth(auth: NEEduWbAuth?): NEWbAuth? {
        return auth?.let {
            NEWbAuth(auth.checksum, auth.curTime, auth.nonce)
        }
    }

    override fun destroy() {
        if (isRtcMode) {
            eduManager.getRoomService().leaveClassroom(eduManager.getEntryMember().userUuid)
                .observe(this) {}
        }
        releasePlayer()
        super.destroy()
    }

    override fun onDestroy() {
        unregisterSeatListener()
        super.onDestroy()
    }

}