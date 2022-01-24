/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard.bridge

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.netease.yunxin.app.wisdom.whiteboard.api.WhiteboardApi
import com.netease.yunxin.app.wisdom.whiteboard.model.JsMessage
import com.netease.yunxin.app.wisdom.whiteboard.model.JsMessageAction
import org.json.JSONException
import org.json.JSONObject

/**
 * 
 */
class JsBridge(private val whiteboardApi: WhiteboardApi) : Handler(Looper.getMainLooper()) {

    private val TAG: String = "JsBridge"

    private val gson: Gson = Gson()

    private val jsCall = 0x1

    private var loginSuccess = false

    private var enableDraw = false

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
            var jsMessage: JsMessage? = gson.fromJson(content, JsMessage::class.java)
            jsMessage?.let {
                when (jsMessage.action) {
                    JsMessageAction.webPageLoaded -> login()
                    JsMessageAction.webJoinWBSucceed -> onLogin()
                    JsMessageAction.webJoinWBFailed -> wbFail()
                    JsMessageAction.webCreateWBFailed -> createFail()
                    JsMessageAction.webLeaveWB -> leave()
                    JsMessageAction.webError -> webError()
                    JsMessageAction.webJsError -> jsError()
                    JsMessageAction.webGetAuth -> jsSendAuth()
                }
            }

        } catch (e: Exception) {

        }
    }

    private fun webError() {
        whiteboardApi.finish()
    }


    private fun jsError() {

    }

    private fun leave() {

    }

    private fun createFail() {

    }

    private fun wbFail() {

    }

    private fun onLogin() {
        loginSuccess = true
        evaluateJavascriptJsDirectCallSetContainerOptions()
        enableDraw(enableDraw)
    }

    private fun login() {
        val jsParam = JSONObject()
        val param = JSONObject()
        jsParam.put("action", "jsJoinWB")
        param.put("uid", whiteboardApi.getUid())
        param.put("channelName", whiteboardApi.getChannelName())
        param.put("record", true)
        param.put("debug", false)
        param.put("platform", "android")
        param.put("appKey", whiteboardApi.getAppKey())
        whiteboardApi.getPrivateConf()?.apply {
            val privateConf = JSONObject()
            privateConf.put("roomServerAddr", roomServerAddr)
            privateConf.put("sdkLogNosAddr", sdkLogNosAddr)
            privateConf.put("dataReportAddr", dataReportAddr)
            privateConf.put("mediaUploadAddr", mediaUploadAddr)
            privateConf.put("docTransAddr", docTransAddr)
            privateConf.put("fontDownloadUrl", fontDownloadUrl)
            param.put("privateConf", privateConf)
        }

        val drawPluginParams = JSONObject()
        drawPluginParams.put("zoomTo1AfterJoin", false)
        param.put("drawPluginParams", drawPluginParams)
        jsParam.put("param", param)
        runJs(jsParam.toString())
    }

    @Throws(JSONException::class)
    fun enableDraw(enable: Boolean) {
        enableDraw = enable
        if (!loginSuccess) {
            // cache
            return
        }
        evaluateJavascriptJsDirectCallToolCollection(enableDraw)
        evaluateJavascriptJsDirectCallEnable("enableDraw", enableDraw)
    }

    fun isEnableDraw(): Boolean {
        return enableDraw
    }

    private fun evaluateJavascriptJsDirectCallSetContainerOptions() {
        val params =
            "[{\"position\":\"bottomRight\",\"items\":[{\"tool\":\"select\",\"hint\":\"Select\"},{\"tool\":\"pen\",\"hint\":\"Brush\",\"stack\":\"horizontal\"},{\"tool\":\"shape\",\"hint\":\"Shape\",\"stack\":\"horizontal\"},{\"tool\":\"multiInOne\",\"hint\":\"More\",\"subItems\":[{\"tool\":\"element-eraser\"},{\"tool\":\"clear\"},{\"tool\":\"undo\"},{\"tool\":\"redo\"}]}]},{\"position\":\"topRight\",\"items\":[{\"tool\":\"multiInOne\",\"hint\":\"More\",\"subItems\":[{\"tool\":\"fitToContent\"},{\"tool\":\"fitToDoc\"},{\"tool\":\"pan\"},{\"tool\":\"zoomIn\"},{\"tool\":\"zoomOut\"},{\"tool\":\"visionLock\"}]},{\"tool\":\"zoomLevel\"}]},{\"position\":\"topLeft\",\"items\":[{\"tool\":\"pageBoardInfo\"},{\"tool\":\"preview\",\"hint\":\"Preview\",\"previewSliderPosition\":\"right\"}]}]"
        evaluateJavascript(
            "javascript:WebJSBridge({\"action\":\"jsDirectCall\",\"param\":{\"target\":\"toolCollection\",\"action\":\"setContainerOptions\",\"params\":[" +
                    params + "]}})")
    }

    private fun evaluateJavascriptJsDirectCallEnable(action: String, enable: Boolean) {
        evaluateJavascript(
            ("javascript:WebJSBridge({\"action\":\"jsDirectCall\",\"param\":{\"target\":\"drawPlugin\",\"action\":\"" +
                    action + "\",\"params\":[" + enable + "]}})"))
    }

    private fun evaluateJavascriptJsDirectCallToolCollection(visible: Boolean) {
        val params = "{\"topLeft\": {\"visible\": " + visible +
                "},\"topRight\": {\"visible\": true},\"bottomRight\": {\"visible\": " + visible + "}}"
        //String params1 = "{\"target\": {\"drawPlugin\" ，\"funcName\": \"zoomTo\",\"arg1\": 0.55}";
        evaluateJavascript(
            "javascript:WebJSBridge({\"action\":\"jsDirectCall\",\"param\":{\"target\":\"toolCollection\",\"action\":\"setVisibility\",\"params\":[" +
                    params + "]}})")
    }

    private fun runJs(param: String) {
        val escapedParam = param.replace("\"".toRegex(), "\\\\\"")
        evaluateJavascript("javascript:WebJSBridge(\"$escapedParam\")")
    }

    private fun evaluateJavascript(js: String) {
        val whiteboardView: WebView = whiteboardApi.getWhiteboardView()
        whiteboardView.evaluateJavascript(js, null)
    }

    private fun jsSendAuth() {
        val jsParam = JSONObject()
        val param = JSONObject()
        jsParam.put("action", "jsSendAuth")
        param.put("code", 200)
        whiteboardApi.getNonce()?.apply { param.put("nonce", this) }
        whiteboardApi.getCurTime()?.apply { param.put("curTime", this) }
        whiteboardApi.getChecksum()?.apply { param.put("checksum", this) }
        jsParam.put("param", param)
        runJs(jsParam.toString())
    }

}