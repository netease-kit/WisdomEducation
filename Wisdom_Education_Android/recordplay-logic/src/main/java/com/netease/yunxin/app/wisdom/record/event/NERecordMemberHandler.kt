/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.event

import com.netease.yunxin.app.wisdom.record.NERecordPlayer
import com.netease.yunxin.app.wisdom.record.model.NEEduRecordData
import com.netease.yunxin.app.wisdom.record.model.NERecordEvent
import com.netease.yunxin.app.wisdom.record.model.NERecordEventType
import com.netease.yunxin.app.wisdom.record.model.NERecordItem
import com.netease.yunxin.kit.alog.ALog
import java.util.*

/**
 * handle member 'join/leave' event
 *
 * @property list raw record item list
 * @property initVideoList init video list
 * @property callback callback to handle event process result
 */
class NERecordMemberHandler(
    val list: List<NERecordItem>,
    private val initVideoList: List<NERecordItem>,
    private val callback: NERecordEventHandlerCallback
) : NERecordEventHandler {
    private val tag: String = "NERecordMemberHandler"

    companion object {
        private var recordData: NEEduRecordData = NERecordPlayer.instance.recordOptions.recordData

        fun memberJoin(event: NERecordEvent): Boolean {
            return event.type == NERecordEventType.MEMBER_JOIN
        }

        fun memberLeave(event: NERecordEvent): Boolean {
            return event.type == NERecordEventType.MEMBER_LEAVE
        }

        /**
         * filter member join or leave event type
         *
         */
        fun filterMemberJoinOrLeaveEvent(event: NERecordEvent): Boolean {
            return (recordData.is1V1() || recordData.isSmall() || NERecordPlayer.instance.getHostActors().any { it.recordItem.roomUuid == event.roomUid })
                    && (memberJoin(event) || memberLeave(event))
        }
    }

    override fun filterType(event: NERecordEvent): Boolean {
        return filterMemberJoinOrLeaveEvent(event)
    }

    override fun filterOther(event: NERecordEvent): Boolean {
        return true
    }

    override fun processSeek(
        prevExecutedEventList: LinkedList<NERecordEvent>,
        executedEventList: LinkedList<NERecordEvent>
    ) {
        val inVideoList: MutableList<NERecordItem> = mutableListOf()
        val outVideoList: MutableList<NERecordItem> = mutableListOf()
        list.forEach {
            val prevEvent =
                prevExecutedEventList.lastOrNull { it1 -> filterType(it1) && it.roomUid.toString() == it1.roomUid }
            val targetEvent =
                executedEventList.lastOrNull { it1 -> filterType(it1) && it.roomUid.toString() == it1.roomUid }
            ALog.i(tag, "processSeek lastEvent: $prevEvent targetEvent: $targetEvent item:${it.roomUid}")
            if (prevEvent == targetEvent) {
                ALog.i("Not change event")
            } else if (prevEvent != null && targetEvent != null && prevEvent != targetEvent) {
                memberJoin(targetEvent).apply {
                    if (this) inVideoList.add(it) else outVideoList.add(it)
                }
            } else if (prevEvent != null) {
                val showInit = initVideoList.any { it1 -> it.roomUid == it1.roomUid }
                memberJoin(prevEvent).apply {
                    // If the event before the seek operation is different from the initial state of the current event, set the event to the initial state
                    if (this != showInit) {
                        if (showInit) inVideoList.add(it) else outVideoList.add(it)
                    }
                }
            } else if (targetEvent != null) {
                val showInit = initVideoList.any { it1 -> it.roomUid == it1.roomUid }
                memberJoin(targetEvent).apply {
                    // If the event after the seek operation is different from the initial state of the current event, swtich to the state of the event after the seek operation
                    if (this != showInit) {
                        if (this) inVideoList.add(it) else outVideoList.add(it)
                    }
                }
            }
        }
        callback.onMemberVideoChange(inVideoList, outVideoList)
    }

    override fun process(event: NERecordEvent) {
        ALog.i(tag, "process...$event")
        val inVideoList: MutableList<NERecordItem> = mutableListOf()
        val outVideoList: MutableList<NERecordItem> = mutableListOf()
        list.firstOrNull { it.roomUid.toString() == event.roomUid }?.let {
            memberJoin(event).apply {
                if (this) inVideoList.add(it) else outVideoList.add(it)
            }
        }
        callback.onMemberVideoChange(inVideoList, outVideoList)
    }

    override fun resetToInit() {

    }
}