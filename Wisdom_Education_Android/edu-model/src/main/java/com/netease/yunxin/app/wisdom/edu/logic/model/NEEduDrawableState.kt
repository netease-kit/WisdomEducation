/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

open class NEEduDrawableState(var drawable: Int? = NEEduStateValue.CLOSE, var time: Long? = 0) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is NEEduDrawableState) {
            return false
        }
        return (other.drawable == this.drawable)
    }

    override fun hashCode(): Int {
        return drawable ?: 0
    }
}
