/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by hzsunyj on 2021/6/2.
 */
object NetworkUtil {

    open fun isNetAvailable(context: Context): Boolean {
        var cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo?.isAvailable() ?: false
    }
}