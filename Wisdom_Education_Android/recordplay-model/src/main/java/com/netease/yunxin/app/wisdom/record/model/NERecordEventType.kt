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
         * 成员进入房间
         */
        const val MEMBER_JOIN = 1

        /**
         * 成员离开房间
         */
        const val MEMBER_LEAVE = 2

        /**
         * 成员打开音频
         */
        const val ENABLE_AUDIO = 3

        /**
         * 成员关闭音频
         */
        const val DISABLE_AUDIO = 4

        /**
         * 成员打开视频
         */
        const val ENABLE_VIDEO = 5

        /**
         * 成员关闭视频
         */
        const val DISABLE_VIDEO = 6

        /**
         * 成员打开屏幕共享
         */
        const val ENABLE_SUB_VIDEO = 7

        /**
         * 成员关闭屏幕共享
         */
        const val DISABLE_SUB_VIDEO = 8

        /**
         * 上台
         */
        const val HANDS_UP_ACCEPTED = 9

        /**
         * 下台
         */
        const val HANDS_UP_NOT_ACCEPTED = 10
    }
}