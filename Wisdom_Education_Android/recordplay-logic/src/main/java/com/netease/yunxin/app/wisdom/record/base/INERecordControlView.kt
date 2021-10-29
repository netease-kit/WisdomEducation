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
     * 初始化视图
     *
     */
    fun initViews()

    /**
     * 开始播放
     *
     */
    fun start()

    /**
     * 暂停播放
     *
     */
    fun pause()

    /**
     * 更新进度
     *
     * @param percent
     */
    fun setProgress(percent: Float)

    /**
     * 停止播放
     *
     */
    fun stop()

    /**
     * 静音
     *
     */
    fun switchAudio(audioEnable: Boolean)

    /**
     * 设置音量
     *
     */
    fun setVolume(volume: Float)

}