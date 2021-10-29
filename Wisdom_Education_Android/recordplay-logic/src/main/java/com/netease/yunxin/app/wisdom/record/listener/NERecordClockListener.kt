/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.listener

/**
 * Progress synchronization callback listening
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