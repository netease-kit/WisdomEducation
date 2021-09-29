/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.education.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.throttleFirst
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.education.R
import com.netease.yunxin.app.wisdom.education.databinding.SettingFragmentBinding
import com.netease.yunxin.app.wisdom.education.ui.MainActivity
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding

class SettingFragment : Fragment(R.layout.setting_fragment) {
    private val binding: SettingFragmentBinding by viewBinding()

    companion object {
        fun newInstance() = SettingFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleChatroom.apply {
            isChecked = PreferenceUtil.enableChatRoom
            setOnCheckedChangeListener { _, isChecked ->
                PreferenceUtil.enableChatRoom = isChecked
            }
        }
        binding.tvBack.setOnClickListener(onClickListener)
        binding.ivImReuse.setOnClickListener(onClickListener)
        binding.toggleLowLatencyLive.apply {
            isChecked = PreferenceUtil.lowLatencyLive
            setOnCheckedChangeListener { _, isChecked ->
                PreferenceUtil.lowLatencyLive = isChecked
            }
        }
    }

    private var onClickListener = View.OnClickListener { v ->
        when (v) {
            binding.tvBack -> {
                (activity as MainActivity).hideSettingFragment()
            }
            binding.ivImReuse -> {
                (activity as MainActivity).addFragment(R.id.layout_setting, SettingIMFragment.newInstance())
            }
        }
    }.throttleFirst()
}