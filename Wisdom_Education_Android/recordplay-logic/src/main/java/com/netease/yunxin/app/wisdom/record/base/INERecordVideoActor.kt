/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.base

/**
 * Timeline actor Abstract class
 *
 */
interface INERecordVideoActor : INERecordActor {
    /**
     * Enable or disable audio
     *
     * @param audioEnable Enable or disable audio
     */
    fun switchAudio(audioEnable: Boolean)

    /**
     * Set the volume. Value range: 0.0 to 1.0. A value of 0.0 indicates muted. A value of 1.0 indicates the maximum volume.
     *
     * @param volume
     */
    fun setVolume(volume: Float)

    /**
     * Enable or disable video
     *
     * @param videoEnable Enable or disable video
     */
    fun switchVideo(videoEnable: Boolean)
}