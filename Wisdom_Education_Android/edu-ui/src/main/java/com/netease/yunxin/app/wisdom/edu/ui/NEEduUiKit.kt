/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduEntryRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduOptions
import com.netease.yunxin.app.wisdom.edu.ui.impl.NEEduUiKitImpl

/**
 * Created by hzsunyj on 4/21/21.
 */
interface NEEduUiKit {

    companion object {

        var instance: NEEduUiKit? = null

        fun init(): LiveData<NEResult<NEEduUiKit>> {
            val liveData: MediatorLiveData<NEResult<NEEduUiKit>> = MediatorLiveData()
            val eduUiKit = NEEduUiKitImpl()
            eduUiKit.init().also {
                it.observeForever(object : Observer<NEResult<NEEduManager>> {
                    override fun onChanged(t: NEResult<NEEduManager>) {
                        it.removeObserver(this)
                        if (t.success()) {
                            instance = eduUiKit
                            liveData.postValue(NEResult(t.code, eduUiKit))
                        } else {
                            liveData.postValue(NEResult(t.code))
                        }
                    }

                })
            }
            return liveData
        }

        fun destroy() {
            instance?.destroy()
            instance = null
        }

        /// must call application onCreate
        fun config(context: Application, eduOptions: NEEduOptions) {
            NEEduManager.config(context, eduOptions)
        }
    }

    var neEduLoginRes: NEEduLoginRes?

    var neEduManager: NEEduManager?

    fun destroy()

    fun enterClass(context: Context, neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>>
}