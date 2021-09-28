/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.util.observeForeverOnce
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduEntryRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduOptions
import com.netease.yunxin.app.wisdom.edu.ui.impl.NEEduUiKitImpl

/**
 * 提供SDK配置，SDK初始化等基础能力，同时获取NEEduManager
 */
interface NEEduUiKit {

    companion object {

        var instance: NEEduUiKit? = null

        /**
         * 初始化组件
         *
         * @param uuid 用户鉴权userUuid。匿名登录时请设置为空字符串""
         * @param token 用户鉴权userToken。匿名登录时请设置为空字符串""
         * @param isLiveClass 是否是直播大班课
         * @return 接口回调
         */
        fun init(uuid: String, token: String): LiveData<NEResult<NEEduUiKit>> {
            val liveData: MediatorLiveData<NEResult<NEEduUiKit>> = MediatorLiveData()
            val eduUiKit = NEEduUiKitImpl()
            eduUiKit.init(uuid, token).also {
                it.observeForeverOnce { t ->
                    if (t.success()) {
                        instance = eduUiKit
                        liveData.postValue(NEResult(t.code, eduUiKit))
                    } else {
                        liveData.postValue(NEResult(t.code))
                    }
                }
            }
            return liveData
        }

        fun destroy() {
            instance?.destroy()
            instance = null
        }

        /**
         * 全局配置 SDK，必须在application的onCreate中调用
         *
         * @param context 应用上下文
         * @param eduOptions 配置项
         */
        fun config(context: Application, eduOptions: NEEduOptions) {
            NEEduManager.config(context, eduOptions)
        }
    }

    var neEduLoginRes: NEEduLoginRes?

    var neEduManager: NEEduManager?

    fun destroy()

    fun enterClass(context: Context, neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>>
}