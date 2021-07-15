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
 * 提供可供 App 调用的屏幕共享相关方法
 *
 */
abstract class NEEduShareScreenService : INEEduService() {
    /**
     * 屏幕共享授权或取消授权
     *
     * @param userId 用户id
     * @param grant 是否授予/取消权限
     *
     */
    abstract fun grantPermission(userId: String, grant: Boolean): LiveData<NEResult<Void>>

    /**
     * 发送屏幕共享，不主动打开截屏
     *
     */
    abstract fun shareScreen(
        roomUuid: String,
        userUuid: String,
    ): LiveData<NEResult<NEEduState>>

    /**
     * 结束屏幕共享，不主动关闭截屏
     */
    abstract fun finishShareScreen(
        roomUuid: String,
        userUuid: String,
    ): LiveData<NEResult<Void>>

    /**
     * 开始屏幕共享
     */
    abstract fun startScreenCapture(config: NERtcScreenConfig, intent: Intent, callback: MediaProjection.Callback): Int

    /**
     * 停止屏幕共享
     */
    abstract fun stopScreenCapture()

    /**
     * 屏幕共享权限发生变化
     */
    abstract fun onPermissionGranted(): LiveData<NEEduMember>

    /**
     * 屏幕共享状态变更
     */
    abstract fun onScreenShareChange(): LiveData<List<NEEduMember>>


    /**
     * 对应的成员流的变更
     */
    internal abstract fun updateStreamChange(member: NEEduMember)

    /**
     * 屏幕共享状态变更
     */
    internal abstract fun updateStreamRemove(member: NEEduMember)


    /**
     * 成员加入流变更
     */
    internal abstract fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean)

    /**
     * 成员离开流变更
     */
    internal abstract fun updateMemberLeave(list: MutableList<NEEduMember>)

    internal abstract fun updatePermission(member: NEEduMember, propertiesDiff: NEEduMemberProperties)
}
