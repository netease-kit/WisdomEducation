/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.record.base

/**
 * Playback player UI update interface
 *
 */
interface INERecordControlView {
    /**
     * instantiate the view
     *
     */
    fun initViews()

    /**
     * Start playing
     *
     */
    fun start()

    /**
     * Pause the player
     *
     */
    fun pause()

    /**
     * Update the progress
     *
     * @param percent
     */
    fun setProgress(percent: Float)

    /**
     * Stop playing
     *
     */
    fun stop()

    /**
     * Mute
     *
     */
    fun switchAudio(audioEnable: Boolean)

    /**
     * Set the volume
     *
     */
    fun setVolume(volume: Float)

}