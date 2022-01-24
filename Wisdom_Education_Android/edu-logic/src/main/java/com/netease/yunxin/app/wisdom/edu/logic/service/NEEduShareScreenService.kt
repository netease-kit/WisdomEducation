/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import android.content.Intent
import android.media.projection.MediaProjection
import androidx.lifecycle.LiveData
import com.netease.lava.nertc.sdk.video.NERtcScreenConfig
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduState

/**
 * Methods for screen share
 *
 */
abstract class NEEduShareScreenService : INEEduService() {
    /**
     * Grant or revoke permissions of screen share
     *
     * @param userId User ID
     * @param grant Specify whether to grant or revoke the permissions
     *
     */
    abstract fun grantPermission(userId: String, grant: Boolean): LiveData<NEResult<Void>>

    /**
     * Share the screen and do not enable screenshot
     *
     */
    abstract fun shareScreen(
        roomUuid: String,
        userUuid: String,
    ): LiveData<NEResult<NEEduState>>

    /**
     * Stop sharing the screen and do not disable screenshot
     */
    abstract fun finishShareScreen(
        roomUuid: String,
        userUuid: String,
    ): LiveData<NEResult<Void>>

    /**
     * Start sharing the screen
     */
    abstract fun startScreenCapture(config: NERtcScreenConfig, intent: Intent, callback: MediaProjection.Callback): Int

    /**
     * Stop sharing the screen
     */
    abstract fun stopScreenCapture()

    /**
     * The permissions of screen share change
     */
    abstract fun onPermissionGranted(): LiveData<NEEduMember>

    /**
     * The state of screen share changes
     */
    abstract fun onScreenShareChange(): LiveData<List<NEEduMember>>


    /**
     * The stream of a specified member changes
     */
    internal abstract fun updateStreamChange(member: NEEduMember)

    /**
     * The state of screen share changes
     */
    internal abstract fun updateStreamRemove(member: NEEduMember)


    /**
     * The stream changes when a member joins the room
     */
    internal abstract fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean)

    /**
     * The stream changes if a member leave the room
     */
    internal abstract fun updateMemberLeave(list: MutableList<NEEduMember>)

    internal abstract fun updatePermission(member: NEEduMember, propertiesDiff: NEEduMemberProperties)
}
