/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.listener

/**
 * 进度同步回调监听
 *
 */
interface NERecordClockListener {
    /**
     * 播放进度更新
     *
     * @param currentTime 当前时间
     * @param totalTime 总时间
     */
    fun onClockProgressChanged(currentTime: Long, totalTime: Long)

    /**
     * 播放结束
     *
     */
    fun onClockStop()
}