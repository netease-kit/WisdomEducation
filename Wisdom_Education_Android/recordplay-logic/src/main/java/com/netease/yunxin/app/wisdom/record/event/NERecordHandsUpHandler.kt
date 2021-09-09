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
 * handle member 'on stage/off stage' event
 *
 * @property list raw record item list
 * @property initVideoList init video list
 * @property callback callback to handle event process result
 */
class NERecordHandsUpHandler(
    val list: List<NERecordItem>,
    private val initVideoList: List<NERecordItem>,
    private val callback: NERecordEventHandlerCallback
) : NERecordEventHandler {
    private val tag: String = "NERecordHandsUpHandler"
    private var recordData: NEEduRecordData = NERecordPlayer.instance.recordOptions.recordData


    override fun filterType(event: NERecordEvent): Boolean {
        return filterOnStageEvent(event)
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
                onStage(targetEvent).apply {
                    if (this) inVideoList.add(it) else outVideoList.add(it)
                }
            } else if (prevEvent != null) {
                val showInit = false
                onStage(prevEvent).apply {
                    // 只有前面有事件时，前面要跟初始不一样才需要处理，回到初始状态
                    if (this != showInit) {
                        if (showInit) inVideoList.add(it) else outVideoList.add(it)
                    }
                }
            } else if (targetEvent != null) {
                val showInit = false
                onStage(targetEvent).apply {
                    // 只有后面有事件时，后面要跟初始不一样才需要处理，换到后面状态
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
            onStage(event).apply {
                if (this) inVideoList.add(it) else outVideoList.add(it)
            }
        }
        callback.onMemberVideoChange(inVideoList, outVideoList)
    }

    override fun resetToInit() {

    }

    private fun onStage(event: NERecordEvent): Boolean {
        return event.type == NERecordEventType.HANDS_UP_ACCEPTED
    }

    private fun offStage(event: NERecordEvent): Boolean {
        return event.type == NERecordEventType.HANDS_UP_NOT_ACCEPTED
    }

    /**
     * filter member on stage or off stage event type
     *
     */
    private fun filterOnStageEvent(event: NERecordEvent): Boolean {
        return recordData.isBig() && (onStage(event) || offStage(event))
    }

}