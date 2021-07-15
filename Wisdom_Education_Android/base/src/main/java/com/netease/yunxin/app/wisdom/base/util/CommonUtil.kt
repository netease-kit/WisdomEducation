/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.base.util

import android.view.View
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object CommonUtil {
    /**
     * Emits only the first event during sequential time windows of a specified duration
     * @param duration duration time
     * @param combine 一个接口中的所有回调方法是否共用防抖时间
     */
    fun <T> T.throttleFirst(duration: Long = 500L, combine: Boolean = false, defaultReturnValue: Any? = null): T {
        return Proxy.newProxyInstance(this!!::class.java.classLoader, this!!::class.java.interfaces,
            object : InvocationHandler {
                private val map = HashMap<Method?, Long>()
                override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
                    val current = System.currentTimeMillis()
                    val key = if (combine) null else method
                    return if (current - (map[key] ?: 0) > duration) {
                        map[key] = current
                        method.invoke(this@throttleFirst, *args.orEmpty())
                    } else {
                        resolveReturnIfThrottle(method, defaultReturnValue)
                    }
                }
            }
        ) as T
    }

    private fun resolveReturnIfThrottle(method: Method, defaultReturnValue: Any?): Any? {
        return when (method.returnType.name) {
            Void::class.java.name, Void.TYPE.name -> null
            else -> defaultReturnValue
                ?: (throw IllegalArgumentException("Cannot properly throttle a callback that does not return a null value or lacks a default value."))
        }
    }

    fun View.setOnClickThrottleFirst(listener: View.OnClickListener) {
        setOnClickListener(listener.throttleFirst())
    }
}