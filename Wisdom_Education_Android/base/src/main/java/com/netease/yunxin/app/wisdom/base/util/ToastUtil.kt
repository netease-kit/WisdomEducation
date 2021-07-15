/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtil {
    private lateinit var sContext: Application
    private lateinit var sHandler: Handler
    fun init(context: Application) {
        sContext = context
        sHandler = Handler(Looper.getMainLooper())
    }

    fun showShort(@StringRes resId: Int) {
        showShort(sContext.getString(resId))
    }

    fun showShort(@StringRes resId: Int, vararg formatArgs: Any?) {
        showShort(sContext.getString(resId, *formatArgs))
    }

    fun showShort(text: String) {
        sHandler.post { Toast.makeText(sContext, text, Toast.LENGTH_SHORT).show() }
    }

    fun showLong(@StringRes resId: Int) {
        showShort(sContext.getString(resId))
    }

    fun showLong(@StringRes resId: Int, vararg formatArgs: Any?) {
        showShort(sContext.getString(resId, *formatArgs))
    }

    fun showLong(text: String) {
        sHandler.post { Toast.makeText(sContext, text, Toast.LENGTH_LONG).show() }
    }

}