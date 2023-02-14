/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.projection.MediaProjection
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netease.lava.api.model.RTCVideoProfile
import com.netease.lava.nertc.sdk.video.NERtcScreenConfig
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.setOnClickThrottleFirst
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.base.util.observeForeverOnce
import com.netease.yunxin.app.wisdom.edu.logic.NEEduErrorCode
import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundService
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.*
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.ItemClickListerAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.MemberVideoListAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ActionSheetDialog
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ConfirmDialog
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.WhiteboardFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.ZoomImageFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel.ChatRoomViewModel
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.RtcVideoAudioView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.TitleView
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ActivityNormalClazzBinding
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding
import com.netease.yunxin.kit.alog.ALog

abstract class BaseNormalClassActivity(layoutId: Int = R.layout.activity_normal_clazz) : BaseClassActivity(layoutId),
    BaseNormalClassView,
    BaseAdapter.OnItemChildClickListener<NEEduMember>, BaseChatView, BaseMemberView {
    private val tag: String = "BaseNormalClassActivity"
    protected val binding: ActivityNormalClazzBinding by viewBinding(R.id.one_container)

    companion object {
        private const val CAPTURE_PERMISSION_REQUEST_CODE = 0x1123

        private const val ACTION_AUDIO = 0
        private const val ACTION_VIDEO = 1
        private const val ACTION_REVOKE_WHITEBOARD = 2
        private const val ACTION_REVOKE_SHARE = 3
        private const val ACTION_OFFSTAGE = 4
    }

    open var whiteboardFragment: WhiteboardFragment = WhiteboardFragment()

    lateinit var memberVideoAdapter: MemberVideoListAdapter

    protected var clazzStart: Boolean = false

    private var clazzEnd: Boolean = false

    private val chatViewModel: ChatRoomViewModel by viewModels()

    override fun getMembersFragment(): BaseFragment? {
        return null
    }

    private val roomStatesChangeObserver = Observer<NEEduRoomStates> { updateRoomStates(it) }
    private val muteAllAudioObserver = Observer<Boolean> { it ->
        it.let {
            if (it) {
                val localMediaUser = eduManager.getMemberService().getLocalUser()
                if (!entryMember.isHost()) {
                    if (localMediaUser!!.hasAudio()) {
                        switchLocalAudio(false).observe(this@BaseNormalClassActivity, {})
                    }
                    ToastUtil.showShort(getString(R.string.all_have_been_muted))
                }
            }
        }
    }

    private val memberJoinObserver = Observer<List<NEEduMember>> { t -> onMemberJoin(t) }
    private val memberPropertiesChangeObserver = Observer<Pair<NEEduMember, NEEduMemberProperties>> { t ->
        onMemberPropertiesChange(t.first, t.second)
    }

    private val streamChangeObserver = Observer<Pair<NEEduMember, Boolean>> { t -> onStreamChange(t.first, t.second) }

    private val screenShareChangeObserver = Observer<List<NEEduMember>> { t -> onScreenShareChange(t) }

    private val boardPermissionObserver = Observer<NEEduMember> { t ->
        onBoardPermissionGranted(t)
    }

    private val shareScreenPermissionObserver = Observer<NEEduMember> { t ->
        onScreenSharePermissionGranted(t)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)// keep screen on
        initEduManager()
        initViews()
        registerObserver()
        loadData()
    }

    private fun initEduManager() {
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

    open fun registerObserver() {
        eduManager.getRoomService().onRoomStatesChange().observeForever(roomStatesChangeObserver)
        eduManager.getRtcService().onMuteAllAudio().observeForever(muteAllAudioObserver)
        eduManager.getMemberService().onMemberJoin().observeForever(memberJoinObserver)
        eduManager.getMemberService().onMemberPropertiesChange().observeForever(memberPropertiesChangeObserver)
        eduManager.getRtcService().onStreamChange().observeForever(streamChangeObserver)
        eduManager.getShareScreenService().onScreenShareChange().observeForever(screenShareChangeObserver)
        eduManager.getBoardService().onPermissionGranted().observeForever(boardPermissionObserver)
        eduManager.getShareScreenService().onPermissionGranted().observeForever(shareScreenPermissionObserver)
    }


    open fun initViews() {
        val self = eduManager.getMemberService().getLocalUser()!!
        getMembersFragment()?.let {
            replaceFragment(R.id.layout_members, it)
        }

        // Bottom button
        getVideoView().visibility = View.GONE
        getAudioView().visibility = View.GONE
        getVideoView().setOnClickThrottleFirst {
            switchLocalVideo().observe(this@BaseNormalClassActivity, { toastOperateSuccess() })
        }
        getAudioView().setOnClickThrottleFirst {
            switchLocalAudio().observe(this@BaseNormalClassActivity, { toastOperateSuccess() })
        }
        getScreenShareView().setOnClickThrottleFirst {
            if (!clazzStart) {
                ToastUtil.showShort(getString(R.string.start_class_first))
            } else switchLocalShareScreen()
        }
        eduManager.roomConfig.memberStreamsPermission()?.apply {
            video?.let { it ->
                if (it.hasPermission(self.role)) {
                    getVideoView().visibility = View.VISIBLE
                    getVideoView().isSelected = self.hasVideo()
                }
            }

            audio?.let { it ->
                if (it.hasPermission(self.role)) {
                    getAudioView().visibility = View.VISIBLE
                    getAudioView().isSelected = self.hasAudio()
                }
            }
            subVideo?.let { it ->
                if (it.hasPermission(self.role)) {
                    getScreenShareView().visibility = View.VISIBLE
                    getScreenShareView().isSelected = !self.hasSubVideo()
                }
            }
        }

        // Whiteboard
        eduRoom.whiteBoardCName()?.let {
            replaceFragment(R.id.layout_whiteboard, whiteboardFragment)
        }

        // Chat room
        eduRoom.chatRoomId()?.let {
            if (!eduManager.roomConfig.is1V1()) {
                getChatroomFragment()?.let {
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
        }

        getMembersView().setOnClickListener {
            showFragmentWithMembers()
        }

        // The back button at the top
        handleBackBtn(getBackView())

        // RTC list at the right side
        val rcvMemberVideo = getMemberVideoRecyclerView()
        val layoutManager = LinearLayoutManager(this)
        rcvMemberVideo.layoutManager = layoutManager
        rcvMemberVideo.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.common_dp_4))
        )
        memberVideoAdapter = MemberVideoListAdapter(this, mutableListOf())
        memberVideoAdapter.setOnItemChildClickListener(this)
        rcvMemberVideo.adapter = memberVideoAdapter

        // Class state & paused & start/end class
        getChangeClazzStateView()?.visibility = View.GONE
        getLeaveClazzView()?.visibility = View.GONE
        eduManager.roomConfig.roomStatesPermission()?.apply {
            step.let {
                if (it.hasPermission(self.role)) {
                    getChangeClazzStateView()?.visibility = View.VISIBLE
                }
            }
            pause?.let {
                if (it.hasPermission(self.role)) {
                    getLeaveClazzView()?.visibility = View.GONE // TODO Leave class
                }
            }
        }

        initClazzViews(
            getClazzTitleView(),
            getChangeClazzStateView(),
            getLeaveClazzView()
        )
        getClazzTitleView().setClazzState(getString(R.string.class_did_not_start))
        getChangeClazzStateView()?.apply {
            text = getString(R.string.classes_begin)
            isSelected = true
            setOnClickThrottleFirst {
                startClazz()
            }
        }

    }

    override fun onItemChildClick(adapter: BaseAdapter<NEEduMember>?, view: View?, position: Int) {
        when (view!!.id) {
            R.id.video_container -> {
                adapter?.let { it1 ->
                    it1.getItem(position).apply {
                        if (eduManager.isSelf(userUuid)) {
                            showLocalActionSheetDialog(this)
                        } else if (!isHolder() && entryMember.isHost()) {
                            showRemoteFullActionSheetDialog(this, true)
                        }
                    }
                }
            }
        }
    }

    /**
     * Spacing between items in the RTC list
     *
     * @property spaceSize
     */
    class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    top = spaceSize
                }
            }
        }
    }

    private fun updateRoomStates(states: NEEduRoomStates) {
        states.step?.value?.also {
            when (it) {
                NEEduRoomStep.START.ordinal -> {
                    // Save playback request parameters
                    classStart(states)
                }
                NEEduRoomStep.END.ordinal -> {
                    classEnd()
                }
                else -> {
                    getClazzTitleView().setClazzState(getString(R.string.class_did_not_start))
                    getChangeClazzStateView()?.apply {
                        text = getString(R.string.classes_begin)
                        isSelected = true
                        setOnClickThrottleFirst {
                            startClazz()
                        }
                    }
                }
            }
        }
    }

    open fun classEnd() {
        hideFragmentWithChatRoom()
        hideFragmentWithMembers()
        eduManager.getRtcService().leave()
        eduManager.getBoardService().dispose()
        getClazzTitleView().apply { setFinishClazzState(getClazzDuration()) }
        getClassFinishReplay().setOnClickThrottleFirst {
            TODO("Not yet implemented")
        }
        getClassFinishBackView().setOnClickThrottleFirst {
            onBackClicked()
        }
        getClazzFinishLayout().visibility = View.VISIBLE
        getClassInitLayout().visibility = View.GONE
        clazzStart = true
        clazzEnd = true
    }

    open fun classStart(states: NEEduRoomStates) {
        PreferenceUtil.recordPlay =
            Pair(eduRoom.roomUuid, eduRoom.rtcCid)

        getClazzTitleView().startClazzState(
            getString(R.string.having_class_now),
            states.duration ?: 0
        )
        if (!clazzStart) {
            getChangeClazzStateView()?.apply {
                text = getString(R.string.end_class)
                isSelected = false
                setOnClickThrottleFirst {
                    finishClazz()
                }
            }
            getClassInitLayout().visibility = View.GONE
            if (eduManager.roomConfig.isBig() && !entryMember.isHost()) {
                getAvHandsUpView().visibility = View.VISIBLE
            }
            clazzStart = true
        }
    }

    open fun onMemberJoin(t: List<NEEduMember>) {
    }

    private fun onMemberPropertiesChange(member: NEEduMember, properties: NEEduMemberProperties) {
        // Refresh the whiteboard and screen share
        memberVideoAdapter.refreshDataAndNotify<Boolean>(member, false)
        if (!isSelf(member) || member.isHost()) return
        properties.streamAV?.also {
            var videoEnabled: Boolean? = null
            it.video?.let { t ->
                videoEnabled = t == NEEduStateValue.OPEN
            }
            var audioEnabled: Boolean? = null
            it.audio?.let { t ->
                audioEnabled = t == NEEduStateValue.OPEN
            }
            if (videoEnabled == null && audioEnabled == null) {
                return
            }
            if (videoEnabled != null && audioEnabled != null) {
                updateLocalUserVideoAudio(videoEnabled!!, audioEnabled!!).observeForeverOnce { }
            } else if (videoEnabled != null) {
                ToastUtil.showShort(getString(if (videoEnabled!!) R.string.teacher_turns_on_your_camera else R.string.teacher_turns_off_your_camera))
                switchLocalVideo(videoEnabled!!).observeForeverOnce { }
            } else if (audioEnabled != null) {
                ToastUtil.showShort(getString(if (audioEnabled!!) R.string.teacher_turns_on_your_microphone else R.string.teacher_turns_off_your_microphone))
                switchLocalAudio(audioEnabled!!).observeForeverOnce { }
            }
        }
    }

    private fun onBoardPermissionGranted(member: NEEduMember) {
        if (member.isHost() || !isSelf(member)) return
        val grantStr = getGrantStr(member.isGrantedWhiteboard())
        ToastUtil.showLong(getString(R.string.whiteboard_permission, grantStr))
        eduManager.getBoardService().setEnableDraw(member.isGrantedWhiteboard())
    }

    private fun onScreenSharePermissionGranted(member: NEEduMember) {
        if (member.isHost() || !isSelf(member)) return
        val grantStr = getGrantStr(member.isGrantedScreenShare())
        ToastUtil.showLong(getString(R.string.screen_share_permission, grantStr))
        getScreenShareView().visibility = if (member.isGrantedScreenShare()) View.VISIBLE else View.GONE
        getScreenShareView().isSelected = true
        if (!member.isGrantedScreenShare()) {
            eduManager.getMemberService().getLocalUser()?.apply {
                if (hasSubVideo()) {
                    stopLocalShareScreen()
                }
            }
            getScreenShareCoverView().visibility = View.GONE
        }
    }

    private fun getGrantStr(isGrant: Boolean): String {
        return if (isGrant) {
            getString(R.string.grant)
        } else {
            getString(R.string.cancel)
        }
    }

    fun isSelf(member: NEEduMember): Boolean {
        return eduManager.isSelf(member.userUuid)
    }

    open fun onStreamChange(member: NEEduMember, updateVideo: Boolean) {
        memberVideoAdapter.refreshDataAndNotify(member, updateVideo)
    }

    private fun loadData() {
        eduManager.syncSnapshot()
    }

    open fun replaceFragment(id: Int, baseFragment: BaseFragment) {
        supportFragmentManager.beginTransaction().replace(id, baseFragment).commitNow()
    }

    open fun addFragment(id: Int, baseFragment: BaseFragment) {
        supportFragmentManager.beginTransaction().add(id, baseFragment).commitNowAllowingStateLoss()
    }

    open fun removeFragment(baseFragment: BaseFragment) {
        supportFragmentManager.beginTransaction().remove(baseFragment).commitAllowingStateLoss()
    }

    open fun handleBackBtn(view: View) {
        view.setOnClickThrottleFirst {
            // may be show quit dialog
            onBackClicked()
        }
    }

    override fun updateUnReadCount() {}

    open fun switchLocalShareScreen() {
        eduManager.getMemberService().getLocalUser()?.apply {
            if (hasSubVideo()) {
                stopLocalShareScreen()
            } else {
                startLocalShareScreen()
            }
        }
    }

    private fun startLocalShareScreen() {
        if(hasOtherScreen()){
            ToastUtil.showShort(getString(R.string.someone_share))
            return
        }
        eduManager.getShareScreenService().shareScreen(eduRoom.roomUuid, entryMember.userUuid).observe(this,
            { t ->
                if (t.success()) {
                    requestScreenCapture(this.applicationContext, this)
                } else {
                    if (t.code == NEEduHttpCode.ROOM_MEMBER_CONCURRENCY_OUT.code) {
                        ToastUtil.showShort(getString(R.string.someone_share))
                    } else {
                        ToastUtil.showShort(getString(R.string.share_screen_fail))
                    }
                }
            })
    }

    private fun hasOtherScreen():Boolean{
      val screenMember =   eduManager.getMemberService().getMemberList().find {
            it.streams?.subVideo?.value ==1
        }
        return screenMember != null
    }

    open fun stopLocalShareScreen(callback: (() -> Unit)? = null) {
        eduManager.getShareScreenService().finishShareScreen(eduRoom.roomUuid, entryMember.userUuid).observe(this,
            { t ->
                if (t.success()) {
                    eduManager.getShareScreenService().stopScreenCapture()
                    eduManager.getMemberService().getLocalUser()?.updateSubVideo(null)
                    getScreenShareView().isSelected = true
                    getScreenShareCoverView().visibility = View.GONE
                } else {
                    ALog.w("fail to stop ScreenCapture")
                }
                callback?.let { it() }
            })
    }

    open fun requestScreenCapture(applicationContext: Context, activity: Activity) {
        activity.startActivityForResult(NEEduForegroundService.neCaptureIntent, CAPTURE_PERMISSION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_PERMISSION_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                // reset
                eduManager.getShareScreenService().finishShareScreen(eduRoom.roomUuid, entryMember.userUuid).also {
                    it.observe(this, {})
                }
            } else {
                if (!entryMember.isHost() && eduManager.getMemberService().getLocalUser()
                        ?.isGrantedScreenShare() != true
                ) {
                    ToastUtil.showShort(getString(R.string.share_screen_fail))
                    return
                }
                val config = NERtcScreenConfig().apply {
                    contentPrefer = NERtcScreenConfig.NERtcSubStreamContentPrefer.CONTENT_PREFER_DETAILS
                    videoProfile = RTCVideoProfile.kVideoProfileHD1080p
                }
                eduManager.getShareScreenService().startScreenCapture(config, data, object :
                    MediaProjection.Callback() {
                    override fun onStop() {
//                        runOnUiThread { stopLocalShareScreen() }
                    }
                })
                eduManager.getMemberService().getLocalUser()?.updateSubVideo(NEEduStreamSubVideo())
                getScreenShareView().isSelected = false
                getScreenShareCoverView().visibility = View.VISIBLE
            }
        }
    }


    open fun updateRtcView(rtcVideoAudioView: RtcVideoAudioView?, member: NEEduMember, updateVideo: Boolean) {
        rtcVideoAudioView?.updateView(member)
        if (!member.isHolder()) {
            //updateRtcAudio(member)
            if (updateVideo) updateRtcVideo(rtcVideoAudioView, member)
        }
    }

    open fun updateRtcVideo(rtcVideoAudioView: RtcVideoAudioView?, member: NEEduMember) {
        eduManager.getRtcService().updateRtcVideo(rtcVideoAudioView?.getViewContainer(), member)
    }

    /**
     * no member screen share or self screen share not need update ui
     */
    open fun updateRtcSubVideo(rtcSubVideo: ViewGroup, member: NEEduMember?) {
        if (member == null || eduManager.isSelf(member.userUuid)) {
            rtcSubVideo.visibility = View.GONE
            rtcSubVideo.removeAllViews()
        } else {
            rtcSubVideo.visibility = View.VISIBLE
            eduManager.getRtcService().updateRtcSubVideo(rtcSubVideo, member)
        }
    }

    fun updateLocalUserVideoAudio(
        videoEnabled: Boolean,
        audioEnabled: Boolean,
    ): LiveData<Pair<NEResult<Void>, NEResult<Void>>> {
        val selfMember = eduManager.getMemberService().getLocalUser()
        return eduManager.getRtcService().localUserVideoAudioEnable(videoEnabled, audioEnabled).map {
            it.first.success().let {
                selfMember!!.updateVideo(if (videoEnabled) NEEduStreamVideo() else null)
            }
            it.second.success().let {
                selfMember!!.updateAudio(if (videoEnabled) NEEduStreamAudio() else null)
            }
            it
        }
    }

    open fun switchLocalVideo(): LiveData<NEResult<Void>> {
        val localMediaUser = eduManager.getMemberService().getLocalUser()
        return switchLocalVideo(!localMediaUser!!.hasVideo())
    }

    open fun switchLocalVideo(videoEnabled: Boolean): LiveData<NEResult<Void>> {
        return eduManager.getRtcService().localUserVideoEnable(videoEnabled)
    }

    open fun switchLocalAudio(): LiveData<NEResult<Void>> {
        val localMediaUser = eduManager.getMemberService().getLocalUser()
        return switchLocalAudio(!localMediaUser!!.hasAudio())
    }

    open fun switchLocalAudio(audioEnabled: Boolean): LiveData<NEResult<Void>> {
        return eduManager.getRtcService().localUserAudioEnable(audioEnabled)
    }

    fun switchRemoteUserVideo(member: NEEduMember) {
        eduManager.roomConfig.memberStreamsPermission()?.apply {
            val self = entryMember
            video?.let { it ->
                if (it.hasAllPermission(self.role)) {
                    eduManager.getRtcService().remoteUserVideoEnable(member.userUuid, !member.hasVideo())
                        .observe(this@BaseNormalClassActivity, {
                            ALog.i(tag, "switchRemoteUserVideo")
                            toastOperateSuccess()
                        })
                }
            }
        }
    }

    fun switchRemoteUserAudio(member: NEEduMember) {
        eduManager.roomConfig.memberStreamsPermission()?.apply {
            val self = entryMember
            audio?.let { it ->
                if (it.hasAllPermission(self.role)) {
                    eduManager.getRtcService().remoteUserAudioEnable(member.userUuid, !member.hasAudio())
                        .observe(this@BaseNormalClassActivity, {
                            ALog.i(tag, "switchRemoteUserAudio")
                            toastOperateSuccess()
                        })
                }
            }
        }
    }

    fun switchGrantWhiteboardPermission(member: NEEduMember) {
        if (!clazzStart) {
            ToastUtil.showShort(getString(R.string.start_class_first))
            return
        }
        eduManager.roomConfig.memberPropertiesPermission()?.apply {
            val self = entryMember
            whiteboard?.let { it ->
                if (it.hasAllPermission(self.role)) {
                    eduManager.getBoardService().grantPermission(member.userUuid, !member.isGrantedWhiteboard())
                        .observe(this@BaseNormalClassActivity, {
                            ALog.i(tag, "grantWhiteboardPermission")
                        })
                }
            }
        }
    }

    fun switchGrantScreenSharePermission(member: NEEduMember) {
        if (!clazzStart) {
            ToastUtil.showShort(getString(R.string.start_class_first))
            return
        }
        eduManager.roomConfig.memberPropertiesPermission()?.apply {
            val self = entryMember
            screenShare?.let { it ->
                if (it.hasAllPermission(self.role)) {
                    eduManager.getShareScreenService().grantPermission(member.userUuid, !member.isGrantedScreenShare())
                        .observe(this@BaseNormalClassActivity, {
                            ALog.i(tag, "grantScreenSharePermission")
                        })
                }
            }
        }
    }

    private fun showLocalActionSheetDialog(member: NEEduMember) {
        val dialog = ActionSheetDialog(this)
        dialog.addAction(
            ACTION_AUDIO,
            if (member.hasAudio()) getString(R.string.disable_audio) else getString(R.string.enable_audio)
        )
        dialog.addAction(
            ACTION_VIDEO,
            if (member.hasVideo()) getString(R.string.disable_video) else getString(R.string.enable_video)
        )
        dialog.setOnItemClickListener(object : ItemClickListerAdapter<ActionSheetDialog.ActionItem>() {
            override fun onClick(v: View?, pos: Int, data: ActionSheetDialog.ActionItem) {
                super.onClick(v, pos, data)
                dialog.dismiss()
                when (data.action) {
                    ACTION_AUDIO -> switchLocalAudio().observe(this@BaseNormalClassActivity, { })
                    ACTION_VIDEO -> switchLocalVideo().observe(this@BaseNormalClassActivity, { })
                }
            }
        })
        dialog.show()
    }

    private fun showRemoteFullActionSheetDialog(member: NEEduMember, fullAction: Boolean) {
        val dialog = ActionSheetDialog(this)
        if (fullAction) {
            dialog.addAction(
                ACTION_AUDIO,
                if (member.hasAudio()) getString(R.string.disable_audio) else getString(R.string.enable_audio)
            )
            dialog.addAction(
                ACTION_VIDEO,
                if (member.hasVideo()) getString(R.string.disable_video) else getString(R.string.enable_video)
            )
            dialog.overCapacity = true
        }

        dialog.addAction(
            ACTION_REVOKE_WHITEBOARD,
            if (member.isGrantedWhiteboard()) getString(R.string.revoke_whiteboard_permissions) else getString(R.string.grant_whiteboard_permissions)
        )
        dialog.addAction(
            ACTION_REVOKE_SHARE,
            if (member.isGrantedScreenShare()) getString(R.string.revoke_share_permission) else getString(R.string.grant_share_permission)
        )

        if (eduManager.roomConfig.isBig()) {
            dialog.addAction(
                ACTION_OFFSTAGE,
                if (member.isOnStage()) getString(R.string.go_off_stage) else getString(R.string.come_on_stage)
            )
        }

        dialog.setOnItemClickListener(object : ItemClickListerAdapter<ActionSheetDialog.ActionItem>() {
            override fun onClick(v: View?, pos: Int, data: ActionSheetDialog.ActionItem) {
                super.onClick(v, pos, data)
                dialog.dismiss()
                when (data.action) {
                    ACTION_AUDIO -> switchRemoteUserAudio(member)
                    ACTION_VIDEO -> switchRemoteUserVideo(member)
                    ACTION_REVOKE_WHITEBOARD -> switchGrantWhiteboardPermission(member)
                    ACTION_REVOKE_SHARE -> switchGrantScreenSharePermission(member)
                    ACTION_OFFSTAGE -> switchStuRemoteHandsUp(member)
                }
            }
        })
        dialog.show()
    }

    open fun switchStuRemoteHandsUp(member: NEEduMember) {
    }

    fun showActionSheetDialog(member: NEEduMember) {
        showRemoteFullActionSheetDialog(member, false)
    }

    open fun initClazzViews(
        title: TitleView,
        btnStateClazz: TextView?,
        btnTempLeaveClazz: TextView?,
    ) {
        eduManager.getRoomService().onCurrentRoomInfo().observe(this@BaseNormalClassActivity, { room ->
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
        eduManager.getRoomService().onNetworkQualityChange().observe(this,
            { t ->
                t?.takeIf { t.isNotEmpty() }?.forEach { it ->
                    if (it.userId == entryMember.rtcUid) {// jude self
                        title.setNetworkQuality(it)
                    }
                }
            })
        btnTempLeaveClazz?.setOnClickThrottleFirst {
            //tempLeaveClazz()
        }
    }

    open fun startClazz() {
        ConfirmDialog.show(this, getString(R.string.sure_start_class), getString(R.string.start_class_tips),
            cancelable = true,
            cancelOnTouchOutside = true,
            callback = object : ConfirmDialog.Callback {
                override fun result(boolean: Boolean?) {
                    if (boolean == true) {
                        eduManager.getRoomService().startClass(roomUuid = eduRoom.roomUuid)
                            .observe(this@BaseNormalClassActivity, {
                                ALog.i(tag, "startClazz")
                            })
                    }
                }
            })

    }

    open fun finishClazz() {
        ConfirmDialog.show(this,
            getString(R.string.end_class),
            getString(R.string.end_class_message),
            ok = getString(R.string.sure),
            cancelable = true,
            cancelOnTouchOutside = true,
            callback = object : ConfirmDialog.Callback {
                override fun result(boolean: Boolean?) {
                    if (boolean == true)
                        eduManager.getRoomService().finishClass(roomUuid = eduRoom.roomUuid)
                            .observe(this@BaseNormalClassActivity, {
                                ALog.i(tag, "finishClazz")
                            })
                }
            })
    }

    override fun showFragmentWithMembers() {
        getMembersFragment()?.let {
            getMembersLayout().visibility = View.VISIBLE
        }
    }

    override fun hideFragmentWithMembers() {
        getMembersFragment()?.let {
            getMembersLayout().visibility = View.GONE
        }
        getMembersFragment()?.hideKeyBoard()
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
                if (entryMember.isHost()) getString(
                    R.string.leave_class_teacher_tips
                ) else getString(R.string.leave_class_student_tips),
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

    fun toastOperateSuccess() {
        ToastUtil.showShort(R.string.operation_successful)
    }

    open fun unRegisterObserver() {
        eduManager.getRoomService().onRoomStatesChange().removeObserver(roomStatesChangeObserver)
        eduManager.getRtcService().onMuteAllAudio().removeObserver(muteAllAudioObserver)
        eduManager.getMemberService().onMemberJoin().removeObserver(memberJoinObserver)
        eduManager.getMemberService().onMemberPropertiesChange().removeObserver(memberPropertiesChangeObserver)
        eduManager.getRtcService().onStreamChange().removeObserver(streamChangeObserver)
        eduManager.getShareScreenService().onScreenShareChange().removeObserver(screenShareChangeObserver)
        eduManager.getBoardService().onPermissionGranted().removeObserver(boardPermissionObserver)
        eduManager.getShareScreenService().onPermissionGranted().removeObserver(shareScreenPermissionObserver)
    }

   open fun destroy() {
        eduManager.getRtcService().leave()
        unRegisterObserver()
        eduManager.getMemberService().getLocalUser().let {
            if (it != null && it.hasSubVideo()) {
                stopLocalShareScreen {
                    NEEduUiKit.destroy()
                    finish()
                }
            } else {
                NEEduUiKit.destroy()
                finish()
            }
        }
    }

    override fun getScreenShareCoverView(): View {
        return binding.tvShareVideo
    }

    override fun getAvHandsUpOffstageView(): View {
        return binding.bottomView.getHandsUpOffstage()
    }

    private var zoomImageFragment: BaseFragment? = null

    override fun getChatroomFragment(): BaseFragment? {
        return null
    }

    override fun showFragmentWithChatRoom() {
        getIMLayout().visibility = View.VISIBLE
    }

    override fun hideFragmentWithChatRoom() {
        getIMLayout().visibility = View.GONE
        chatViewModel.clearUnread()
        getChatroomFragment()?.hideKeyBoard()
    }

    override fun getZoomImageLayout(): View {
        return binding.layoutZoomImage
    }

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
}