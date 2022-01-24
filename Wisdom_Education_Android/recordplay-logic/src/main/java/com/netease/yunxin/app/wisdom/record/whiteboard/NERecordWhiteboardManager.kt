/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.whiteboard

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.whiteboard.api.NERecordWhiteboardApi
import com.netease.yunxin.app.wisdom.record.whiteboard.bridge.NERecordJsBridge
import com.netease.yunxin.app.wisdom.record.whiteboard.config.NERecordWhiteboardConfig
import com.netease.yunxin.app.wisdom.record.whiteboard.view.NERecordWhiteboardView


object NERecordWhiteboardManager : NERecordWhiteboardApi() {

    private const val DEFAULT_URL =
        "https://yiyong-xedu-v2-static.netease.im/whiteboard-webview/g2/webview.record.html"

    private var webView: NERecordWhiteboardView? = null

    private lateinit var jsBridge: NERecordJsBridge

    private lateinit var config: NERecordWhiteboardConfig

    private val playStateLD: MediatorLiveData<Int> = MediatorLiveData()

    private var playState: Int = NERecordPlayState.IDLE

    fun init(webView: NERecordWhiteboardView, config: NERecordWhiteboardConfig) {
        this.config = config
        this.webView = webView
        jsBridge = NERecordJsBridge(this)
        initWebView(webView)
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebView(webView: NERecordWhiteboardView) {
        webView.addJavascriptInterface(jsBridge, "jsBridge")
        if (TextUtils.isEmpty(config.whiteBoardUrl)) {
            webView.loadUrl(DEFAULT_URL)
        } else {
            webView.loadUrl(config.whiteBoardUrl!!)
        }
    }

    override fun getUrls(): List<String> {
        return config.urls
    }

    override fun getWhiteboardView(): NERecordWhiteboardView {
        return webView!!
    }

    override fun start() {
        jsBridge.jsPlay()
    }

    override fun pause() {
        jsBridge.jsPause()
    }

    override fun seek(time: Long) {
        jsBridge.jsSeekTo(time)
        if (playState != NERecordPlayState.PAUSED && playState != NERecordPlayState.PREPARED)
            jsBridge.jsPlay() // Continue playing
    }

    override fun stop() {

    }

    override fun setSpeed(speed: Float) {
        jsBridge.jsSetPlaySpeed(speed)
    }

    override fun getDuration(): Long {
        return 0L
    }

    override fun getCurrentPosition(): Long {
        return jsBridge.currentPosition
    }

    override fun getState(): Int {
        return jsBridge.state
    }

    override fun setViewer(viewer: String) {
        jsBridge.jsSetViewer(viewer)
    }

    override fun setTimeRange(startTime: Long?, endTime: Long?) {
        jsBridge.jsSetTimeRange(startTime, endTime)
    }

//    override fun getState() {
//        jsBridge.jsGetState()
//    }

    /**
     * live with the class instance
     */
    override fun finish() {
        webView?.destroy()
        webView = null
    }

    override fun onStateChange(): LiveData<Int> {
        return playStateLD
    }

    override fun updateState(@NERecordPlayState playState: Int) {
        // sync time
        config.startTime?.let {
            if (playState == NERecordPlayState.PREPARED) {
                setTimeRange(config.startTime, null)
            }
        }
        this.playState = playState
        playStateLD.postValue(playState)
    }
}