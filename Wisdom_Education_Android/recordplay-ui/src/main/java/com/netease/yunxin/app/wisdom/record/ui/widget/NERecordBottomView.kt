/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.netease.yunxin.app.wisdom.record.ui.databinding.LayoutRecordBottomViewBinding

class NERecordBottomView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutRecordBottomViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    fun getProgressBar(): SeekBar {
        return binding.controllerSeekbar
    }

    fun getPlayBtn(): ImageView {
        return binding.controllerPlayPause
    }

    fun getMuteBtn(): ImageView {
        return binding.videoPlayerMute
    }

    fun getSeekBar(): SeekBar {
        return binding.controllerSeekbar
    }
}