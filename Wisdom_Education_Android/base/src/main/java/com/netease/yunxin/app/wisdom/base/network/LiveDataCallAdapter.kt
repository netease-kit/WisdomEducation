/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.network

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by hzsunyj on 4/20/21.
 */
class LiveDataCallAdapter<T>(private val responseType: Type) : CallAdapter<T, LiveData<T>> {

    companion object {
        val gson = Gson()
    }

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<T>): LiveData<T> {
        return object : LiveData<T>() {

            private val onShot = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (onShot.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<T> {
                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            if (response.isSuccessful) {
                                postValue(response.body())
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        var errorBody = String(response.errorBody()!!.bytes())
                                        if (errorBody != null) {
                                            val data = gson.fromJson(errorBody, TypeToken.get(responseType()).type) as T
                                            postValue(data)
                                        } else {
                                            postValue(NEResult(response.code(), "0", null, 0, null) as T)
                                        }
                                    }
                                } catch (e: Throwable) {
                                    postValue(NEResult(response.code(), "0", null, 0, null) as T)
                                }

                            }
                        }

                        override fun onFailure(call: Call<T>, t: Throwable) {
                            postValue(NEResult(ErrorCode.unknown, "0", t.message, 0, null) as T)
                        }
                    })
                }
            }

            override fun onInactive() {
                super.onInactive()
                if (!call.isExecuted) {
                    call.cancel()
                }
            }
        }
    }

}