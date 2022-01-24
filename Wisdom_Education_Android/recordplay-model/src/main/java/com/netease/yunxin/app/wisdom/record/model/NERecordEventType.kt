/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

import androidx.annotation.IntDef

@IntDef(
    NERecordEventType.MEMBER_JOIN,
    NERecordEventType.MEMBER_LEAVE,
    NERecordEventType.ENABLE_AUDIO,
    NERecordEventType.DISABLE_AUDIO,
    NERecordEventType.ENABLE_VIDEO,
    NERecordEventType.DISABLE_VIDEO,
    NERecordEventType.ENABLE_SUB_VIDEO,
    NERecordEventType.DISABLE_SUB_VIDEO,
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class NERecordEventType {
    companion object {
        /**
         * A members joins the room
         */
        const val MEMBER_JOIN = 1

        /**
         * A member leaves the room
         */
        const val MEMBER_LEAVE = 2

        /**
         * A member enables audio
         */
        const val ENABLE_AUDIO = 3

        /**
         * A member disables audio
         */
        const val DISABLE_AUDIO = 4

        /**
         * A member enables video
         */
        const val ENABLE_VIDEO = 5

        /**
         * A member disables video
         */
        const val DISABLE_VIDEO = 6

        /**
         * A member enables screen share
         */
        const val ENABLE_SUB_VIDEO = 7

        /**
         * A member disables screen share
         */
        const val DISABLE_SUB_VIDEO = 8

        /**
         * Raising hand accepted
         */
        const val HANDS_UP_ACCEPTED = 9

        /**
         * Raising hand rejected
         */
        const val HANDS_UP_NOT_ACCEPTED = 10
    }
}