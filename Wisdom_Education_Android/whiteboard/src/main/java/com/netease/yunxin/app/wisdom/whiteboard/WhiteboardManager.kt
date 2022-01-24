/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard

import android.annotation.SuppressLint
import android.text.TextUtils
import com.netease.yunxin.app.wisdom.whiteboard.api.WhiteboardApi
import com.netease.yunxin.app.wisdom.whiteboard.bridge.JsBridge
import com.netease.yunxin.app.wisdom.whiteboard.config.NEWbPrivateConf
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.model.WhiteboardUser
import com.netease.yunxin.app.wisdom.whiteboard.view.WhiteboardView

/**
 * 
 */
object WhiteboardManager : WhiteboardApi() {

    private const val DEFAULT_URL = "https://yiyong-xedu-v2-static.netease.im/whiteboard-webview/g2/webview.html"

    private var webView: WhiteboardView? = null

    private lateinit var jsBridge: JsBridge

    private lateinit var config: WhiteboardConfig

    fun init(webView: WhiteboardView, config: WhiteboardConfig) {
        this.config = config
        this.webView = webView
        jsBridge = JsBridge(this)
        initWebView(webView)
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebView(webView: WhiteboardView) {
        webView.addJavascriptInterface(jsBridge, "jsBridge")
        if (TextUtils.isEmpty(config.whiteBoardUrl)) {
            webView.loadUrl(DEFAULT_URL)
        } else {
            webView.loadUrl(config.whiteBoardUrl!!)
        }
    }

    override fun getChannelName(): String {
        return config.channelName
    }

    override fun getUserInfo(): WhiteboardUser {
        return WhiteboardUser(config.imAccid, config.imToken)
    }

    override fun getAppKey(): String {
        return config.appKey
    }

    override fun getUid(): Long {
        return config.rtcUid
    }

    override fun getNonce(): String? {
        return config.wbAuth?.nonce
    }

    override fun getPrivateConf(): NEWbPrivateConf? {
        return config.privateConf
    }

    override fun getCurTime(): Long? {
        return config.wbAuth?.curTime?.toLong()
    }

    override fun getChecksum(): String? {
        return config.wbAuth?.checksum
    }

    override fun setEnableDraw(enable: Boolean) {
        jsBridge.enableDraw(enable)
    }

    override fun isEnableDraw(): Boolean {
        return jsBridge.isEnableDraw()
    }

    override fun getOwnerAccount(): String {
        return if (config.isHost) config.imAccid else ""
    }

    override fun getWhiteboardView(): WhiteboardView {
        return webView!!
    }

    /**
     * Live with the host
     */
    override fun finish() {
        webView?.destroy()
        webView = null
    }
}