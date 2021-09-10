/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.netease.yunxin.app.wisdom.record.NERecordPlayer

/**
 */
abstract class NERecordBaseFragment(layoutId: Int) : Fragment(layoutId) {

    lateinit var recordPlayer: NERecordPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecordPlayer()
        parseArguments()
    }

    private fun initRecordPlayer() {
        recordPlayer = NERecordPlayer.instance
    }

    open fun parseArguments() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViews()
    }

    abstract fun initViews()

    open fun initData() {

    }
}