/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.netease.yunxin.app.wisdom.record.ui.R
import com.netease.yunxin.app.wisdom.record.ui.databinding.LayoutRecordTitleViewBinding

class NERecordTitleView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutRecordTitleViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private lateinit var prefix: String

    init {
        binding.apply {
            ivQuality.setImageResource(R.drawable.ic_clazz_signal_good)
        }
    }

    fun getBackTv(): View {
        return binding.tvBack
    }

    fun setClazzName(text: String) {
        binding.tvClazzName.text = text
    }

    fun setClazzState(text: String) {
        binding.tvClazzState.text = text
    }

    fun startClazzState(prefix: String, time: Long) {
        this.prefix = prefix
    }

    fun getClazzInfoBtn(): View {
        return binding.ivInfo
    }

    fun setClazzInfoClickListener(l: OnClickListener) {
        binding.ivInfo.setOnClickListener(l)
    }
}