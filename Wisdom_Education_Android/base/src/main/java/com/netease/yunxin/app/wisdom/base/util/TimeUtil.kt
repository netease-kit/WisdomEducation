/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class to assist time display
 */
object TimeUtil {

    private val FULL_DIVIDE_SDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun stringForTimeHMS(timeS: Long): String {
        val seconds = timeS % 60
        val minutes = (timeS / 60) % 60
        val hours = timeS / 3600
        return if (hours > 0) {
            Formatter(StringBuffer(), Locale.getDefault())
                .format("%02d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            Formatter(StringBuffer(), Locale.getDefault())
                .format("%02d:%02d", minutes, seconds).toString()
        }
    }

    fun getNowDatetime(time: Long): String {
        return FULL_DIVIDE_SDF.format(Date(time))
    }

}