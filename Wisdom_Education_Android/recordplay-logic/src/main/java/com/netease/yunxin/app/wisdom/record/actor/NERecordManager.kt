/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.actor

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.netease.yunxin.app.wisdom.record.model.NERecordItem
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.base.util.observeForeverOnce
import com.netease.yunxin.app.wisdom.record.base.INERecordActor
import com.netease.yunxin.kit.alog.ALog

/**
 * actor装饰器类
 *
 * @constructor
 *
 * @param clockActor 管理者
 */
class NERecordManager(private var clockActor: NERecordClockActor) : NERecordDecorator(clockActor) {
    private var actorList: MutableList<NERecordDecorator> = mutableListOf()
    private val mergeLD: MediatorLiveData<Int> = MediatorLiveData()
    var actorCount: Int = Int.MAX_VALUE

    private val onChanged = Observer<Int> {

        ALog.w("actorCount--> $actorCount  state:${getState()}")
        actorList.forEach {
            ALog.w("-->${it.realActor} , state ${it.onStateChange().value} ")
        }

        if (getState() == NERecordPlayState.IDLE) {
            val isReady =
                actorList.count() == actorCount && actorList.all { it1 ->
                    (it1.realActor is NERecordWhiteboardActor && it1.onStateChange().value == NERecordPlayState.PREPARED)
                            || (it1.realActor is NERecordVideoActor
                            && (it1.onStateChange().value == NERecordPlayState.PAUSED || it1.onStateChange().value == NERecordPlayState.PLAYING))
                }
            if (isReady) updateState(NERecordPlayState.PREPARED)
            else {
                ALog.w("actorCount $actorCount ")
                actorList.forEach {
                    if (it.onStateChange().value != NERecordPlayState.PREPARED
                        && it.onStateChange().value != NERecordPlayState.PAUSED
                        && it.onStateChange().value != NERecordPlayState.PLAYING
                    )
                        ALog.w("$it is not ready, state ${it.onStateChange().value} ")
                }
            }
        } else {
            val isStop =
                actorList.count() == actorCount && actorList.all { it1 -> it1.onStateChange().value == NERecordPlayState.STOPPED }
            if (isStop) updateState(NERecordPlayState.STOPPED) else {
                ALog.w("actorCount $actorCount ")
                actorList.forEach {
                    if (it.onStateChange().value != NERecordPlayState.STOPPED) ALog.w("$it is not stop, state ${it.onStateChange().value} ")
                }
            }
        }
    }

    fun init() {
        mergeLD.observeForeverOnce {}
    }

    fun addActor(actor: INERecordActor) {
        actorList.add(object : NERecordDecorator(actor) {})
        mergeLD.addSource(actor.onStateChange(), onChanged)
    }

    fun removeActor(actor: INERecordActor) {
        actorList.removeAll {
            (it.realActor == actor).apply {
                if (this) {
                    it.dispose()
                    mergeLD.removeSource(it.onStateChange())
                }
            }
        }
    }

    fun removeAllActor() {
        actorList.forEach {
            it.dispose()
            mergeLD.removeSource(it.onStateChange())
        }
        actorList.clear()
    }

    fun getActor(recordItem: NERecordItem): INERecordActor? {
        return actorList.find {
            it.realActor is NERecordVideoActor && (it.realActor as NERecordVideoActor).recordItem == recordItem
        }?.realActor
    }

    fun getActor(recordItem: NERecordItem, subStream: Boolean): INERecordActor? {
        return actorList.find {
            it.realActor is NERecordVideoActor
                    && (it.realActor as NERecordVideoActor).recordItem.roomUid == recordItem.roomUid
                    && (it.realActor as NERecordVideoActor).recordItem.subStream == subStream
        }?.realActor
    }

    override fun start() {
        for (actor in actorList) {
            if (actor.getState() == NERecordPlayState.IDLE || actor.getState() == NERecordPlayState.PREPARING) {
                return
            }
        }
        val state = getState()
        for (actor in actorList) {
            if (actor.getState() == NERecordPlayState.STOPPED && state != NERecordPlayState.STOPPED) {
                ALog.w("not start actor: $actor")
            } else {
                actor.start()
            }
        }
        super.start()
    }

    override fun pause() {
        for (actor in actorList) {
            if (actor.getState() == NERecordPlayState.IDLE || actor.getState() == NERecordPlayState.PREPARING) {
                return
            }
        }
        super.pause()
        for (actor in actorList) {
            actor.pause()
        }
    }

    override fun seek(positionMs: Long) {
        for (actor in actorList) {
            if (actor.getState() == NERecordPlayState.IDLE || actor.getState() == NERecordPlayState.PREPARING) {
                return
            }
        }
        for (actor in actorList) {
            actor.seek(positionMs)
        }
        super.seek(positionMs)

        if (positionMs == getDuration()) {
            super.stop()
        }
    }

    override fun stop() {
        for (actor in actorList) {
            if (actor.getState() == NERecordPlayState.IDLE || actor.getState() == NERecordPlayState.PREPARING) {
                return
            }
        }
        super.stop()
        for (actor in actorList) {
            actor.stop()
        }
    }

    override fun switchAudio(audioEnable: Boolean) {
        super.switchAudio(audioEnable)
        for (actor in actorList) {
            actor.switchAudio(audioEnable)
        }
    }

    override fun setVolume(volume: Float) {
        super.setVolume(volume)
        for (actor in actorList) {
            actor.setVolume(volume)
        }
    }

    override fun switchVideo(videoEnable: Boolean) {
        super.switchVideo(videoEnable)
        for (actor in actorList) {
            actor.switchVideo(videoEnable)
        }
    }

    fun setHostActor(actor: INERecordActor) {
        clockActor.hostActor = actor
    }

    fun getHostActor(): INERecordActor {
        return clockActor.hostActor
    }

    fun prepareEvent() {
        clockActor.prepare()
    }

}