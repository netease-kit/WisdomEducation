/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHandsUpStateValue
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHttpCode
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ConfirmDialog
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.BigClazzMembersFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.ChatRoomFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment.HandsUpListFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ClazzInfoView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ItemBottomView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.TitleView
import com.netease.yunxin.kit.alog.ALog

/**
 * 
 */
abstract class BaseBigClassActivity : BaseNormalClassActivity() {
    private val bigClazzMembersFragment = BigClazzMembersFragment()
    private val chatRoomFragment = ChatRoomFragment()

    open var handsUpListFragment: HandsUpListFragment = HandsUpListFragment()

    private val stageChangeObserver = Observer<List<NEEduMember>?>{ onStageListChange() }

    override fun getIMLayout(): View {
        return binding.layoutIm
    }

    override fun getMembersLayout(): View {
        return binding.layoutMembers
    }

    override fun getScreenShareLayout(): View {
        return binding.layoutShareVideo
    }

    override fun getScreenShareView(): View {
        return binding.bottomView.getShareScreen()
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

    override fun getClazzTitleView(): TitleView {
        return binding.titleLayout
    }

    override fun getMembersFragment(): BaseFragment? {
        return bigClazzMembersFragment
    }

    override fun getChatroomFragment(): BaseFragment? {
        return chatRoomFragment
    }

    override fun getClazzInfoLayout(): ClazzInfoView {
        return binding.clazzInfoView
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

    override fun getClazzFinishLayout(): View {
        return binding.layoutClassFinish
    }

    override fun getChatRoomView(): ItemBottomView {
        return binding.bottomView.getChatRoom()
    }

    override fun getMembersView(): View {
        return binding.bottomView.getMembers()
    }

    open fun onStageListChange() {}

    override fun registerObserver() {
        super.registerObserver()
        eduManager.getHandsUpService().onHandsUpStateChange().observeForever(stageChangeObserver)
    }

    override fun unRegisterObserver() {
        super.unRegisterObserver()
        eduManager.getHandsUpService().onHandsUpStateChange().removeObserver(stageChangeObserver)
    }

    open fun showFragmentWithHandsUp() {
        getHandsUpListLayout()?.let {
            it.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().remove(handsUpListFragment)
                .commitNowAllowingStateLoss()
            supportFragmentManager.beginTransaction().add(
                R.id.layout_handsup_list, handsUpListFragment
            ).show(handsUpListFragment)
                .commitNowAllowingStateLoss()
        }
    }

    open fun hideFragmentWithHandsUp() {
        getHandsUpListLayout()?.let {
            it.visibility = View.GONE
            supportFragmentManager.beginTransaction().remove(handsUpListFragment)
                .commitNowAllowingStateLoss()
        }
    }


    /**
     * Raise hand/lower hand
     *
     */
    fun switchStuLocalHandsUp() {
        eduManager.getMemberService().getLocalUser()?.apply {
            if (isHandsUp()) {
                ConfirmDialog.show(this@BaseBigClassActivity,
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
                ConfirmDialog.show(this@BaseBigClassActivity,
                    this@BaseBigClassActivity.getString(R.string.hands_up_apply),
                    this@BaseBigClassActivity.getString(R.string.hands_up_apply_msg),
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


    /**
     * Teacher unmute/mute a student
     *
     * @param member
     */
    override fun switchStuRemoteHandsUp(member: NEEduMember) {
        if (member.isOnStage()) offStageStudentRemote(member.userUuid) else acceptStuRemoteHandsUp(member.userUuid)
    }

    fun acceptStuRemoteHandsUp(userUuid: String) {
        handsUpStateChange(NEEduHandsUpStateValue.TEACHER_ACCEPT, userUuid)
    }

    fun rejectStuRemoteHandsUp(userUuid: String) {
        handsUpStateChange(NEEduHandsUpStateValue.TEACHER_REJECT, userUuid)
    }

    private fun offStageStudentRemote(userUuid: String) {
        handsUpStateChange(NEEduHandsUpStateValue.TEACHER_OFF_STAGE, userUuid)
    }

    private fun applyStudentHandsUp() {
        handsUpStateChange(NEEduHandsUpStateValue.APPLY, entryMember.userUuid)
    }

    private fun cancelStudentHandsUp() {
        handsUpStateChange(NEEduHandsUpStateValue.STUDENT_CANCEL, entryMember.userUuid)
    }

    /**
     * Student leaves as speaker
     *
     */
    fun offStageStudentLocal() {
        handsUpStateChange(NEEduHandsUpStateValue.IDLE, entryMember.userUuid)
    }

    private fun handsUpStateChange(state: Int, userUuid: String) {
        eduManager.getHandsUpService().handsUpStateChange(state, userUuid)
            .observe(this,
                { t ->
                    if (!t.success()) {
                        if (t.code == NEEduHttpCode.ROOM_MEMBER_CONCURRENCY_OUT.code) {
                            ToastUtil.showShort(getString(R.string.stage_student_over_limit))
                        } else {
                            ToastUtil.showShort(getString(R.string.operate_fail))
                        }
                        ALog.w("fail to $state hands up ${t.code}")
                    }
                })
    }
}