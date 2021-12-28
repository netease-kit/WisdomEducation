/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.netease.yunxin.app.wisdom.base.util.CommonUtil
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.setOnClickThrottleFirst
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember

class BigClazzTeacherActivity : BaseBigClassActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BigClazzTeacherActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getHandsUpListLayout(): View {
        return binding.layoutHandsupList
    }

    override fun getChangeClazzStateView(): TextView {
        return binding.bottomView.getBtnClazzCtrlRight()
    }

    override fun getLeaveClazzView(): TextView {
        return binding.bottomView.getBtnClazzCtrlLeft()
    }

    override fun initViews() {
        super.initViews()
        binding.ivChatHide.setOnClickListener { hideFragmentWithChatRoom() }
        binding.bottomView.apply {
            getHandsUp().visibility = View.GONE
            getHandsUpApply().setOnClickThrottleFirst {
                showFragmentWithHandsUp()
            }
        }
        getClassInitLayout().visibility = View.GONE
        eduManager.getHandsUpService().let { it1 ->
            it1.onHandsUpStateChange().observe(this@BigClazzTeacherActivity, { t -> onHandsUpStateChange(t) })
        }

    }

    private fun onHandsUpStateChange(members: List<NEEduMember>?) {
        binding.bottomView.getHandsUpApply().setNumUnread(eduManager.getHandsUpService().getHandsUpApplyList().size)
        if (members == null) return
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

    override fun onMemberJoin(t: List<NEEduMember>) {
        super.onMemberJoin(t)
        onStageListChange()
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
            val list = mutableListOf<NEEduMember>()
            val element = eduManager.getMemberService().getMemberList().firstOrNull { it.isHost() }
            if (element != null) {
                list.add(element)
            }
            list.addAll(t)
            list.let {
                if(!CommonUtil.compareList(it, memberVideoAdapter.dataList)) memberVideoAdapter.setData(it)
            }
        }
    }
}