/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui

import com.netease.yunxin.app.wisdom.record.ui.databinding.FragmentRecordWhiteboardBinding
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding

/**
 */
class NERecordWhiteboardFragment : NERecordBaseFragment(R.layout.fragment_record_whiteboard) {
    private val binding: FragmentRecordWhiteboardBinding by viewBinding()

    override fun initViews() {
        (activity as NERecordActivity).updateWhiteBoard(binding.whiteboardView)
    }
}