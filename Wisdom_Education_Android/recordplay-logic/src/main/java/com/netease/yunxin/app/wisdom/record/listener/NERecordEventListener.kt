/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.listener

import com.netease.yunxin.app.wisdom.record.model.NERecordEvent

/**
 * Dispatcher performs event callbacks
 *
 */
interface NERecordEventListener {
    /**
     * The callback is triggered if the event is executed
     *
     * @param event The event
     */
    fun onEventFinish(event: NERecordEvent)

    /**
     * Event callback
     *
     * @param event
     */
    fun onEventExecute(event: NERecordEvent)
}