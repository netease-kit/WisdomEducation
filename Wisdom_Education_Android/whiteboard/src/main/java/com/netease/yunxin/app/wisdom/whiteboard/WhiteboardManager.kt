/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import android.util.Base64
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import com.netease.yunxin.app.wisdom.whiteboard.api.WhiteboardApi
import com.netease.yunxin.app.wisdom.whiteboard.bridge.JsBridge
import com.netease.yunxin.app.wisdom.whiteboard.config.NEWbPrivateConf
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.model.WhiteboardUser
import com.netease.yunxin.app.wisdom.whiteboard.utils.HexDump
import com.netease.yunxin.app.wisdom.whiteboard.view.WhiteboardView
import com.netease.yunxin.kit.alog.ALog
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * 
 */
object WhiteboardManager : WhiteboardApi() {

    private const val TAG = "WhiteboardManager"

    private const val DEFAULT_URL = "https://yiyong.netease.im/yiyong-static/statics/whiteboard-webview/webview.html"

    /**
     * 音视频转码presetId, 详见 @see <a href="https://doc.yunxin.163.com/vod/docs/Dc5NDE5NjM?platform=server">创建视频转码模板</a>
     */
    private const val PRESET_ID = "104868090"

    private var webView: WhiteboardView? = null

    private lateinit var jsBridge: JsBridge

    private lateinit var config: WhiteboardConfig

    private var bgHandler: Handler? = null

    private var bgHandlerThread: HandlerThread? = null

    private lateinit var choosePictureLaunch: ActivityResultLauncher<Uri>

    fun init(webView: WhiteboardView, config: WhiteboardConfig) {
        this.config = config
        this.webView = webView
        jsBridge = JsBridge(this)
        jsBridge.setPresetId(PRESET_ID)

        bgHandlerThread = HandlerThread("bg")
        bgHandlerThread?.let {
            it.start()
            bgHandler = Handler(it.looper)
        }


        choosePictureLaunch = (webView.context as ComponentActivity).registerForActivityResult(ChoosePicture()) {
            onGetChosenFile(it)
        }

        initWebView(webView)
    }

    fun reload(){
        webView?.reload()
    }

    fun setConfig(whiteboardConfig: WhiteboardConfig){
     config = whiteboardConfig
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebView(webView: WhiteboardView) {
        webView.addJavascriptInterface(jsBridge, "jsBridge")
        if (TextUtils.isEmpty(config.whiteBoardUrl)) {
            webView.loadUrl(DEFAULT_URL)
        } else {
            webView.loadUrl(config.whiteBoardUrl!!)
        }

        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                ALog.i(TAG, "onJsAlert")
                val builder = AlertDialog.Builder(webView.context)
                builder.setTitle("提示")
                builder.setMessage(message)
                builder.setPositiveButton(
                    R.string.ok
                ) { dialog: DialogInterface?, which: Int -> result.confirm() }
                builder.setCancelable(false)
                builder.create().show()
                return true
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                ALog.i(TAG, "onShowFileChooser")
                jsBridge.setFileValueCallback(filePathCallback)
                choosePictureLaunch.launch(null)
                return true
            }
        })

        webView.setDownloadListener(DownloadListener { url: String, userAgent: String?, contentDisposition: String?, mimeType: String?, contentLength: Long ->
            val key = "base64,"
            val keyIndex = url.indexOf(key)
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            val dataBase64Str =
                if (keyIndex < 0) url else url.substring(keyIndex + key.length)
            if (TextUtils.isEmpty(dataBase64Str)) {
                ALog.e(
                    TAG,
                    "empty file"
                )
                return@DownloadListener
            }
            val dataOriginBytes =
                Base64.decode(dataBase64Str, Base64.DEFAULT)
            bgHandler?.post {
                ALog.i(
                    TAG,
                    "dataOriginBytes=" + if (dataOriginBytes == null) "null" else HexDump.toHex(
                        dataOriginBytes
                    )
                )
            }
            val imgName = StringBuilder()
            imgName.append(UUID.randomUUID().toString())
            if (!TextUtils.isEmpty(ext)) {
                imgName.append(".")
                imgName.append(ext)
            }
            val local = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .absolutePath, imgName.toString()
            )
            try {
                if (!local.exists() && !local.createNewFile()) {
                    ALog.i(
                        TAG,
                        "path error, exist: " + local.exists()
                    )
                    return@DownloadListener
                }
                val outputStream = FileOutputStream(local, false)
                outputStream.write(dataOriginBytes)
                outputStream.close()
                Toast.makeText(webView.context, "已下载到 " + local.absolutePath, Toast.LENGTH_LONG).show()
                ALog.i(
                    TAG,
                    "download complete, path is " + local.absolutePath
                )
            } catch (e: Throwable) {
                Toast.makeText(webView.context, "下载异常 " + local.absolutePath, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        })
    }

    private fun onGetChosenFile(uri: Uri?) {
        jsBridge.transferFile(uri)
    }

    class ChoosePicture : ActivityResultContract<Uri, Uri?>() {

        @CallSuper
        override fun createIntent(context: Context, input: Uri?): Intent {
            val choosePictureIntent = Intent(Intent.ACTION_GET_CONTENT)
            choosePictureIntent.type = "*/*"
            return choosePictureIntent
        }

        override fun getSynchronousResult(context: Context, input: Uri?): SynchronousResult<Uri?>? {
            return null
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (intent == null || resultCode != Activity.RESULT_OK)
                null
            else
                intent.data
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
        bgHandler?.removeCallbacksAndMessages(null)
        bgHandlerThread?.quit()
        webView?.destroy()
        webView = null
    }
}