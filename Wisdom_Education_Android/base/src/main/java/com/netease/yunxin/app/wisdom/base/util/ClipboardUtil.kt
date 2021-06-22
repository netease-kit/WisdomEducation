/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Created by hzsunyj on 2021/6/7.
 */
object ClipboardUtil {

    private const val LABEL: String = "text"

    fun copyText(context: Context, value: String, label: String? = LABEL) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(label, value)
        cm.setPrimaryClip(clipData)
    }
}