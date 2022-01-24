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
     * Update the playback position
     *
     * @param currentTime The current time
     * @param totalTime The total time
     */
    fun onClockProgressChanged(currentTime: Long, totalTime: Long)

    /**
     * Playback stops
     *
     */
    fun onClockStop()
}