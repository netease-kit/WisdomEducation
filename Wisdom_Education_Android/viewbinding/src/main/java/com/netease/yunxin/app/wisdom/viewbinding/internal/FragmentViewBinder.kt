/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.viewbinding.internal

import android.view.View
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

@RestrictTo(RestrictTo.Scope.LIBRARY)
@PublishedApi
internal class FragmentViewBinder<T : ViewBinding>(private val viewBindingClass: Class<T>) {

    /**
     * Cache static method `ViewBinding.bind(View)`
     */
    private val bindViewMethod by lazy(LazyThreadSafetyMode.NONE) {
        viewBindingClass.getMethod("bind", View::class.java)
    }

    /**
     * Create new [ViewBinding] instance
     */
    @Suppress("UNCHECKED_CAST")
    fun bind(fragment: Fragment): T {
        return bindViewMethod(null, fragment.requireView()) as T
    }
}
