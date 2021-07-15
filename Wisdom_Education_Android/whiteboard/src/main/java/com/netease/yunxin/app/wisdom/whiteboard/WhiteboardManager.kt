/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard

import android.annotation.SuppressLint
import android.text.TextUtils
import com.netease.yunxin.app.wisdom.whiteboard.api.WhiteboardApi
import com.netease.yunxin.app.wisdom.whiteboard.bridge.JsBridge
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.model.WhiteboardUser
import com.netease.yunxin.app.wisdom.whiteboard.view.WhiteboardView

/**
 * Created by hzsunyj on 2021/5/21.
 */
object WhiteboardManager : WhiteboardApi() {

    private const val DEFAULT_URL = "https://yiyong-xedu-v2-static.netease.im/whiteboard/stable/webview.html"

    private lateinit var webView: WhiteboardView

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
        return webView
    }

    /**
     * 与宿主同生共死
     */
    override fun finish() {
        if (webView != null) {
            //webView.destroy()
        }
    }
}