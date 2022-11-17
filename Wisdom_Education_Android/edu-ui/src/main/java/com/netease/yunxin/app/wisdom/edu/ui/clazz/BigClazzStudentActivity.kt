/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.compareList
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.setOnClickThrottleFirst
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ConfirmDialog

open class BigClazzStudentActivity : BaseBigClassActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BigClazzStudentActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getHandsUpListLayout(): View? {
        return null
    }

    override fun getChangeClazzStateView(): TextView? {
        return binding.bottomView.getBtnClazzCtrlRight()
    }

    override fun getLeaveClazzView(): TextView? {
        return binding.bottomView.getBtnClazzCtrlLeft()
    }

    override fun initViews() {
        super.initViews()
        binding.ivChatHide.setOnClickListener { hideFragmentWithChatRoom() }
        binding.bottomView.apply {
            getHandsUpApply().visibility = View.GONE
            getAvHandsUpView().visibility = View.GONE
            getAvHandsUpView().setOnClickThrottleFirst {
                switchStuLocalHandsUp()
            }
            getLeaveClazzView()?.visibility = View.GONE
            getShareScreen().visibility = View.GONE
        }

        eduManager.getHandsUpService().onHandsUpStateChange().observe(this, { t -> onHandsUpStateChange(t) })
    }

    override fun onMemberJoin(t: List<NEEduMember>) {
        super.onMemberJoin(t)
        onStageListChange()
    }

    override fun onStreamChange(member: NEEduMember, updateVideo: Boolean) {
        super.onStreamChange(member, updateVideo)
        // Refresh the status bar at the bottom of the screen
        if (isSelf(member)) {
            binding.bottomView.apply {
                getVideo().isSelected = member.hasVideo()
                getAudio().isSelected = member.hasAudio()
            }
        }
    }

    override fun onScreenShareChange(t: List<NEEduMember>) {
        updateRtcSubVideo(binding.layoutShareVideo, if (t.isNotEmpty()) t.first() else null)
    }

    override fun onStageListChange() {
        eduManager.getHandsUpService().getOnStageMemberList().let { t ->
            var list = mutableListOf<NEEduMember>()
            eduManager.getMemberService().getMemberList().firstOrNull { it.isHost() }?.let { list.add(it) }
            list.addAll(t)
            preprocessList(list).let {list ->
                if(!compareList(list, memberVideoAdapter.dataList)) {
                    list.sortWith(compareBy({ !it.isHost() }, { eduManager.getEntryMember().userUuid != it.userUuid }))
                    memberVideoAdapter.setData(list)
                }
            }
        }
    }

    private fun preprocessList(it: MutableList<NEEduMember>): MutableList<NEEduMember> {
        return if (it.none { it.role == NEEduRoleType.HOST.value }) {
            val toMutableList = it.toMutableList()
            toMutableList.add(0, NEEduMember.buildHoldTeacherMember())
            toMutableList
        } else it
    }

    private fun onHandsUpStateChange(members: List<NEEduMember>?) {
        if (!clazzStart) return
        if (members == null) return
        members.firstOrNull { isSelf(it) }?.let {
            handleSelf(it)
        }
        // Reset the state
        members.forEach { member ->
            member.properties?.avHandsUp?.let {
                if (!member.isOnStage()) {
                    eduManager.getMemberService().getMemberList().firstOrNull { it == member }?.apply {
                        properties?.whiteboard = null
                        properties?.screenShare = null
                        streams?.reset()
                    }
                }
            }
        }
    }

    private fun handleSelf(member: NEEduMember) {
        when {
            member.isOnStage() -> {
                ToastUtil.showLong(getString(R.string.avhandsup_accept))
                getAvHandsUpView().visibility = View.GONE
                updateLocalUserVideoAudio(
                    videoEnabled = true,
                    audioEnabled = true
                ).observe(this, {})
                getAvHandsUpOffstageView().apply {
                    visibility = View.VISIBLE
                    setOnClickThrottleFirst {
                        showOffStageDialog()
                    }
                }
                //                            getLeaveClazzView()?.visibility = View.VISIBLE // TODO leave class
                //                            getLeaveClazzView()?.setOnClickListener {
                //                                onBackPressed()
                //
                getLeaveClazzView()?.text = getString(R.string.leave_class)

                getAudioView().visibility = View.VISIBLE
                getVideoView().visibility = View.VISIBLE
            }
            member.isHandsUp() -> {
                getAvHandsUpView().visibility = View.VISIBLE
                getAvHandsUpView().isSelected = false
                getLeaveClazzView()?.visibility = View.GONE
                getAvHandsUpOffstageView().visibility = View.GONE

                getAudioView().visibility = View.GONE
                getVideoView().visibility = View.GONE
            }
            else -> {
                if (member.isHandsUpReject()) ToastUtil.showLong(R.string.hands_up_has_been_rejected)
                if (member.isOffStage()) ToastUtil.showLong(R.string.teacher_finished_your_stage_operation)
                resetHandsUpState()
            }
        }
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

    private fun showOffStageDialog() {
        ConfirmDialog.show(this@BigClazzStudentActivity,
            this@BigClazzStudentActivity.getString(R.string.off_stage),
            this@BigClazzStudentActivity.getString(R.string.offstage_confirm_message),
            cancelable = true,
            cancelOnTouchOutside = true,
            ok = this@BigClazzStudentActivity.getString(R.string.confirm),
            callback = object : ConfirmDialog.Callback {
                override fun result(boolean: Boolean?) {
                    if (boolean == true)
                        offStageStudentLocal()
                }
            })
    }
}