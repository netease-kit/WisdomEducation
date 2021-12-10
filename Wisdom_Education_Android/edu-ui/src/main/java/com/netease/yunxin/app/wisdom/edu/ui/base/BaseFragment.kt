/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit

/**
 */
abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    open lateinit var eduManager: NEEduManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEduManager()
        parseArguments()
    }

    private fun initEduManager() {
        eduManager = NEEduUiKit.instance!!.neEduManager!!
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

    open fun hideKeyBoard() {

    }
}