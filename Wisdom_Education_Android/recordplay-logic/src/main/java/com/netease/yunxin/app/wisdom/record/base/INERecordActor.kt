/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.base

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState

/**
 * record actor abstract class
 *
 */
interface INERecordActor {
    /**
     * Start playback
     */
    fun start()

    /**
     * Pause playback
     */
    fun pause()

    /**
     * Set a specified time for playback
     * @param positionMs The specified time
     */
    fun seek(positionMs: Long)

    /**
     * Stop playback
     */
    fun stop()

    /**
     * Set the playback speed
     * @param speed The playback speed
     */
    fun setSpeed(speed: Float)

    /**
     * Get the duration in milliseconds
     */
    fun getDuration(): Long

    /**
     * Get the current playback position in milliseconds
     */
    fun getCurrentPosition(): Long

    /**
     * Get the playback state
     * @return
     */
    @NERecordPlayState
    fun getState(): Int

    /**
     * The playback state changes
     *
     * @return The livedata callback
     */
    fun onStateChange(): LiveData<Int>

    /**
     * Update the playback state
     *
     * @param playState The playback state [NERecordPlayState]
     */
    fun updateState(@NERecordPlayState playState: Int)

    /**
     * Release the player instance
     *
     */
    fun dispose() {}
}