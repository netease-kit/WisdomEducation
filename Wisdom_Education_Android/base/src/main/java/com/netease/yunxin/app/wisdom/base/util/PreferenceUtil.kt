/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.base.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.preference.PreferenceManager
import java.util.*

object PreferenceUtil {
    private const val KEY_POLICY_SHOW = "KEY_POLICY_SHOW"
    private const val KEY_DEVICE_ID = "KEY_DEVICE_ID"
    private const val KEY_REUSE_IM = "KEY_REUSE_IM"
    private const val KEY_RECORD_PLAY_ROOMUUID = "KEY_RECORD_PLAY_ROOMUUID"
    private const val KEY_RECORD_PLAY_RTCID = "KEY_RECORD_PLAY_RTCID"
    private const val KEY_OPEN_CHATROOM = "KEY_OPEN_CHATROOM"
    private const val LOW_LATENCY_LIVE = "LOW_LATENCY_LIVE"
    private var sharedPreferences: SharedPreferences? = null
    fun init(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            context.applicationContext
        )
    }

    private fun put(key: String, value: Any) {
        val sp = getSharedPreferences()
        when (value) {
            is Boolean -> {
                sp!!.edit().putBoolean(key, value).apply()
            }
            is Int -> {
                sp!!.edit().putInt(key, value).apply()
            }
            is String -> {
                sp!!.edit().putString(key, value).apply()
            }
            is Float -> {
                sp!!.edit().putFloat(key, value).apply()
            }
            is Long -> {
                sp!!.edit().putLong(key, value).apply()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String, defaultValue: T): T {
        val result: Any
        val sp = getSharedPreferences()
        when (defaultValue) {
            is Boolean -> {
                result = sp!!.getBoolean(key, (defaultValue as Boolean))
            }
            is Int -> {
                result = sp!!.getInt(key, (defaultValue as Int))
            }
            is String -> {
                result = sp!!.getString(key, defaultValue as String)!!
            }
            is Float -> {
                result = sp!!.getFloat(key, (defaultValue as Float))
            }
            is Long -> {
                result = sp!!.getLong(key, (defaultValue as Long))
            }
            else -> {
                return defaultValue
            }
        }
        return result as T
    }

    @Throws(IllegalStateException::class)
    private fun getSharedPreferences(): SharedPreferences? {
        checkNotNull(sharedPreferences) { "PreferenceManager is not initialized. Please call init() before use!" }
        return sharedPreferences
    }

    var isShowPolicy: Boolean
        get() = get(KEY_POLICY_SHOW, false)
        set(isShow) {
            put(KEY_POLICY_SHOW, isShow)
        }
    var deviceId: String
        get() {
            var deviceId = get(KEY_DEVICE_ID, "")
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = UUID.randomUUID().toString()
                PreferenceUtil.deviceId = deviceId
            }
            return deviceId
        }
        set(deviceId) {
            put(KEY_DEVICE_ID, deviceId)
        }


    var reuseIM: Boolean
        get() {
            return get(KEY_REUSE_IM, false)
        }
        set(enable) {
            put(KEY_REUSE_IM, enable)
        }

    var recordPlay: Pair<String, String>
        get() {
            val roomUuid = get(KEY_RECORD_PLAY_ROOMUUID, "")
            val rtcCid = get(KEY_RECORD_PLAY_RTCID, "")
            return Pair(roomUuid, rtcCid)
        }
        set(pair) {
            val sp = getSharedPreferences()
            sp!!.edit()
                .putString(KEY_RECORD_PLAY_ROOMUUID, pair.first)
                .putString(KEY_RECORD_PLAY_RTCID, pair.second)
                .apply()
        }

    var enableChatRoom: Boolean
        get() {
            return get(KEY_OPEN_CHATROOM, true)
        }
        set(enable) {
            put(KEY_OPEN_CHATROOM, enable)
        }

    var lowLatencyLive: Boolean
        get() {
            return get(LOW_LATENCY_LIVE, true)
        }
        set(enable) {
            put(LOW_LATENCY_LIVE, enable)
        }

}