/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.UUID;

public class PreferenceUtil {

    private static final String KEY_POLICY_SHOW = "KEY_POLICY_SHOW";

    private static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";

    private static SharedPreferences sharedPreferences;

    public static void init(@NonNull Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext());
    }

    public static void put(@NonNull String key, @Nullable Object value) {
        SharedPreferences sp = getSharedPreferences();
        if (value instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Integer) {
            sp.edit().putInt(key, (Integer) value).apply();
        } else if (value instanceof String) {
            sp.edit().putString(key, (String) value).apply();
        } else if (value instanceof Float) {
            sp.edit().putFloat(key, (Float) value).apply();
        } else if (value instanceof Long) {
            sp.edit().putLong(key, (Long) value).apply();
        }
    }

    public static <T> T get(@NonNull String key, @Nullable T defaultValue) {
        Object result;
        SharedPreferences sp = getSharedPreferences();
        if (defaultValue instanceof Boolean) {
            result = sp.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Integer) {
            result = sp.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof String) {
            result = sp.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Float) {
            result = sp.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            result = sp.getLong(key, (Long) defaultValue);
        } else {
            return null;
        }
        return (T) result;
    }

    private static SharedPreferences getSharedPreferences() throws IllegalStateException {
        if (sharedPreferences == null) {
            throw new IllegalStateException("PreferenceManager is not initialized. Please call init() before use!");
        }
        return sharedPreferences;
    }

    public static boolean isShowPolicy() {
        return get(KEY_POLICY_SHOW, false);
    }

    public static void setShowPolicy(boolean isShow) {
        put(KEY_POLICY_SHOW, isShow);
    }

    public static void setDeviceId(String deviceId) {
        put(KEY_DEVICE_ID, deviceId);
    }

    public static String getDeviceId() {
        String deviceId = get(KEY_DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            setDeviceId(deviceId);
        }
        return deviceId;
    }
}
