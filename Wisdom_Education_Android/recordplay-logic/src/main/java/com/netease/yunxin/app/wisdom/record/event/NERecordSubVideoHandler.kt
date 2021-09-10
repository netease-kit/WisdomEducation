/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.event

import com.netease.yunxin.app.wisdom.record.model.NERecordEvent
import com.netease.yunxin.app.wisdom.record.model.NERecordEventType
import com.netease.yunxin.app.wisdom.record.model.NERecordItem
import com.netease.yunxin.kit.alog.ALog
import java.util.*

class NERecordSubVideoHandler(
    var list: List<NERecordItem>,
    val callback: NERecordSubVideoHandlerCallback
) : NERecordEventHandler {
    private val tag: String = "NERecordSubVideoHandler"
    override fun filterType(event: NERecordEvent): Boolean {
        return subVideoEnable(event) || subVideoDisable(event)
    }

    override fun filterOther(event: NERecordEvent): Boolean {
        return true
    }

    override fun processSeek(
        prevExecutedEventList: LinkedList<NERecordEvent>,
        executedEventList: LinkedList<NERecordEvent>
    ) {
        val prevEvent = prevExecutedEventList.lastOrNull { filterType(it) }
        val targetEvent = executedEventList.lastOrNull { filterType(it) }
        ALog.i(tag, "processSeek lastEvent: $prevEvent targetEvent: $targetEvent")
        if (prevEvent == targetEvent) {
            ALog.i("Not change event")
        } else if (prevEvent != null && targetEvent != null && prevEvent != targetEvent) {
            list.firstOrNull { it.roomUid.toString() == prevEvent.roomUid }?.let {
                subVideoEnable(prevEvent).apply {
                    callback.onSubVideo(!this, it)
                }
            }
            list.firstOrNull { it.roomUid.toString() == targetEvent.roomUid }?.let {
                subVideoEnable(targetEvent).apply {
                    callback.onSubVideo(this, it)
                }
            }
        } else if (targetEvent != null) {
            val showInit = false
            list.firstOrNull { it.roomUid.toString() == targetEvent.roomUid }?.let {
                subVideoEnable(targetEvent).apply {
                    if (this != showInit) callback.onSubVideo(this, it)
                }
            }
        } else if (prevEvent != null) {
            val showInit = false
            list.firstOrNull { it.roomUid.toString() == prevEvent.roomUid }?.let {
                subVideoEnable(prevEvent).apply {
                    if (this != showInit) callback.onSubVideo(false, it)
                }
            }
        }
    }

    override fun process(event: NERecordEvent) {
        ALog.i("process...$event")
        list.firstOrNull { it.roomUid.toString() == event.roomUid }?.let {
            subVideoEnable(event).apply {
                callback.onSubVideo(this, it)
            }
        }
    }

    override fun resetToInit() {
//        callback.showSubVideo(false, recordItem)
    }

    interface NERecordSubVideoHandlerCallback {
        fun onSubVideo(show: Boolean, recordItem: NERecordItem?)
    }

    private fun subVideoEnable(event: NERecordEvent): Boolean {
        return event.type == NERecordEventType.ENABLE_SUB_VIDEO
    }

    private fun subVideoDisable(event: NERecordEvent): Boolean {
        return event.type == NERecordEventType.DISABLE_SUB_VIDEO
    }


}