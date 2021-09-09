/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard.api

import com.netease.yunxin.app.wisdom.whiteboard.model.WhiteboardUser
import com.netease.yunxin.app.wisdom.whiteboard.view.WhiteboardView

/**
 * Created by hzsunyj on 2021/5/21.
 */
abstract class WhiteboardApi {

    /**
     * 获取白板房间通道
     */
    abstract fun getChannelName(): String

    abstract fun getUserInfo(): WhiteboardUser

    abstract fun getAppKey(): String

    abstract fun getUid(): Long

    abstract fun setEnableDraw(enable: Boolean)

    abstract fun isEnableDraw(): Boolean

    abstract fun getOwnerAccount(): String

    abstract fun getWhiteboardView(): WhiteboardView

    abstract fun finish()

    abstract fun getChecksum(): String?

    abstract fun getCurTime(): Long?

    abstract fun getNonce(): String?
}