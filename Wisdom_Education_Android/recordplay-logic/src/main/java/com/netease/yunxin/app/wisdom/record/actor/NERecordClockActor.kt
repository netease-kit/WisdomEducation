/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.actor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.record.base.INERecordActor
import com.netease.yunxin.app.wisdom.record.event.NERecordEventDispatcher
import com.netease.yunxin.app.wisdom.record.event.NERecordEventHandler
import com.netease.yunxin.app.wisdom.record.listener.NERecordClockListener
import com.netease.yunxin.app.wisdom.record.listener.NERecordEventListener
import com.netease.yunxin.app.wisdom.record.listener.NERecordUIListener
import com.netease.yunxin.app.wisdom.record.model.NERecordEvent
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.options.NERecordOptions
import com.netease.yunxin.kit.alog.ALog
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

/**
 * Handles event and UI progress updates Class
 *
 */
class NERecordClockActor(recordOptions: NERecordOptions, private var uiListener: NERecordUIListener) :
    INERecordActor, NERecordEventListener, NERecordClockListener {
    private val tag: String = "NERecordClockActor"

    @NERecordPlayState
    private var state: Int = NERecordPlayState.IDLE

    private var speed: Float = 1f
    private var eventDispatcher: NERecordEventDispatcher? = null
    private var nextEvent: NERecordEvent? = null

    /**
     * Events that have been executed
     */
    private val executedEventList: LinkedList<NERecordEvent> = LinkedList()

    /**
     * not executed events
     */
    private val pendingEventList: LinkedList<NERecordEvent> = LinkedList()

    /**
     * Event handlers. Each handler should handle a series of related types of events
     */
    private val eventHandlers: MutableList<NERecordEventHandler> = mutableListOf()

    private val playStateLD: MediatorLiveData<Int> = MediatorLiveData()

    private var mDuration: Long = 0L


    private val period = 1000L / 15
    private val playedTime = AtomicLong()
    private var previewTime: Long = 0

    private var timer: Timer? = null

    private val record = recordOptions.recordData
    private var startTime: Long = record.record.startTime
    private var endTime: Long = max(record.record.stopTime, record.eventList.lastOrNull()?.timestamp ?: 0)
    private val eventList: MutableList<NERecordEvent> = recordOptions.recordData.eventList

    fun prepare() {
        pendingEventList.addAll(eventList)
    }

    override fun getDuration(): Long {
        return endTime - startTime
    }

    override fun getCurrentPosition(): Long {
        return playedTime.get()
    }

    override fun start() {
        updateState(NERecordPlayState.PLAYING)
        uiListener.onStart()

        startTimer()

        if (eventDispatcher == null) {
            eventDispatcher = NERecordEventDispatcher()
            eventDispatcher!!.listener = this
        }

        eventDispatcher?.let {
            executeNextEvent()
        }
    }

    private fun startTimer() {
        val task = object : TimerTask() {
            override fun run() {
                val nowTime = Date().time
                playedTime.set(playedTime.get() + (nowTime - previewTime))
                if (startTime + playedTime.get() > endTime) {
                    onClockStop()
                    return
                }
                onClockProgressChanged(playedTime.get(), getDuration())
                previewTime = nowTime
            }
        }
        previewTime = Date().time
        timer = Timer()
        timer!!.schedule(task, period, period)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun executeNextEvent() {
        pendingEventList.poll()?.let {
            ALog.d(tag, "executeNextEvent list: $pendingEventList")
            ALog.d(tag, "executeNextEvent $it")
            nextEvent = it
            eventDispatcher?.dispatchEvent(it, ((it.timestamp - startTime - getCurrentPosition()) / speed).toLong())
        }
    }

    private fun clearDispatcher() {
        eventDispatcher?.clear()
    }

    /**
     * Clear the event scheduler and move the current event to pendingEvents
     *
     */
    private fun revertNextEvent() {
        eventDispatcher?.let {
            clearDispatcher()
            nextEvent?.apply { pendingEventList.addFirst(this) }
            nextEvent = null
        }
    }

    override fun seek(positionMs: Long) {
        stopTimer()
        playedTime.set(positionMs)
        if (state != NERecordPlayState.PAUSED && state != NERecordPlayState.PREPARED) {
            startTimer()
        }

        eventDispatcher?.let {
            // 0. The executed queue before caching
            val prevExecutedEventList: LinkedList<NERecordEvent> = LinkedList()
            prevExecutedEventList.addAll(executedEventList)
            // 1. Clear the event scheduler and move the current event to pendingEvents
            revertNextEvent()
            // 2. Repartition finishEvents and pendingEvents based on positionMs.
            // The current lastEvent will be moved to pendingEvents
            rearrangeEvents(positionMs)

            // 3. Handling seek events
            eventHandlers.forEach {
                it.resetToInit()
                it.processSeek(prevExecutedEventList, executedEventList)
            }

            if (state != NERecordPlayState.PAUSED && state != NERecordPlayState.PREPARED) {
                // 4. Continue the event processing process
                executeNextEvent()
            }

            // 5. The UI update
            if (state == NERecordPlayState.STOPPED) {
                updateState(NERecordPlayState.PLAYING)
                uiListener.onStart()
            }
        }
    }

    private fun rearrangeEvents(positionMs: Long) {
        val tempQueue = eventList
        val currIndex = tempQueue.indexOfFirst {
            ALog.i(
                tag,
                "rearrangeEvents result: ${it.timestamp} $startTime $positionMs ${it.timestamp - startTime > positionMs}"
            )
            it.timestamp - startTime > positionMs
        }
        ALog.i(tag, "rearrangeEvents $currIndex")
        executedEventList.clear()
        pendingEventList.clear()
        if (currIndex > -1) {
            executedEventList.addAll(tempQueue.subList(0, currIndex))
            pendingEventList.addAll(tempQueue.subList(currIndex, tempQueue.size))
        } else {
            executedEventList.addAll(tempQueue)
        }
    }

    override fun pause() {
        stopTimer()

        updateState(NERecordPlayState.PAUSED)
        uiListener.onPause()
        revertNextEvent()
    }

    override fun stop() {
        stopTimer()
        playedTime.set(0)

        clearDispatcher()
        nextEvent = null

        updateState(NERecordPlayState.STOPPED)
        uiListener.onStop()
    }

    override fun setSpeed(speed: Float) {
        this.speed = speed

        eventDispatcher?.let {
            revertNextEvent()
            executeNextEvent()
        }
    }

    override fun getState(): Int {
        return state
    }

    override fun onStateChange(): LiveData<Int> {
        return playStateLD
    }

    override fun updateState(@NERecordPlayState playState: Int) {
        state = playState
        playStateLD.postValue(playState)
    }

    fun switchAudio(audioEnable: Boolean) {
        uiListener.onSwitchAudio(audioEnable)
    }

    fun setVolume(volume: Float) {
        uiListener.onVolumeChange(volume)
    }

    fun switchVideo(videoEnable: Boolean) {

    }

    fun addHandler(handler: NERecordEventHandler) = apply {
        eventHandlers += handler
    }

    override fun onEventFinish(event: NERecordEvent) {
        executedEventList.add(event)
        executeNextEvent()
    }

    override fun onEventExecute(event: NERecordEvent) {
        nextEvent = null
        eventHandlers.forEach {
            synchronized(this) {
                if (it.filterType(event)) {
                    it.process(event)
                }
            }
        }
    }

    override fun onClockProgressChanged(currentTime: Long, totalTime: Long) {
        uiListener.onProgressChanged(currentTime, totalTime)
    }

    override fun onClockStop() {
        stop()
        uiListener.onProgressChanged(0, getDuration())
    }

    override fun dispose() {
        super.dispose()
        eventDispatcher?.release()
    }
}