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
         * The initial state
         */
        const val IDLE = 0

        /**
         * Preparing
         */
        const val PREPARING = 1

        /**
         * Prepared
         */
        const val PREPARED = 2

        /**
         * Playing
         */
        const val PLAYING = 3

        /**
         * Paused
         */
        const val PAUSED = 4

        /**
         * An error occurred
         */
        const val ERROR = 5

        /**
         * The Stopped state (destroyed) is the same as the IDLE state
         */
        const val STOPPED = 6

    }
}