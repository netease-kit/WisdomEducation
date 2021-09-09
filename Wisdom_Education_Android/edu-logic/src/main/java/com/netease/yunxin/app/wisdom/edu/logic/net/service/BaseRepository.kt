/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHttpCode
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Created by hzsunyj on 2021/5/14.
 */
open class BaseRepository {

    companion object {
        lateinit var appKey: String
        val passthrough: Boolean = true
        val errorLD: MediatorLiveData<Int> = MediatorLiveData()
    }

    fun <T> interceptor(result: LiveData<NEResult<T>>): LiveData<NEResult<T>> {
        return result.map {
            if (!it.success()) {
                /// handle error
                if (it.code == NEEduHttpCode.UNAUTHORIZED.code) {
                    errorLD.postValue(it.code)
                }
            }
            it
        }
    }

    /**
     * method cannot over load
     */
    private fun findMethod(name: String): Method? {
        return BaseService.methodMap[name]
    }

    fun getMethodObservableType(name: String): Type? {
        val findMethod = findMethod(name) ?: return null
        return BaseService.getMethodObservableType(findMethod)
    }


    fun <T> getDelegateService(zClass: Class<T>): T {
        return when (zClass) {
            AuthService::class.java -> PassthroughAuthService as T
            RoomService::class.java -> PassthroughRoomService as T
            StreamService::class.java -> PassthroughStreamService as T
            UserService::class.java -> PassthroughUserService as T
            else -> null as T
        }
    }
}