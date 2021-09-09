/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.netease.yunxin.app.wisdom.record.actor.NERecordVideoActor
import com.netease.yunxin.app.wisdom.record.ui.R
import com.netease.yunxin.app.wisdom.record.ui.databinding.RecordVideoAudioViewBinding


class NERecordVideoView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    ConstraintLayout(
        context,
        attrs, defStyleAttr
    ) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding =
        RecordVideoAudioViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun getViewContainer(): FrameLayout {
        return binding.videoContainer
    }

    fun updateView(videoActor: NERecordVideoActor) {
        val member = videoActor.recordItem
        binding.icAudio.visibility = VISIBLE
        binding.icVideo.visibility = VISIBLE
        updateHolderVisibility(if (videoActor.enableVideo) View.GONE else View.VISIBLE)
        var suffix = context.getString(if (member.isHost()) R.string.teacher_label else R.string.student_label)
        updateName("${member.userName}${suffix}")
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

    fun enableScreenShare(enable: Boolean) {
        binding.icScreenShare.visibility = if (enable) View.VISIBLE else View.GONE
    }
}