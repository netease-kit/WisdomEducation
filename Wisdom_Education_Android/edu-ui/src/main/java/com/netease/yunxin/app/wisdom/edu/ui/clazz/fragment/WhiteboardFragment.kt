/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentWhiteboardBinding
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig

/**
 */
class WhiteboardFragment : BaseFragment(R.layout.fragment_whiteboard) {
    private val binding: FragmentWhiteboardBinding by viewBinding()

    private lateinit var config: WhiteboardConfig

    override fun parseArguments() {
        super.parseArguments()
        config = WhiteboardConfig(eduManager.eduLoginRes.imKey, eduManager.eduLoginRes.userUuid, eduManager
            .eduLoginRes.imToken, eduManager.eduEntryRes.room.whiteBoardCName(), "", eduManager.eduEntryRes.isHost())
    }

    override fun initViews() {
        eduManager.getBoardService().initBoard(binding.whiteboardView, config)
        eduManager.getBoardService().setEnableDraw(eduManager.eduEntryRes.isHost() || eduManager.roomConfig.is1V1())
    }
}