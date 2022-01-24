/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.event

import com.netease.yunxin.app.wisdom.record.model.NERecordEvent
import java.util.*

/**
 * Classes that handle related event types. One EventHandler corresponds to one group of related event types
 *
 *
 */
interface NERecordEventHandler {
    /**
     * Filter event types
     *
     * @return check whether to allow to handle events
     */
    fun filterType(event: NERecordEvent): Boolean

    /**
     * Handle events for dragging the progress bar. Update the state to currentTime
     * Developers can customize the handling process. For example, get the snapshot of the current state. or manage local event history queue.
     *
     * @param prevExecutedEventList The executed event list before the seek operation
     * @param executedEventList The executed event list after the seek operation
     */
    fun processSeek(prevExecutedEventList: LinkedList<NERecordEvent>, executedEventList: LinkedList<NERecordEvent>)

    /**
     * Handle each event during normal playback
     *
     * @param event
     */
    fun process(event: NERecordEvent)

    /**
     * Reset to the initial state
     *
     */
    fun resetToInit()

    /**
     * Filter other conditions
     *
     */
    fun filterOther(event: NERecordEvent): Boolean
}