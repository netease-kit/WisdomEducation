/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.netease.neliveplayer.proxy.config.NEPlayerConfig
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.setOnClickThrottleFirst
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.NEEduErrorCode
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseChatView
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseMemberView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.MemberVideoListAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ConfirmDialog
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.ChatRoomFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.LiveClazzMembersFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.ZoomImageFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel.ChatRoomViewModel
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ClazzInfoView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ItemBottomView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.TitleView
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ActivityLiveClazzBinding
import com.netease.yunxin.app.wisdom.player.sdk.PlayerManager
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayer
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayerObserver
import com.netease.yunxin.app.wisdom.player.sdk.constant.CauseCode
import com.netease.yunxin.app.wisdom.player.sdk.model.*
import com.netease.yunxin.app.wisdom.player.sdk.view.AdvanceTextureView
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding
import com.netease.yunxin.kit.alog.ALog

class LiveClassActivity(layoutId: Int = R.layout.activity_live_clazz) : BaseClassActivity(layoutId), BaseChatView, BaseMemberView {
    private val tag: String = "LiveClassActivity"
    private val binding: ActivityLiveClazzBinding by viewBinding(R.id.one_container)

    lateinit var memberVideoAdapter: MemberVideoListAdapter

    private var clazzStart: Boolean = false

    private var clazzEnd: Boolean = false

    private val chatViewModel: ChatRoomViewModel by viewModels()

    private val chatRoomFragment: ChatRoomFragment = ChatRoomFragment()

    private val membersFragment: LiveClazzMembersFragment = LiveClazzMembersFragment()

    private lateinit var player: VodPlayer

    private var textureView: AdvanceTextureView? = null

    private var isReleasePlayer: Boolean = false

