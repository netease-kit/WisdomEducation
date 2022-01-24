/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * 
 */
open class NEEduState(var value: Int? = NEEduStateValue.CLOSE, var time: Long? = 0) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is NEEduState) {
            return false
        }
        return (other.value == this.value)
    }

    override fun hashCode(): Int {
        return value ?: 0
    }
}

object NEEduStateValue {
    const val CLOSE = 0
    const val OPEN = 1
}
