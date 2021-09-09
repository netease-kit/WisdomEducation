/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

open class NERecordData(
    val eventList: MutableList<NERecordEvent>,
    val record: NERecord,
    val recordItemList: MutableList<NERecordItem>,
)