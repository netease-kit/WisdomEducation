/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * 
 */

private val handler: Handler = Handler(Looper.getMainLooper())

fun <T> LiveData<T>.observeForeverOnce(observer: Observer<T>) {
    runOnMainThread {
        observeForever(object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    runOnMainThread {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

}

fun runOnMainThread(runnable: Runnable) {
    if (isMainThread()) {
        runnable.run()
    } else {
        handler.post(runnable)
    }
}

fun isMainThread(): Boolean {
    return Looper.getMainLooper().thread == Thread.currentThread()
}

inline fun <T> LiveData<T>.filter(crossinline filter: (T?) -> Boolean): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@filter) {
            if (filter(it)) {
                this.value = it
            }
        }
    }
}
