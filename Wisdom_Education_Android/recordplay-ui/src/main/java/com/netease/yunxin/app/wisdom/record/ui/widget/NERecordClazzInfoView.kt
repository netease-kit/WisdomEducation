/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.netease.yunxin.app.wisdom.base.util.ClipboardUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.record.ui.R
import com.netease.yunxin.app.wisdom.record.ui.databinding.LayoutClazzInfoViewBinding

class NERecordClazzInfoView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutClazzInfoViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    fun setRoomId(text: String) {
        binding.tvRoomIdValue.text = text
    }

    fun setRoomName(text: String) {
        binding.tvRoomNameValue.text = text
    }

    fun setTeacherName(text: String) {
        binding.tvTeacherNameValue.text = text
    }

    fun setOnCopyText(text: String) {
        binding.ivRoomIdCopy.setOnClickListener {
            ClipboardUtil.copyText(context, text)
            ToastUtil.showShort(context.getString(R.string.copy_success))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun show() {
        this.visibility = View.VISIBLE
        this.setOnClickListener {
            hide()
        }
    }

    private fun hide() {
        this.visibility = View.GONE
        this.setOnTouchListener(null)
    }
}