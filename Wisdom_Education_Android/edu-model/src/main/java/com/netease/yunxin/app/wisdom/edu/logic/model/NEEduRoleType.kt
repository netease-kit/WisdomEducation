/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Role type: host: teacher, broadcaster: student, audience: viewer in large classes
 */
enum class NEEduRoleType(var value: String) {
    HOST("host"),
    BROADCASTER("broadcaster"),
    AUDIENCE("audience")
}