/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduWbAuth
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentWhiteboardBinding
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.model.NEWbAuth

/**
 */
class WhiteboardFragment : BaseFragment(R.layout.fragment_whiteboard) {
    private val binding: FragmentWhiteboardBinding by viewBinding()

    private lateinit var config: WhiteboardConfig

    override fun parseArguments() {
        super.parseArguments()
        eduManager.apply {
            config = WhiteboardConfig(
                eduLoginRes.imKey,
                getEntryMember().rtcUid,
                buildWbAuth(getWbAuth()),
                eduLoginRes.userUuid,
                eduLoginRes.imToken,
                getRoom().whiteBoardCName()!!,
                "",
                isHost()
            )
        }
    }

    private fun buildWbAuth(auth: NEEduWbAuth?): NEWbAuth? {
        return auth?.let {
            NEWbAuth(auth.checksum, auth.curTime, auth.nonce)
        }
    }

    override fun initViews() {
        eduManager.getBoardService().initBoard(binding.whiteboardView, config)
        eduManager.getBoardService().setEnableDraw(eduManager.isHost() || eduManager.roomConfig.is1V1())
    }
}