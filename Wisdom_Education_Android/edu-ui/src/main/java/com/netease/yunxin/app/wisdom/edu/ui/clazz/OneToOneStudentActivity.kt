/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ClazzInfoView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ItemBottomView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.TitleView

class OneToOneStudentActivity : BaseNormalClassActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OneToOneStudentActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getIMLayout(): View {
        return binding.layoutIm
    }

    override fun getMembersLayout(): View {
        return binding.layoutMembers
    }

    override fun getHandsUpListLayout(): View? {
        return null
    }

    override fun getScreenShareLayout(): View {
        return binding.layoutShareVideo
    }

    override fun getScreenShareView(): View {
        return binding.bottomView.getShareScreen()
    }

    override fun getChatRoomView(): ItemBottomView {
        return binding.bottomView.getChatRoom()
    }

    override fun getMembersView(): View {
        return binding.bottomView.getMembers()
    }

    override fun getAvHandsUpView(): View {
        return binding.bottomView.getHandsUp()
    }

    override fun getAudioView(): View {
        return binding.bottomView.getAudio()
    }

    override fun getVideoView(): View {
        return binding.bottomView.getVideo()
    }

    override fun getMemberVideoRecyclerView(): RecyclerView {
        return binding.rcvMemberVideo
    }

    override fun getBackView(): View {
        return binding.titleLayout.getBackTv()
    }

    override fun getChangeClazzStateView(): TextView {
        return binding.bottomView.getBtnClazzCtrlRight()
    }

    override fun getLeaveClazzView(): TextView {
        return binding.bottomView.getBtnClazzCtrlLeft()
    }

    override fun getClazzTitleView(): TitleView {
        return binding.titleLayout
    }

    override fun getClazzInfoLayout(): ClazzInfoView {
        return binding.clazzInfoView
    }

    override fun getClazzFinishLayout(): View {
        return binding.layoutClassFinish
    }

    override fun getClassFinishReplay(): View {
        return binding.btnClassFinishReplay
    }

    override fun getClassFinishBackView(): View {
        return binding.btnClassFinishBack
    }

    override fun getClassInitLayout(): View {
        return binding.rlClassInit
    }

    override fun initViews() {
        super.initViews()
        binding.bottomView.apply {
            getHandsUp().visibility = View.GONE
            getHandsUpApply().visibility = View.GONE
            getMembers().visibility = View.GONE
        }
    }

    override fun onMemberJoin(t: List<NEEduMember>) {
        super.onMemberJoin(t)
        eduManager.getMemberService().getMemberList().let {
            memberVideoAdapter.setData(preprocessList(it))
        }
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

    private fun preprocessList(it: List<NEEduMember>): List<NEEduMember> {
        return if (it.none { it.role == NEEduRoleType.HOST.value }) {
            val toMutableList = it.toMutableList()
            toMutableList.add(0, NEEduMember.buildHoldTeacherMember())
            toMutableList
        } else it
    }

    override fun onScreenShareChange(t: List<NEEduMember>) {
        updateRtcSubVideo(binding.layoutShareVideo, if (t.isNotEmpty()) t.first() else null)
    }
}