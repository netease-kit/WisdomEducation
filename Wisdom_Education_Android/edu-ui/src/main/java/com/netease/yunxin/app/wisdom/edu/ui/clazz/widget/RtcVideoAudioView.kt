/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.databinding.RtcVideoAudioViewBinding

/**
 * Created by hzsunyj on 2021/5/20.
 */
class RtcVideoAudioView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : ConstraintLayout(
    context,
    attrs, defStyleAttr
) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding = RtcVideoAudioViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun getViewContainer(): FrameLayout {
        return binding.videoContainer
    }

    fun updateView(member: NEEduMember) {
        if (member.isHolder()) {
            binding.icAudio.visibility = GONE
            binding.icVideo.visibility = GONE
            updateHolderVisibility(View.VISIBLE)
            updateName("")
        } else {
            binding.icAudio.visibility = VISIBLE
            binding.icVideo.visibility = VISIBLE
            updateHolderVisibility(if (member.hasVideo()) View.GONE else View.VISIBLE)
            var suffix = if (member.role == NEEduRoleType.HOST.value) context.getString(R.string.teacher_label)
            else context.getString(R.string.student_label)
            updateName("${member.userName}${suffix}")
        }
    }

    private fun updateName(nickname: String) {
        binding.tvName.text = nickname
    }

    private fun updateHolderVisibility(visibility: Int) {
        binding.placeHolder.visibility = visibility
    }

    fun enableAudio(enable: Boolean) {
        binding.icAudio.isSelected = enable
    }

    fun enableVideo(enable: Boolean) {
        binding.icVideo.isSelected = enable
    }
}