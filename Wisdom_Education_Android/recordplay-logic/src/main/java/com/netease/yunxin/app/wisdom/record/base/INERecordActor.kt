/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.base

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.listener.NERecordClockListener

/**
 * 时间轴演员抽象类
 *
 */
interface INERecordActor {
    /**
     * 开始播放
     */
    fun start()

    /**
     * 暂停播放
     */
    fun pause()

    /**
     * 设置到指定时间点播放
     * @param positionMs 指定时间点
     */
    fun seek(positionMs: Long)

    /**
     * 停止播放
     */
    fun stop()

    /**
     * 设置播放速度
     * @param speed 播放速度
     */
    fun setSpeed(speed: Float)

    /**
     * 获取播放时长(单位ms)
     */
    fun getDuration(): Long

    /**
     * 当前播放时间(单位ms)
     */
    fun getCurrentPosition(): Long

    /**
     * 获取播放状态
     * @return
     */
    @NERecordPlayState
    fun getState(): Int

    /**
     * 播放状态发生变化
     *
     * @return 回调livedata
     */
    fun onStateChange(): LiveData<Int>

    /**
     * 更新播放状态
     *
     * @param playState 播放状态[NERecordPlayState]
     */
    fun updateState(@NERecordPlayState playState: Int)

    /**
     * 用于设置主持人为进度的参照基准
     *
     * @param clockListener
     */
    fun setClockListener(clockListener: NERecordClockListener) {}

    /**
     * 销毁
     *
     */
    fun dispose() {}
}