    companion object {

        val options by lazy {
            val options = VideoOptions()
            options.hardwareDecode = false
            /**
             * isPlayLongTimeBackground 控制退到后台或者锁屏时是否继续播放，开发者可根据实际情况灵活开发,我们的示例逻辑如下：
             * 使用软件解码：
             * isPlayLongTimeBackground 为 false 时，直播进入后台停止播放，进入前台重新拉流播放
             * isPlayLongTimeBackground 为 true 时，直播进入后台不做处理，继续播放,
             *
             * 使用硬件解码：
             * 直播进入后台停止播放，进入前台重新拉流播放
             */
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

    override fun getMembersFragment(): BaseFragment {
        return membersFragment
    }

    override fun getChatroomFragment(): BaseFragment {
        return chatRoomFragment
    }

    private val roomStatesChangeObserver = Observer<NEEduRoomStates> { updateRoomStates(it) }
    private val memberJoinObserver = Observer<List<NEEduMember>> {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)// keep screen on
        initVideoPlayer()
        initEduManager()
        initViews()
        registerObserver()
    }

    private fun initVideoPlayer() {
        val config = SDKOptions()
        config.privateConfig = NEPlayerConfig()
        PlayerManager.init(this, config)
        val sdkInfo: SDKInfo = PlayerManager.getSDKInfo(this)
        ALog.i(tag, "NESDKInfo:version" + sdkInfo.version.toString() + ",deviceId:" + sdkInfo.deviceId)
    }

    private fun initEduManager() {
        if (NEEduUiKit.instance?.neEduManager == null) {
            finish()
        }
        eduManager = NEEduUiKit.instance!!.neEduManager!!
        eduRoom = eduManager.getRoom()
        entryMember = eduManager.getEntryMember()
        eduManager.errorLD.observe(this, { t ->
            if (t != null && t != NEEduHttpCode.SUCCESS.code && !clazzEnd) {
                val tip = NEEduErrorCode.tipsWithErrorCode(baseContext, t)
                if (!TextUtils.isEmpty(tip)) {
                    ToastUtil.showLong(tip)
                } else {
                    ToastUtil.showLong(getString(R.string.left_class_due_to_network_problems))
                }
                NEEduUiKit.destroy()
                finish()
            }

        })
    }


    private fun registerObserver() {
        eduManager.getRoomService().onRoomStatesChange().observeForever(roomStatesChangeObserver)
        eduManager.getMemberService().onMemberJoin().observeForever(memberJoinObserver)
    }


    private fun initViews() {
        replaceFragment(R.id.layout_members, getMembersFragment())

        // 底部按钮
        binding.bottomView.apply {
            getHandsUp().visibility = View.GONE
            getHandsUpApply().visibility = View.GONE
            getAudio().visibility = View.GONE
            getVideo().visibility = View.GONE
            getShareScreen().visibility = View.GONE
            getBtnClazzCtrlLeft().visibility = View.GONE
            getBtnClazzCtrlRight().visibility = View.GONE
            getChatRoomView().visibility = View.VISIBLE
        }

        // 聊天室
        eduRoom.chatRoomId()?.let {
            if (!eduManager.roomConfig.is1V1()) {
                getChatroomFragment().let {
                    replaceFragment(R.id.layout_chat_room, it)
                }

                getChatRoomView().setOnClickListener {
                    showFragmentWithChatRoom()
                }

                chatViewModel.onUnreadChange().observe(this, {
                    getChatRoomView().setSmallUnread(it)
                })

                getChatRoomView().visibility = View.VISIBLE
            }
            binding.ivChatHide.setOnClickListener { hideFragmentWithChatRoom() }

            eduRoom.run {
                if(PreferenceUtil.lowLatencyLive) {
                    pullRtsUrl()
                } else {
                    pullRtmpUrl()
                }
            }?.apply {
                initVideo(this)
                renderVideo(binding.videoContainer)
            }
        }

        getMembersView().setOnClickListener {
            showFragmentWithMembers()
        }

        // 顶部返回按钮
        handleBackBtn(getBackView())

        // 课程状态 & 暂离 & 开始/结束课程
        initClazzViews(getClazzTitleView())
        getClazzTitleView().setClazzState(getString(R.string.class_did_not_start))


    }

    private fun updateRoomStates(states: NEEduRoomStates) {
        states.step?.value?.also {
            when (it) {
                NEEduRoomStep.START.ordinal -> {
                    // 保存回放请求参数
                    PreferenceUtil.recordPlay =
                        Pair(eduRoom.roomUuid, eduRoom.rtcCid)

                    getClazzTitleView().startClazzState(getString(R.string.having_class_now), states.duration ?: 0)
                    if (!clazzStart) {
                        getClassInitLayout().visibility = View.GONE
                        clazzStart = true
                    }
                    startPlayer()
                }
                NEEduRoomStep.END.ordinal -> {
                    hideFragmentWithChatRoom()
                    hideFragmentWithMembers()
                    getClazzTitleView().apply { setFinishClazzState(getClazzDuration()) }
                    getClassFinishReplay().setOnClickThrottleFirst {

                    }
                    getClassFinishBackView().setOnClickThrottleFirst {
                        onBackClicked()
                    }
                    getClazzFinishLayout().visibility = View.VISIBLE
                    getClassInitLayout().visibility = View.GONE
                    clazzStart = true
                    clazzEnd = true
                    releasePlayer()
                }
                else -> {
                    getClazzTitleView().setClazzState(getString(R.string.class_did_not_start))
                }
            }
        }
    }

    fun isSelf(member: NEEduMember): Boolean {
        return eduManager.isSelf(member.userUuid)
    }

    private fun replaceFragment(id: Int, baseFragment: BaseFragment) {
        supportFragmentManager.beginTransaction().replace(id, baseFragment).commitNow()
    }

    private fun addFragment(id: Int, baseFragment: BaseFragment) {
        supportFragmentManager.beginTransaction().add(id, baseFragment).commitNowAllowingStateLoss()
    }

    private fun removeFragment(baseFragment: BaseFragment) {
        supportFragmentManager.beginTransaction().remove(baseFragment).commitAllowingStateLoss()
    }

    private fun handleBackBtn(view: View) {
        view.setOnClickThrottleFirst {
            // may be show quit dialog
            onBackClicked()
        }
    }

    override fun updateUnReadCount() {}


    private fun initClazzViews(
        title: TitleView
    ) {
        eduManager.getRoomService().onCurrentRoomInfo().observe(this@LiveClassActivity, { room ->
            title.setClazzName(room.roomName)
            title.setClazzInfoClickListener {
                getClazzInfoLayout().let {
                    it.setRoomName(room.roomName)
                    eduManager.getMemberService().getMemberList().firstOrNull { it1 -> it1.isHost() }
                        ?.let { it2 -> it.setTeacherName(it2.userName) }
                    room.roomUuid.let { it3 ->
                        it.setRoomId(it3)
                        it.setOnCopyText(it3)
                    }
                    it.show()
                }
            }

        })
    }

    override fun showFragmentWithChatRoom() {
        getIMLayout().visibility = View.VISIBLE
    }

    override fun hideFragmentWithChatRoom() {
        getIMLayout().visibility = View.GONE
        chatViewModel.clearUnread()
    }

    override fun showFragmentWithMembers() {
        getMembersFragment().let {
            getMembersLayout().visibility = View.VISIBLE
        }
    }

    override fun hideFragmentWithMembers() {
        getMembersFragment().let {
            getMembersLayout().visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        onBackClicked()
    }

    private fun onBackClicked() {
        if (clazzEnd) {
            destroy()
        } else {
            ConfirmDialog.show(this,
                getString(R.string.sure_leave_class),
                getString(R.string.leave_class_student_tips),
                cancelable = true,
                cancelOnTouchOutside = true,
                callback = object : ConfirmDialog.Callback {
                    override fun result(boolean: Boolean?) {
                        if (boolean == true) {
                            destroy()
                        }
                    }
                })
        }
    }

//    fun toastOperateSuccess() {
//        ToastUtil.showShort(R.string.operation_successful)
//    }

    private fun unRegisterObserver() {
        eduManager.getRoomService().onRoomStatesChange().removeObserver(roomStatesChangeObserver)
    }

    fun getClazzTitleView(): TitleView {
        return binding.titleLayout
    }

    private fun getClazzInfoLayout(): ClazzInfoView {
        return binding.clazzInfoView
    }

    override fun getChatRoomView(): ItemBottomView {
        return binding.bottomView.getChatRoom()
    }

    override fun getMembersView(): View {
        return binding.bottomView.getMembers()
    }

    override fun getIMLayout(): View {
        return binding.layoutIm
    }

    override fun getMembersLayout(): View {
        return binding.layoutMembers
    }

    private fun getClassInitLayout(): View {
        return binding.rlClassInit
    }

    private fun getClassFinishReplay(): View {
        return binding.btnClassFinishReplay
    }

    private fun getClassFinishBackView(): View {
        return binding.btnClassFinishBack
    }

    fun getClazzFinishLayout(): View {
        return binding.layoutClassFinish
    }

    private fun getBackView(): View {
        return binding.titleLayout.getBackTv()
    }

    override fun getZoomImageLayout(): View {
        return binding.layoutZoomImage
    }

    private var zoomImageFragment: BaseFragment? = null

    override fun showZoomImageFragment(message: ChatRoomMessage) {
        getZoomImageLayout().visibility = View.VISIBLE
        zoomImageFragment = ZoomImageFragment().also {
            it.arguments = Bundle().apply { putSerializable(ZoomImageFragment.INTENT_EXTRA_IMAGE, message) }
            replaceFragment(R.id.layout_zoom_image, it)
        }
    }

    override fun hideZoomImageFragment() {
        getZoomImageLayout().visibility = View.GONE
        zoomImageFragment?.let {
            removeFragment(it)
        }
    }

    private fun initVideo(url: String) {
        player = PlayerManager.buildVodPlayer(this, url, options)
        player.registerPlayerObserver(playerObserver, true)

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
            if (code == CauseCode.CODE_VIDEO_PARSER_ERROR) {
                ALog.i(tag, "Video parsing error")
            } else {
                ALog.i(tag, "Play error, error code:$code")
            }
        }

        override fun onFirstVideoRendered() {
            ALog.i(tag, "The first frame of video has been parsed")
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
            ALog.i(tag, "Use the decoder type: ${if (value == 1) "Hardware decode" else "Soft decode"}")
        }

        override fun onStateChanged(stateInfo: StateInfo?) {
            ALog.i(tag, "onStateChanged ${stateInfo?.state}")
        }

        override fun onHttpResponseInfo(code: Int, header: String) {
            ALog.i(tag, "onHttpResponseInfo,code:$code header:$header")
        }
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

    private fun renderVideo(viewGroup: ViewGroup) {
        val videoView = pickVideoView()
        viewGroup.removeAllViews()
        viewGroup.addView(videoView)
        textureView = videoView
        player.setupRenderView(textureView, VideoScaleMode.FIT)
    }

    private fun startPlayer() {
        player.start()
    }

    private fun releasePlayer() {
        if(isReleasePlayer) {
            return
        }
        isReleasePlayer = true
        player.apply {
            registerPlayerObserver(playerObserver, false)
            setupRenderView(null, VideoScaleMode.NONE)
            stop()
        }
        textureView?.releaseSurface()
        textureView = null
    }

    fun destroy() {
        releasePlayer()
        unRegisterObserver()
        NEEduUiKit.destroy()
        finish()
    }
}