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
 * Configure and initialize the SDK and get NEEduManager
 */
interface NEEduUiKit {

    companion object {

        /**
         *  instance of NEEduUiKit
         */
        var instance: NEEduUiKit? = null

        /**
         * Initialize components
         *
         * @param uuid The userUuid for authentication. set "" for anonymous login
         * @param token The userToken for authentication. set "" for anonymous login
         * @return callback
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

        /**
         * destroy NEEduUiKit instance
         *
         */
        fun destroy() {
            instance?.destroy()
            instance = null
        }

        /**
         * Configure the SDK. Call the interface in onCreate of application
         *
         * @param context The context information
         * @param eduOptions configuration options
         */
        fun config(context: Application, eduOptions: NEEduOptions) {
            NEEduManager.config(context, eduOptions)
        }
    }

    /**
     * the information data returned by the login interface
     */
    var neEduLoginRes: NEEduLoginRes?

    /**
     * instance of NEEduManager, which provides services of room, member, rtc, im, board, hands up, screen share.
     */
    var neEduManager: NEEduManager?

    /**
     * destroy instance of NEEduUiKit
     *
     */
    fun destroy()

    /**
     * enter class
     *
     * @param context
     * @param neEduClassOptions
     * @return
     */
    fun enterClass(context: Context, neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>>
}