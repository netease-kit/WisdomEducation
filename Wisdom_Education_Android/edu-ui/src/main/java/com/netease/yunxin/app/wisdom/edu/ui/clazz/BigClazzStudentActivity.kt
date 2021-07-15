/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import CommonUtil.setOnClickThrottleFirst
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ConfirmDialog

class BigClazzStudentActivity : BigClassBaseActivity(R.layout.activity_clazz) {

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

    override fun isLegalMember(member: NEEduMember): Boolean {
        return member.isOnStage() || member.isHost()
    }

    override fun onStreamChange(member: NEEduMember, updateVideo: Boolean) {
        super.onStreamChange(member, updateVideo)
        // 刷新底部状态栏
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
            memberVideoAdapter.setData(preprocessList(list))
        }
    }

    private fun preprocessList(it: List<NEEduMember>): List<NEEduMember> {
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
        // 清空状态
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
                //                            getLeaveClazzView()?.visibility = View.VISIBLE // TODO 离开课堂功能
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

        // 状态清除: 流/白板/举手状态/白板/屏幕共享
        eduManager.getMemberService().getLocalUser()?.apply {
            if (hasSubVideo()) {
                stopLocalShareScreen()
            }

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