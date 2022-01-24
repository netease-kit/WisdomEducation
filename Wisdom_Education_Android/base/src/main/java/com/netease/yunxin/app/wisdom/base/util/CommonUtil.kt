/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.base.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object CommonUtil {
    /**
     * Emits only the first event during sequential time windows of a specified duration
     * @param duration duration time
     * @param combine Specify whether all callback methods for an API share the anti-jitter time
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

    fun <T> compareList(list1: List<T>, list2: List<T>): Boolean {
        return list1.size == list2.size && list1.containsAll(list2)
                && list2.containsAll(list1)
    }

    fun hideKeyBoard(activity: Activity, view: View) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}