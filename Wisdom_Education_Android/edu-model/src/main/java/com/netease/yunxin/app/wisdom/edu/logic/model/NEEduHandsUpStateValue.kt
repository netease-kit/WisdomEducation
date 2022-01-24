/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

object NEEduHandsUpStateValue {
    /**Instantiating, students do not raise hands**/
    const val IDLE = 0

    /**Students raise hands**/
    const val APPLY = 1

    /**The teacher accept the request to speak**/
    const val TEACHER_ACCEPT = 2

    /**The teacher rejects the request**/
    const val TEACHER_REJECT = 3

    /**Students cancel the request**/
    const val STUDENT_CANCEL = 4

    /**The teacher forcibly mute the student**/
    const val TEACHER_OFF_STAGE = 5
}