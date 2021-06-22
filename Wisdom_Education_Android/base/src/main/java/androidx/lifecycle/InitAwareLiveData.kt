/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package androidx.lifecycle

import androidx.annotation.Keep
import java.lang.reflect.InvocationTargetException

/**
 *
 */
@Keep
class InitAwareLiveData<T> : MediatorLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            // ignore
            return
        }
        val wrapper = LifecycleBoundObserver(owner, observer)
        wrapper.mLastVersion = version
        val existing: LifecycleBoundObserver? = getExisting(observer, wrapper)
        require(!(existing != null && !existing.isAttachedTo(owner))) { "Cannot add the same observer with different lifecycles" }
        if (existing != null) {
            return
        }
        owner.lifecycle.addObserver(wrapper)
    }

    private fun getExisting(observer: Observer<in T>, wrapper: LifecycleBoundObserver): LifecycleBoundObserver? {
        try {
            val mObservers = LiveData::class.java.getDeclaredField("mObservers")
            mObservers.isAccessible = true
            val o = mObservers[this]
            val putIfAbsent = o.javaClass.getMethod("putIfAbsent", Any::class.java, Any::class.java)
            return putIfAbsent.invoke(o, observer, wrapper) as LiveData<T>.LifecycleBoundObserver?
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return null
    }
}