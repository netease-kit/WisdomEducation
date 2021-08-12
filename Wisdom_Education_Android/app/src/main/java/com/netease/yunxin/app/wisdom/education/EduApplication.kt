/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.education

import android.app.Application
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.util.NIMUtil
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.base.util.ScreenUtil
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
        ScreenUtil.init(this)
        CrashReport.initCrashReport(this, "645e346c8e", true)

        PreferenceUtil.init(this)
        if (PreferenceUtil.reuseIM) {
            // 以下代码用于演示IM复用
            val value = SDKOptions()
            value.appKey = BuildConfig.APP_KEY
            value.disableAwake = true
            NIMClient.config(this, null, value)
            if (NIMUtil.isMainProcess(this)) {
                // protect async init
                NIMClient.initSDK()
            }
        }
        NEEduUiKit.config(
            this,
            NEEduOptions(
                BuildConfig.APP_KEY,
                BuildConfig.AUTHORIZATION,
                BuildConfig.API_BASE_URL,
                PreferenceUtil.reuseIM
            )
        )

        if (NIMUtil.isMainProcess(this)) {
            ToastUtil.init(this)
            ALog.i("Application init ${BuildConfig.TIMESTAMP}")
        }
    }
}