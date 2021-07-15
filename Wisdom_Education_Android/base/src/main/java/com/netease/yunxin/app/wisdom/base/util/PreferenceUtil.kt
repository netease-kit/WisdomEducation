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
    private var sharedPreferences: SharedPreferences? = null
    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            context.applicationContext
        )
    }

    fun put(key: String, value: Any?) {
        val sp = getSharedPreferences()
        if (value is Boolean) {
            sp!!.edit().putBoolean(key, (value as Boolean?)!!).apply()
        } else if (value is Int) {
            sp!!.edit().putInt(key, (value as Int?)!!).apply()
        } else if (value is String) {
            sp!!.edit().putString(key, value as String?).apply()
        } else if (value is Float) {
            sp!!.edit().putFloat(key, (value as Float?)!!).apply()
        } else if (value is Long) {
            sp!!.edit().putLong(key, (value as Long?)!!).apply()
        }
    }

    operator fun <T> get(key: String, defaultValue: T?): T? {
        val result: Any
        val sp = getSharedPreferences()
        when (defaultValue) {
            is Boolean -> {
                result = sp!!.getBoolean(key, (defaultValue as Boolean?)!!)
            }
            is Int -> {
                result = sp!!.getInt(key, (defaultValue as Int?)!!)
            }
            is String -> {
                result = sp!!.getString(key, defaultValue as String?)!!
            }
            is Float -> {
                result = sp!!.getFloat(key, (defaultValue as Float?)!!)
            }
            is Long -> {
                result = sp!!.getLong(key, (defaultValue as Long?)!!)
            }
            else -> {
                return null
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
        get() = get(KEY_POLICY_SHOW, false)!!
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
            return deviceId!!
        }
        set(deviceId) {
            put(KEY_DEVICE_ID, deviceId)
        }
}