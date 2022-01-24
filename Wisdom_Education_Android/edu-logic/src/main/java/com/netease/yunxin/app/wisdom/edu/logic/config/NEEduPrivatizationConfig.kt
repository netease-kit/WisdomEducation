package com.netease.yunxin.app.wisdom.edu.logic.config

import android.content.Context
import android.content.res.AssetManager
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netease.lava.nertc.sdk.NERtcServerAddresses
import com.netease.yunxin.app.wisdom.whiteboard.config.NEWbPrivateConf
import com.netease.yunxin.kit.alog.ALog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * 获取私有化配置
 *
 */
object NEEduPrivatizationConfig {
    private val tag = "NEEduPrivatizationConfig"
    private val gson: Gson = Gson()

    private fun getConfig(context: Context, path: String): String? {
        val assetManager: AssetManager = context.getAssets()
        var reader: BufferedReader? = null
        var stringBuffer: StringBuffer? = null
        try {
            if (!Arrays.asList(*assetManager.list("")).contains(path)) {
                return null
            }
            reader = BufferedReader(
                InputStreamReader(assetManager.open(path))
            )
            stringBuffer = StringBuffer()
            var mLine: String?
            while (reader.readLine().also { mLine = it } != null) {
                //process line
                stringBuffer.append(mLine)
            }
            if (TextUtils.isEmpty(stringBuffer)) {
                return null
            }
        } catch (e: IOException) {
            //log the exception
            ALog.e(tag, "getConfig", e)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    //log the exception
                    ALog.e(tag, "getConfig", e)
                }
            }
        }
        return stringBuffer.toString()
    }

    private fun getRtc(context: Context): NEEduRtcServerAddresses? {
        val config: String? = getConfig(context, "rtc_server.conf")
        if (TextUtils.isEmpty(config)) {
            return null
        }
        try {
            val addresses: NEEduRtcServerAddresses = gson.fromJson(config, object : TypeToken<NEEduRtcServerAddresses>() {}.type)
            return addresses
        } catch (e: Throwable) {
            ALog.i(tag, "get rtc address fail $config")
            return null
        }
    }

    private fun getWb(context: Context): NEEduWbPrivateConf? {
        val config: String? = getConfig(context, "wb_server.conf")
        if (TextUtils.isEmpty(config)) {
            return null
        }
        try {
            val addresses: NEEduWbServerAddresses = gson.fromJson(config, object : TypeToken<NEEduWbServerAddresses>() {}.type)
            return addresses.privateConf
        } catch (e: Throwable) {
            ALog.i(tag, "get wb address fail $config")
            return null
        }
    }

    public fun getRtcServerAddresses(context: Context): NERtcServerAddresses? {
        val addresses = getRtc(context)
        val rtcAddresses = NERtcServerAddresses()
        if (addresses != null) {
            rtcAddresses.channelServer = addresses.channelServer
            rtcAddresses.statisticsServer = addresses.statisticsServer
            rtcAddresses.roomServer = addresses.roomServer
            rtcAddresses.compatServer = addresses.compatServer
            rtcAddresses.nosLbsServer = addresses.nosLbsServer
            rtcAddresses.nosUploadSever = addresses.nosUploadSever
            rtcAddresses.nosTokenServer = addresses.nosTokenServer
            rtcAddresses.useIPv6 = addresses.useIPv6 ?: false
            return rtcAddresses
        } else {
            return null
        }
    }

    public fun getWbPrivateConf(context: Context): NEWbPrivateConf? {
        val privateConf = getWb(context)
        val wbPrivateConf = NEWbPrivateConf()
        if (privateConf != null) {
            wbPrivateConf.roomServerAddr = privateConf.roomServerAddr
            wbPrivateConf.sdkLogNosAddr = privateConf.sdkLogNosAddr
            wbPrivateConf.dataReportAddr = privateConf.dataReportAddr
            wbPrivateConf.directNosAddr = privateConf.directNosAddr
            wbPrivateConf.mediaUploadAddr = privateConf.mediaUploadAddr
            wbPrivateConf.docTransAddr = privateConf.docTransAddr
            wbPrivateConf.fontDownloadUrl = privateConf.fontDownloadUrl
            return wbPrivateConf
        } else {
            return null
        }
    }
}