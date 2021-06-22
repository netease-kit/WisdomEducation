/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ItemBottomViewBinding

class ItemBottomView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ItemBottomViewBinding.inflate(LayoutInflater.from(context), this)

    private var selected: Boolean = false
    private var unSelectText: String? = null
    private var text: String? = null

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ItemBottomView)
        binding.apply {
            a.getString(R.styleable.ItemBottomView_text)?.let {
                text = it
                unSelectText = it
            }
            a.getString(R.styleable.ItemBottomView_unSelectedText)?.let {
                unSelectText = it
            }
            selected = a.getBoolean(R.styleable.ItemBottomView_selected, true)

            binding.ivIcon.isSelected = selected
            tvLabel.text = if (selected) text else unSelectText

            ivIcon.setImageDrawable(a.getDrawable(R.styleable.ItemBottomView_src))
        }
        a.recycle()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        binding.ivIcon.isSelected = selected
        binding.tvLabel.text = if (selected) text else unSelectText
        this.selected = selected
    }

    override fun isSelected(): Boolean {
        return selected
    }

    fun setNumUnread(num: Int) {
        binding.tvNumUnread.apply {
            if (num > 0) {
                visibility = View.VISIBLE
                text = num.toString()
            } else {
                visibility = View.GONE
            }
        }
    }

    fun setSmallUnread(num: Int) {
        binding.tvSmallUnread.apply {
            visibility = if (num > 0) View.VISIBLE else View.GONE
        }
    }
}