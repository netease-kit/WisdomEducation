/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.view.View

/**
 */
interface OnItemClickListener<T> {
    fun onClick(v: View?, pos: Int, data: T)
    fun onLongClick(v: View?, pos: Int, data: T): Boolean
}