/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.netease.yunxin.app.wisdom.edu.ui.databinding.LayoutBottomViewBinding

class BottomView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutBottomViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    fun getVideo(): ItemBottomView {
        return binding.itemVideo
    }

    fun getAudio(): ItemBottomView {
        return binding.itemAudio
    }

    fun getHandsUp(): ItemBottomView {
        return binding.itemHandup
    }

    fun getHandsUpApply(): ItemBottomView {
        return binding.itemHandupApply
    }

    fun getHandsUpOffstage(): ItemBottomView {
        return binding.itemHandupOffstage
    }

    fun getShareScreen(): ItemBottomView {
        return binding.itemShare
    }

    fun getChatRoom(): ItemBottomView {
        return binding.itemChatroom
    }

    fun getMembers(): ItemBottomView {
        return binding.itemMembers
    }

    fun getBtnClazzCtrlLeft(): Button {
        return binding.btnClazzCtrlLeft
    }

    fun getBtnClazzCtrlRight(): Button {
        return binding.btnClazzCtrlRight
    }

}