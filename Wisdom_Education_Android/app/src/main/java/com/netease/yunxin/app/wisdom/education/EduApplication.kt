/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.education

import android.app.Application
import android.util.Log
import com.netease.nimlib.sdk.util.NIMUtil
import com.netease.yunxin.app.wisdom.base.network.RetrofitManager
import com.netease.yunxin.app.wisdom.base.util.CryptoUtil
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduOptions
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit
import com.netease.yunxin.kit.alog.ALog
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created by hzsunyj on 4/20/21.
 */
class EduApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        ALog.init(this, if (BuildConfig.DEBUG) ALog.LEVEL_ALL else ALog.LEVEL_INFO)
        CrashReport.initCrashReport(this, "645e346c8e", true)
        NEEduUiKit.config(this, NEEduOptions(BuildConfig.APP_KEY, BuildConfig.APP_ID, BuildConfig.API_BASE_URL))
        if (NIMUtil.isMainProcess(this)) {
            ToastUtil.init(this)
            PreferenceUtil.init(this)
            RetrofitManager.instance().addHeader(
                "Authorization",
                CryptoUtil.getAuth(BuildConfig.AUTHORIZATION)
            ).addHeader(
                "deviceId",
                PreferenceUtil.getDeviceId()
            ).addHeader(
                "clientType",
                "android"
            )
            ALog.i("Application init ${BuildConfig.TIMESTAMP}")
        }
    }
}