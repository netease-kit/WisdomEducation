/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.whiteboard.api

import com.netease.yunxin.app.wisdom.record.base.INERecordActor
import com.netease.yunxin.app.wisdom.record.whiteboard.view.NERecordWhiteboardView

/**
 * Created by hzsunyj on 2021/5/21.
 */
abstract class NERecordWhiteboardApi : INERecordActor {
    abstract fun getUrls(): List<String>
    abstract fun getWhiteboardView(): NERecordWhiteboardView
    abstract fun finish()
    abstract fun setViewer(viewer: String)
    abstract fun setTimeRange(startTime: Long?, endTime: Long?)
}