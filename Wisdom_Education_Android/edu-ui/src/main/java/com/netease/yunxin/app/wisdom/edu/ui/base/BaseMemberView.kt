/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.base

import android.view.View

interface BaseMemberView {
    fun getMembersView(): View
    fun getMembersFragment(): BaseFragment?
    fun showFragmentWithMembers()
    fun hideFragmentWithMembers()
    fun getMembersLayout(): View
}