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
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties
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

    override fun grantPermission(userId: String, grant: Boolean): LiveData<NEResult<Void>> {
        val req = NEEduUpdateMemberPropertyReq(drawable = if (grant) NEEduStateValue.OPEN else NEEduStateValue.CLOSE)
        return UserServiceRepository.updateProperty(
            NEEduManagerImpl.eduEntryRes.room.roomUuid,
            userId,
            NEEduMemberPropertiesType.WHITEBOARD.type,
            req
        )
    }

    override fun initBoard(webView: WhiteboardView, config: WhiteboardConfig) {
        whiteboardManager.init(webView, config)
    }

    override fun setEnableDraw(enable: Boolean) {
        whiteboardManager.setEnableDraw(enable)
    }

    override fun updatePermission(member: NEEduMember, properties: NEEduMemberProperties) {
        properties.whiteboard?.let {
            permissionLD.postValue(member)
        }
    }

    override fun onPermissionGranted(): LiveData<NEEduMember> {
        return permissionLD
    }
}