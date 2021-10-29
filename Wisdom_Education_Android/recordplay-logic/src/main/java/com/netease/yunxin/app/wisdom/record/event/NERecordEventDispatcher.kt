/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.event

import android.os.Handler
import android.os.HandlerThread
import com.netease.yunxin.app.wisdom.record.model.NERecordEvent
import com.netease.yunxin.app.wisdom.record.listener.NERecordEventListener

/**
 * Event scheduler, used to execute events
 *
 */
class NERecordEventDispatcher {
    private var dispatchThread: HandlerThread? = null
    var listener: NERecordEventListener? = null

    private val dispatcher by lazy {
        dispatchThread = HandlerThread("EventDispatcher")
        dispatchThread!!.start()
        Handler(dispatchThread!!.looper)
    }

    fun dispatchEvent(event: NERecordEvent, delay: Long) {
        dispatcher.postDelayed({
            listener?.onEventExecute(event)
            listener?.onEventFinish(event)
        }, delay)
    }

    fun clear() {
        dispatcher.removeCallbacksAndMessages(null)
    }

    fun release() {
        dispatcher.removeCallbacksAndMessages(null)
        dispatchThread?.quit()
        dispatchThread = null
    }
}