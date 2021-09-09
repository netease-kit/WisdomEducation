/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.passthrough.model.PassthroughProxyData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.network.RetrofitManager
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHttpCode
import com.netease.yunxin.kit.alog.ALog
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * Created by hzsunyj on 2021/5/26.
 */
internal interface BaseService {

    companion object {
        val gson: Gson = Gson()
        val methodMap = mutableMapOf<String, Method>()
        const val FETCH_SNAPSHOT = "fetchSnapshot"
        const val FETCH_NEXT_SEQUENCES = "fetchNextSequences"
        const val SEND_P2P_MESSAGE = "sendP2PMessage"
        const val UPDATE_ROOM_STATES = "updateRoomStates"
        const val DELETE_ROOM_STATES = "deleteRoomStates"
        const val UPDATE_ROOM_PROPERTIES = "updateRoomProperties"
        const val DELETE_ROOM_PROPERTIES = "deleteRoomProperties"
        const val UPDATE_STREAM_INFO = "updateStreamInfo"
        const val DELETE_STREAM = "deleteStream"
        const val BATCH_STREAMS = "batchStreams"
        const val JOIN_CLASS_ROOM = "joinClassroom"
        const val UPDATE_PROPERTY = "updateProperty"
        const val UPDATE_INFO = "updateInfo"
        lateinit var baseUrl: String

        fun getMethodObservableType(method: Method): Type? {
            val returnType: Type = method.genericReturnType
            return getParameterUpperBound(0, returnType as ParameterizedType)
        }

        private fun getParameterUpperBound(index: Int, type: ParameterizedType): Type? {
            val types = type.actualTypeArguments
            require(!(index < 0 || index >= types.size)) { "Index " + index + " not in range [0," + types.size + ") for " + type }
            val paramType = types[index]
            return if (paramType is WildcardType) {
                paramType.upperBounds[0]
            } else paramType
        }

        /**
         * method cannot over load
         */
        fun findMethod(name: String): Method? {
            return methodMap[name]
        }
    }

    fun <T> getService(zClass: Class<T>): T {
        require(zClass.isInterface) { "API declarations must be interfaces." }
        zClass.declaredMethods.forEach {
            methodMap[it.name] = it
        }
        return RetrofitManager.instance().getService(baseUrl, zClass)
    }

    /**
     * method cannot over load
     */
    fun findMethod(name: String): Method? {
        return BaseService.findMethod(name)
    }

    fun overPassthrough(): Boolean {
        return BaseRepository.passthrough
    }

    /**
     * return type : LiveData<NEResult<T>>, T is data bean type
     *request over by passthrough
     */
    fun <T> executeOverPassthrough(method: Method, vararg args: Any): LiveData<NEResult<T>> {
        val observableType = getMethodObservableType(method)
        val completer: MediatorLiveData<NEResult<T>> = MediatorLiveData()
        val data1 = PassthroughDataFactory().builder(method, args)
        ALog.i("passthrougth--> ${method.name} ${data1.path} ${data1.body}")
        NEEduManagerImpl.imManager.httpProxy(data1).setCallback(object : RequestCallbackWrapper<PassthroughProxyData>() {
            override fun onResult(code: Int, result: PassthroughProxyData?, exception: Throwable?) {
                if (code == ResponseCode.RES_SUCCESS.toInt()) {
                    if (result == null) {
                        completer.postValue(NEResult(NEEduHttpCode.SUCCESS.code))
                    } else {
                        ALog.i("passthrougth <-- ${method.name} data: ${result.body}")
                        val data: NEResult<T> = gson.fromJson(result.body, TypeToken.get(observableType).type)
                        completer.postValue(data)
                    }
                } else {
                    completer.postValue(NEResult(code))
                }
            }
        })
        return completer
    }

}


