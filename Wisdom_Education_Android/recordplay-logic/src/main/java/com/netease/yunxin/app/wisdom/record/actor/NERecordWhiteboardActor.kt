/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.actor

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.NERecordPlayer
import com.netease.yunxin.app.wisdom.record.base.INERecordActor
import com.netease.yunxin.app.wisdom.record.whiteboard.NERecordWhiteboardManager
import com.netease.yunxin.app.wisdom.record.whiteboard.config.NERecordWhiteboardConfig
import com.netease.yunxin.app.wisdom.record.whiteboard.view.NERecordWhiteboardView

/**
 * Timeline whiteboard Actor class
 *
 */
class NERecordWhiteboardActor : INERecordActor {
    private val tag: String = "NERecordWhiteboardActor"
    private val whiteboardManager: NERecordWhiteboardManager = NERecordWhiteboardManager
    private var recordPlayer = NERecordPlayer.instance

    fun init(webView: NERecordWhiteboardView, config: NERecordWhiteboardConfig) {
        whiteboardManager.init(webView, config)
    }

    override fun start() {
        whiteboardManager.start()
    }

    override fun pause() {
        whiteboardManager.pause()
    }

    override fun seek(positionMs: Long) {
        whiteboardManager.seek(positionMs)
    }

    override fun stop() {
        whiteboardManager.stop()
    }

    override fun setSpeed(speed: Float) {
        whiteboardManager.setSpeed(speed)
    }

    override fun getDuration(): Long {
        return recordPlayer.getWhiteboardList().firstOrNull()?.size?.let { it * 1000L } ?: 0L
    }

    override fun getCurrentPosition(): Long {
        return whiteboardManager.getCurrentPosition()
    }

    @NERecordPlayState
    override fun getState(): Int {
        return whiteboardManager.getState()
    }

    override fun onStateChange(): LiveData<Int> {
        return whiteboardManager.onStateChange()
    }

    override fun updateState(playState: Int) {

    }

    /**
     * Switching the Playing Address
     *
     * @param urls Gz File address list
     */
    fun switchContentUrls(urls: List<String>) {

    }

    override fun toString(): String {
        return "NERecordWhiteboardActor(tag='$tag' state='${getState()}')"
    }

    override fun dispose() {
        super.dispose()
        whiteboardManager.finish()
    }

}