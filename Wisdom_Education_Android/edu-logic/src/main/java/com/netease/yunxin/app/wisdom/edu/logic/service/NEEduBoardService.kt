/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.view.WhiteboardView

/**
 * Methods for whiteboard
 *
 */
abstract class NEEduBoardService : INEEduService() {
    /**
     * Grant whiteboard permissions
     *
     * @param userId User ID
     * @param grant Specify whether to grant or revoke permissions
     *
     */
    abstract fun grantPermission(userId: String, grant: Boolean): LiveData<NEResult<Void>>

    /**
     * Instantiate whiteboard
     *
     * @param webView webView control of the whiteboard
     * @param config Whiteboard configuration parameters
     */
    abstract fun initBoard(webView: WhiteboardView, config: WhiteboardConfig)

    /**
     * Specify whether to allow drawing on whiteboard
     *
     * @param enable Specify whether to enable drawing on whiteboard
     */
    abstract fun setEnableDraw(enable: Boolean)

    /**
     * Notification upon whiteboard permissions change
     */
    abstract fun onPermissionGranted(): LiveData<NEEduMember>

    internal abstract fun updatePermission(member: NEEduMember, properties: NEEduMemberProperties)
}
