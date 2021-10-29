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
     * 设置音频开关
     *
     * @param audioEnable 是否打开音频
     */
    fun switchAudio(audioEnable: Boolean)

    /**
     * 设置音量(0.0 ~ 1.0, 0.0为静音，1.0为最大)
     *
     * @param volume
     */
    fun setVolume(volume: Float)

    /**
     * 设置视频开关
     *
     * @param videoEnable 是否打开视频
     */
    fun switchVideo(videoEnable: Boolean)
}