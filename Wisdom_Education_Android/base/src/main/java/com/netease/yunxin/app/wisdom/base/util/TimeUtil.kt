/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hzsunyj on 2021/6/3.
 */
object TimeUtil {

    private val FULL_DIVIDE_SDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun stringForTimeHMS(timeS: Long, formatStrHMS: String): String {
        val seconds = timeS % 60
        val minutes = timeS / 60
        return Formatter(StringBuffer(), Locale.getDefault())
            .format(formatStrHMS, minutes, seconds).toString()
    }

    fun getNowDatetime(time: Long): String {
        return FULL_DIVIDE_SDF.format(Date(time))
    }

}