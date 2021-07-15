/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard.view

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.http.SslError
import android.util.AttributeSet
import android.webkit.*
import com.netease.yunxin.app.wisdom.whiteboard.BuildConfig

/**
 * Created by hzsunyj on 2021/5/21.
 */
open class WhiteboardView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    WebView(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null, 0, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    init {
        this.settings.mediaPlaybackRequiresUserGesture = false
        this.settings.javaScriptEnabled = true
        this.webChromeClient = FixWebChromeClient()
        this.webViewClient = FixWebViewClient()
        // static method
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true)
        }
    }

    internal class FixWebViewClient : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed()
        }
    }

    internal class FixWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("Alert")
            builder.setMessage(message)
            builder.setPositiveButton(R.string.ok
            ) { _: DialogInterface?, _: Int -> result.confirm() }
            builder.setCancelable(false)
            builder.create().show()
            return true
        }
    }
}