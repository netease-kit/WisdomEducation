/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

import com.netease.yunxin.app.wisdom.edu.logic.model.NESnapshot

data class NESnapshotDto(
    val sequence: Int,
    val snapshot: NESnapshot
)