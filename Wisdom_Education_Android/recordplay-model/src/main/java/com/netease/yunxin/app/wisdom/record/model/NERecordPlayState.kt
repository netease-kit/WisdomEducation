/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

import androidx.annotation.IntDef

@IntDef(
    NERecordPlayState.IDLE,
    NERecordPlayState.PREPARING,
    NERecordPlayState.PREPARED,
    NERecordPlayState.PLAYING,
    NERecordPlayState.PAUSED,
    NERecordPlayState.ERROR,
    NERecordPlayState.STOPPED,
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class NERecordPlayState {
    companion object {
        /**
         * 初始状态
         */
        const val IDLE = 0

        /**
         * 准备中
         */
        const val PREPARING = 1

        /**
         * 准备完成
         */
        const val PREPARED = 2

        /**
         * 开始播放
         */
        const val PLAYING = 3

        /**
         * 暂停
         */
        const val PAUSED = 4

        /**
         * 出错
         */
        const val ERROR = 5

        /**
         * 停止（已销毁）本质上与IDLE状态相同
         */
        const val STOPPED = 6

    }
}