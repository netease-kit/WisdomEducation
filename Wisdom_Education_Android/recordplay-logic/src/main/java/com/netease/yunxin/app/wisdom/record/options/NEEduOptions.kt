/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.options

import com.netease.yunxin.app.wisdom.record.model.NEEduRecordData

class NERecordOptions(
    val recordData: NEEduRecordData,
    val roomName: String?,
    val roomUuid: String?,
    val teacherName: String?
)