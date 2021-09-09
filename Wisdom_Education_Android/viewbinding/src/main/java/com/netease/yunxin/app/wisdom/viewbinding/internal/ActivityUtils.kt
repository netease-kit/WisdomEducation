/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.viewbinding.internal

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityCompat

@RestrictTo(RestrictTo.Scope.LIBRARY)
@PublishedApi
internal fun <V : View> Activity.requireViewByIdCompat(@IdRes viewId: Int): V {
    return ActivityCompat.requireViewById(this, viewId)
}
