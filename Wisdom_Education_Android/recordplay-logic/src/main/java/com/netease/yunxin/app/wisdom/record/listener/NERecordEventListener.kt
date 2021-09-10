/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.listener

import com.netease.yunxin.app.wisdom.record.model.NERecordEvent

/**
 * dispatcher执行事件回调
 *
 */
interface NERecordEventListener {
    /**
     * 事件执行结束回调
     *
     * @param event 事件
     */
    fun onEventFinish(event: NERecordEvent)

    /**
     * 事件执行回调
     *
     * @param event
     */
    fun onEventExecute(event: NERecordEvent)
}