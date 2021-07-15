/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import android.content.Context
import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.view.WhiteboardView

/**
 * 提供可供 App 调用的白板相关方法
 *
 */
abstract class NEEduBoardService : INEEduService() {
    /**
     * 白板授权
     *
     * @param userId 用户id
     * @param grant 是否授予/取消权限
     *
     */
    abstract fun grantPermission(userId: String, grant: Boolean): LiveData<NEResult<Void>>

    /**
     * 初始化白板
     *
     * @param webView 白板webView控件
     * @param config 白板配置参数
     */
    abstract fun initBoard(webView: WhiteboardView, config: WhiteboardConfig)

    /**
     * 设置是否允许绘制
     *
     * @param enable 是否允许绘制
     */
    abstract fun setEnableDraw(enable: Boolean)

    /**
     * 白板权限变化通知
     */
    abstract fun onPermissionGranted(): LiveData<NEEduMember>

    internal abstract fun updatePermission(member: NEEduMember, properties: NEEduMemberProperties)
}
