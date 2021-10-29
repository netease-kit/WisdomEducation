/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.record.actor

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.base.INERecordActor
import com.netease.yunxin.app.wisdom.record.base.INERecordVideoActor

/**
 * Timeline decorator class
 *
 */
abstract class NERecordDecorator(var realActor: INERecordActor) : INERecordActor {

    override fun start() {
        if (getState() == NERecordPlayState.PREPARING) {
            return
        }
        realActor.start()
    }

    override fun seek(positionMs: Long) {
        if (getState() == NERecordPlayState.PREPARING
            || getState() == NERecordPlayState.IDLE
        ) {
            return
        }
        realActor.seek(positionMs)
    }

    override fun pause() {
        if (getState() != NERecordPlayState.PLAYING) {
            return
        }
        realActor.pause()
    }

    override fun stop() {
        if (getState() != NERecordPlayState.PLAYING
            && getState() != NERecordPlayState.PAUSED
        ) {
            return
        }
        realActor.stop()
    }

    override fun setSpeed(speed: Float) {
        if (getState() == NERecordPlayState.PREPARING) {
            return
        }
        realActor.setSpeed(speed)
    }

    @NERecordPlayState
    override fun getState(): Int {
        return realActor.getState()
    }

    override fun getDuration(): Long {
        return realActor.getDuration()
    }

    override fun getCurrentPosition(): Long {
        return realActor.getCurrentPosition()
    }

    override fun onStateChange(): LiveData<Int> {
        return realActor.onStateChange()
    }

    override fun updateState(@NERecordPlayState playState: Int) {
        realActor.updateState(playState)
    }

    override fun toString(): String {
        return "NERecordDecorator(actor=$realActor)"
    }

    open fun switchAudio(audioEnable: Boolean) {
        if (realActor is INERecordVideoActor) (realActor as INERecordVideoActor).switchAudio(audioEnable)
        if (realActor is NERecordClockActor) (realActor as NERecordClockActor).switchAudio(audioEnable)
    }

    open fun setVolume(volume: Float) {
        if (realActor is INERecordVideoActor) (realActor as INERecordVideoActor).setVolume(volume)
        if (realActor is NERecordClockActor) (realActor as NERecordClockActor).setVolume(volume)
    }

    open fun switchVideo(videoEnable: Boolean) {
        if (realActor is INERecordVideoActor) (realActor as INERecordVideoActor).switchVideo(videoEnable)
    }
}