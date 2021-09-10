/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.listener

/**
 * 用来做UI交互控制
 *
 */
interface NERecordUIListener {
    fun onStart()
    fun onPause()
    fun onProgressChanged(currentTime: Long, totalTime: Long)
    fun onStop()
    fun onSwitchAudio(audioEnable: Boolean)
    fun onVolumeChange(volume: Float)
}