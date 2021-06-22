/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStateValue
import com.netease.yunxin.app.wisdom.edu.logic.net.service.UserServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduMemberPropertiesType
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduUpdateMemberPropertyReq
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduBoardService
import com.netease.yunxin.app.wisdom.whiteboard.WhiteboardManager
import com.netease.yunxin.app.wisdom.whiteboard.config.WhiteboardConfig
import com.netease.yunxin.app.wisdom.whiteboard.view.WhiteboardView

/**
 * Created by hzsunyj on 2021/5/21.
 */
internal class NEEduBoardServiceImpl : NEEduBoardService() {

    private val whiteboardManager: WhiteboardManager = WhiteboardManager

    private val permissionLD: MediatorLiveData<NEEduMember> = MediatorLiveData()

    private var boardPermission: Int? = null

    override fun grantPermission(userId: String, grant: Boolean): LiveData<NEResult<Void>> {
        val req = NEEduUpdateMemberPropertyReq(drawable = if (grant) NEEduStateValue.OPEN else NEEduStateValue.CLOSE)
        return UserServiceRepository.updateProperty(
            NEEduManagerImpl.eduEntryRes.room.roomUuid,
            userId,
            NEEduMemberPropertiesType.WHITEBOARD.type,
            req
        )
    }

    override fun initBoard(context: Context, webView: WhiteboardView, config: WhiteboardConfig) {
        whiteboardManager.init(context, webView, config)
    }

    override fun setEnableDraw(enable: Boolean) {
        whiteboardManager.setEnableDraw(enable)
    }

    override fun updateSelfPermission(member: NEEduMember) {
        if (NEEduManagerImpl.isSelf(member.userUuid) && !member.isHost()) {
            member.properties?.whiteboard?.let {
                if (it.drawable != boardPermission) {
                    boardPermission = it.drawable
                    permissionLD.postValue(member)
                }
            }
        }
    }

    override fun onSelfPermissionGranted(): LiveData<NEEduMember> {
        return permissionLD
    }
}