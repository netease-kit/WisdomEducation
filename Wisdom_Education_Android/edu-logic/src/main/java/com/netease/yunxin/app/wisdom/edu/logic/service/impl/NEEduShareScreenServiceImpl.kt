/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import android.content.Intent
import android.media.projection.MediaProjection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.lava.nertc.sdk.video.NERtcScreenConfig
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.logic.net.service.StreamServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.UserServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduMemberPropertiesType
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduUpdateMemberPropertyReq
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduShareScreenService

/**
 * 
 */
internal class NEEduShareScreenServiceImpl : NEEduShareScreenService() {

    private val screenShareLD: MediatorLiveData<List<NEEduMember>> = MediatorLiveData()

    private val permissionLD: MediatorLiveData<NEEduMember> = MediatorLiveData()

    /**
     * future may be multi member same time screen share
     */
    private val screenShareList: MutableList<NEEduMember> = mutableListOf()

    private var sharePermission: Int? = null

    override fun grantPermission(userId: String, grant: Boolean): LiveData<NEResult<Void>> {
        val req = NEEduUpdateMemberPropertyReq(value = if (grant) NEEduStateValue.OPEN else NEEduStateValue.CLOSE)
        return UserServiceRepository.updateProperty(
            NEEduManagerImpl.getRoom().roomUuid,
            userId,
            NEEduMemberPropertiesType.SCREENSHARE.type,
            req
        )
    }

    override fun shareScreen(
        roomUuid: String,
        userUuid: String,
    ): LiveData<NEResult<NEEduState>> {
        return StreamServiceRepository.updateStreamInfo(roomUuid,
            userUuid,
            NEEduStreamType.SUB_VIDEO.type,
            CommonReq(NEEduStateValue.OPEN))
    }

    override fun startScreenCapture(
        config: NERtcScreenConfig,
        intent: Intent,
        callback: MediaProjection.Callback,
    ): Int {
        return NEEduManagerImpl.rtcManager.startScreenCapture(config, intent, callback)
    }

    override fun finishShareScreen(
        roomUuid: String,
        userUuid: String,
    ): LiveData<NEResult<Void>> {
        return StreamServiceRepository.deleteStream(roomUuid, userUuid, NEEduStreamType.SUB_VIDEO.type)
    }

    override fun stopScreenCapture() {
        return NEEduManagerImpl.rtcManager.stopScreenCapture()
    }

    override fun updatePermission(member: NEEduMember, propertiesDiff: NEEduMemberProperties) {
        propertiesDiff.screenShare?.let {
            permissionLD.postValue(member)
        }
    }

    override fun onPermissionGranted(): LiveData<NEEduMember> {
        return permissionLD
    }

    override fun onScreenShareChange(): LiveData<List<NEEduMember>> {
        return screenShareLD
    }

    override fun updateStreamChange(member: NEEduMember) {
        // adjust screen share list
        if (member.hasSubVideo() && !screenShareList.contains(member)) {
            screenShareList.add(member)
            screenShareLD.postValue(screenShareList)
        } else if (!member.hasSubVideo() && screenShareList.contains(member)) {
            screenShareList.remove(member)
            screenShareLD.postValue(screenShareList)
        }
    }

    override fun updateStreamRemove(member: NEEduMember) {
        // adjust screen share list
        if (!member.hasSubVideo() && screenShareList.contains(member)) {
            screenShareList.remove(member)
            screenShareLD.postValue(screenShareList)
        }
    }

    override fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean) {
        var change = false
        if (increment) {
            list.forEach {
                if (it.hasSubVideo()) {
                    val indexOf = screenShareList.indexOf(it)
                    if (indexOf != -1) {
                        screenShareList[indexOf] = it
                    } else {
                        screenShareList.add(it)
                    }
                    change = true
                } else {
                    val indexOf = screenShareList.indexOf(it)
                    if (indexOf != -1) {
                        screenShareList.remove(it)
                        change = true
                    }
                }
            }
        } else {
            change = true
            screenShareList.clear()// screen share rebuild
            // may be jude tempList equals screenShareList
            screenShareList.addAll(list.filter { it.hasSubVideo() })
        }

        if (change) {
            screenShareLD.postValue(screenShareList)
        }
    }

    override fun updateMemberLeave(list: MutableList<NEEduMember>) {
        // adjust screen share list
        screenShareList.removeAll(list).also {
            if (it) {
                screenShareLD.postValue(screenShareList)
            }
        }
    }

}