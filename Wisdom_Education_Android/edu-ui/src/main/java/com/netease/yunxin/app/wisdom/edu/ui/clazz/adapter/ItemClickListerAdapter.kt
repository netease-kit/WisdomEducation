/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.view.View
import com.netease.yunxin.app.wisdom.rvadapter.OnItemClickListener

/**
 */
open class ItemClickListerAdapter<T> : OnItemClickListener<T> {
    override fun onClick(v: View?, pos: Int, data: T) {}
    override fun onLongClick(v: View?, pos: Int, data: T): Boolean {
        return false
    }
}