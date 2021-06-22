/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

class SequenceListRes<T>(
    val total: Int,
    val nextId: Int,
    val list: MutableList<SequenceRes<T>>
)

class SequenceRes<T>(
    val sequence: Int,
    val cmd: Int,
    val version: Int,
    val data: T
)