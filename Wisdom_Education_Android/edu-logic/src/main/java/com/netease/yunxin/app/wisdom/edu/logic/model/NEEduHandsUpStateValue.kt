/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

object NEEduHandsUpStateValue {
    /**初始化 学生主动下台 未举手**/
    const val IDLE = 0

    /**学生举手**/
    const val APPLY = 1

    /**老师通过 在台上 请他上台**/
    const val TEACHER_ACCEPT = 2

    /**老师拒绝**/
    const val TEACHER_REJECT = 3

    /**学生取消**/
    const val STUDENT_CANCEL = 4

    /**老师强制关闭**/
    const val TEACHER_OFF_STAGE = 5
}