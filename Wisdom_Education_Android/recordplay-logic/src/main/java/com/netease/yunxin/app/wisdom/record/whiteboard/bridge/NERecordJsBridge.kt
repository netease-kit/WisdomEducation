/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.whiteboard.bridge

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.whiteboard.api.NERecordWhiteboardApi
import com.netease.yunxin.app.wisdom.record.whiteboard.model.NERecordJsMessage
import com.netease.yunxin.app.wisdom.record.whiteboard.model.NERecordJsMessageAction
import com.netease.yunxin.app.wisdom.record.whiteboard.model.NERecordJsTickMessage
import com.netease.yunxin.kit.alog.ALog
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by hzsunyj on 2021/5/21.
 */
class NERecordJsBridge(private val whiteboardApi: NERecordWhiteboardApi) : Handler(Looper.getMainLooper()) {

    private val TAG: String = "JsBridge"

    private val gson: Gson = Gson()

    private val jsCall = 0x1

    var state: Int = NERecordPlayState.IDLE

    var currentPosition: Long = 0

    @JavascriptInterface
    fun NativeFunction(content: String) {
        val message: Message = this.obtainMessage(jsCall)
        message.obj = content
        message.sendToTarget()
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        var content: String = msg.obj as String
//        ALog.i(TAG, String.format("called by js, content=%s", content))
        try {
            var jsMessage: NERecordJsMessage? = gson.fromJson(content, NERecordJsMessage::class.java)
            jsMessage?.let {
                when (jsMessage.action) {
                    NERecordJsMessageAction.webPageLoaded -> jsInitPlayer()
                    NERecordJsMessageAction.webAssetsLoaded -> onWebAssetsLoaded(jsMessage.param)
                    NERecordJsMessageAction.webPlayFinish -> onWebPlayFinish()
                    NERecordJsMessageAction.webPlayTick -> onWebPlayTick(jsMessage.param)
                    NERecordJsMessageAction.webPlayDurationChange -> onWebPlayDurationChange(jsMessage.param)
                    NERecordJsMessageAction.webSendAppState -> onWebSendAppState(jsMessage.param)
                    NERecordJsMessageAction.webError -> webError()
                    NERecordJsMessageAction.webJsError -> jsError()
                }
            }

        } catch (e: Exception) {
            ALog.e(TAG, "handleMessage", e)
        }
    }

    private fun webError() {
        whiteboardApi.finish()
    }


    private fun jsError() {

    }

    private fun onWebSendAppState(param: Object?) {
        ALog.i(TAG, String.format("onWebSendAppState, param=%s", param))
    }

    private fun onWebPlayDurationChange(param: Object?) {
        ALog.i(TAG, String.format("onWebPlayDurationChange, param=%s", param))
    }

    private fun onWebAssetsLoaded(param: Object?) {
        ALog.i(TAG, String.format("onWebAssetsLoaded, param=%s", param))
        updateState(NERecordPlayState.PREPARED)
    }

    private fun onWebPlayTick(param: Object?) {
//        ALog.i(TAG, String.format("onWebPlayTick, param=%s", param))
        if (this.state == NERecordPlayState.IDLE || this.state == NERecordPlayState.PREPARED) return
        try {
            var tickMessage: NERecordJsTickMessage? = gson.fromJson(param.toString(), NERecordJsTickMessage::class.java)
            if (tickMessage != null) {
                currentPosition = tickMessage.time
            }
        } catch (e: Exception) {
            ALog.e(TAG, "handleMessage", e)
        }
    }

    private fun onWebPlayFinish() {
        ALog.i(TAG, "onWebPlayFinish")
        updateState(NERecordPlayState.STOPPED)
    }

    private fun jsInitPlayer() {
        updateState(NERecordPlayState.PREPARING)
        val jsParam = JSONObject()
        val param = JSONObject()
        val urls = JSONArray()
        jsParam.put("action", "jsInitPlayer")
        for (url in whiteboardApi.getUrls()) {
            urls.put(url)
        }
        param.put("urls", urls)
        // param.put("controlContainerId", "toolbar") // show toolbar, open it when develop or debug
        jsParam.put("param", param)
        param.getString("urls")
        runJs(jsParam.toString())
    }

    fun jsPlay() {
        runJsAction("jsPlay", JSONObject())
        updateState(NERecordPlayState.PLAYING)
    }

    fun jsPause() {
        runJsAction("jsPause", JSONObject())
        updateState(NERecordPlayState.PAUSED)
    }

    fun jsSeekTo(time: Long) {
        runJsAction("jsSeekTo", JSONObject().put("time", time))
        currentPosition = time
    }

    fun jsSetPlaySpeed(speed: Float) {
        runJsAction("jsSetPlaySpeed", JSONObject().put("speed", speed))
    }

    fun jsSetViewer(viewer: String) {
        runJsAction("jsSetViewer", JSONObject().put("viewer", viewer))
    }

    fun jsSetTimeRange(startTime: Long?, endTime: Long?) {
        runJsAction("jsSetTimeRange", JSONObject().apply {
            startTime?.let { put("startTime", startTime) }
            endTime?.let { put("endTime", endTime) }
        })
    }

    private fun runJsAction(action: String, param: JSONObject) {
        val jsParam = JSONObject()
        jsParam.put("action", action)
        jsParam.put("param", param)
        runJs(jsParam.toString())
    }

    private fun runJs(param: String) {
        var escapedParam = param.replace("\\\\".toRegex(), "\\\\\\\\")
            .replace("\"".toRegex(), "\\\\\"")
        evaluateJavascript("javascript:WebJSBridge(\"$escapedParam\")")
    }

    private fun evaluateJavascript(js: String) {
        val whiteboardView: WebView = whiteboardApi.getWhiteboardView()
        whiteboardView.evaluateJavascript(js, null)
    }

    private fun updateState(state: Int) {
        this.state = state
        whiteboardApi.updateState(state)
    }

}