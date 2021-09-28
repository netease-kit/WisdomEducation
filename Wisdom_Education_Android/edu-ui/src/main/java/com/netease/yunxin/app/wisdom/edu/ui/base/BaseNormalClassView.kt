/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.base

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ClazzInfoView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.TitleView

interface BaseNormalClassView {

    fun onScreenShareChange(t: List<NEEduMember>)

    fun getHandsUpListLayout(): View?

    fun getScreenShareLayout(): View

    fun getScreenShareView(): View

    fun getScreenShareCoverView(): View

    fun getAvHandsUpView(): View

    fun getAudioView(): View

    fun getVideoView(): View

    fun getMemberVideoRecyclerView(): RecyclerView

    fun getBackView(): View

    fun getChangeClazzStateView(): TextView?

    fun getLeaveClazzView(): TextView?

    fun getClazzTitleView(): TitleView

    fun getClazzInfoLayout(): ClazzInfoView

    fun getClazzFinishLayout(): View

    fun getClassFinishReplay(): View

    fun getClassFinishBackView(): View

    fun getClassInitLayout(): View

    fun getAvHandsUpOffstageView(): View
}