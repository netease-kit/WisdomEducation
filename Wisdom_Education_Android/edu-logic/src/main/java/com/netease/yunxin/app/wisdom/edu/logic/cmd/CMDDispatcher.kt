/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import androidx.lifecycle.Observer
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduCMDBody
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStateValue
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStreamType
import com.netease.yunxin.kit.alog.ALog

/**
 * 
 */
internal class CMDDispatcher(private val neEduManager: NEEduManagerImpl) {

    private val tag: String = "CMDDispatcher"

    private val observer: Observer<String> = Observer<String> {
        it?.let { dispatch(it) }
    }

    private val observerCMDBody: Observer<NEEduCMDBody> = Observer<NEEduCMDBody> {
        it?.let { dispatch(it) }
    }

    fun start() {
        if (neEduManager.isLiveClass()) {
            NEEduManagerImpl.getIMService().onReceiveCustomCMDMessage().observeForever(observerCMDBody)
        } else
            NEEduManagerImpl.imManager.passthroughLD.observeForever(observer)
    }

    fun destroy() {
        if (neEduManager.isLiveClass()) {
            NEEduManagerImpl.getIMService().onReceiveCustomCMDMessage().removeObserver(observerCMDBody)
        } else
            NEEduManagerImpl.imManager.passthroughLD.removeObserver(observer)
    }

    private fun dispatch(text: String) {
        ALog.i(tag, text)
        val cmdBody = CMDActionFactory.parse(text)
        cmdBody?.let {
            NEEduManagerImpl.neEduSync?.handle(cmdBody).let {
                if (it == true) {
                    dispatchCMD(cmdBody)
                }
            }

        }
    }

    private fun dispatch(cmdBody: NEEduCMDBody) {
        ALog.i(tag, cmdBody.toString())
        cmdBody?.takeIf { cmdBody.cmd == CMDId.ROOM_STATES_CHANGE || cmdBody.cmd == CMDId.ROOM_STATES_DELETE
                || cmdBody.cmd == CMDId.USER_JOIN || cmdBody.cmd == CMDId.USER_LEAVE }?.let {
            NEEduManagerImpl.neEduSync?.handle(cmdBody).let {
                if (it == true) {
                    dispatchCMD(cmdBody)
                }
            }
        }
    }

    fun dispatchCMD(cmdBody: NEEduCMDBody) {
        val realAction: CMDAction? = CMDActionFactory.getRealAction(cmdBody)
        realAction?.let {
            when (realAction) {
                is RoomStateChangeAction -> onRoomStateChange(realAction)
                is RoomMemberJoinAction -> onMemberJoin(realAction)
                is RoomMemberLeaveAction -> onMemberLeave(realAction)
                is StreamChangeAction -> onStreamChange(realAction)
                is StreamRemoveAction -> onStreamRemove(realAction)
                is RoomMemberPropertiesChangeAction -> onRoomMemberPropertiesChange(realAction)
                is RoomPropertiesChangeAction -> onRoomPropertiesChange(realAction)
            }
        }
    }

    private fun onRoomPropertiesChange(action: RoomPropertiesChangeAction) {
    }

    private fun onRoomStateChange(action: RoomStateChangeAction) {
        neEduManager.getRoomService().updateRoomStatesChange(action.states, true)
        action.states.muteChat?.let {
            neEduManager.getIMService().updateMuteAllChat(action.states.muteChat?.value == NEEduStateValue.OPEN)
        }
        action.states.muteAudio?.let {
            neEduManager.getRtcService().updateMuteAllAudio(action.states.muteAudio!!)
        }
    }

    private fun onMemberJoin(action: RoomMemberJoinAction) {
        neEduManager.getRtcService().updateMemberJoin(action.members, true)
        neEduManager.getMemberService().updateMemberJoin(action.members, true)
        neEduManager.getShareScreenService().updateMemberJoin(action.members, true)
        neEduManager.getHandsUpService().updateMemberJoin(action.members, true)
    }

    private fun onMemberLeave(action: RoomMemberLeaveAction) {
        neEduManager.getRtcService().updateMemberLeave(action.members)
        neEduManager.getMemberService().updateMemberLeave(action.members)
        neEduManager.getShareScreenService().updateMemberLeave(action.members)
        neEduManager.getHandsUpService().updateMemberLeave(action.members)
    }

    private fun onStreamChange(action: StreamChangeAction) {
        val member = neEduManager.getMemberService().updateStreamChange(action.member, action.streams)
        member?.let {
            neEduManager.getRtcService().updateStreamChange(it, action.streams.video != null)
            neEduManager.getShareScreenService().updateStreamChange(it)
        }
    }

    private fun onStreamRemove(action: StreamRemoveAction) {
        val member = neEduManager.getMemberService().updateStreamRemove(action.member, action.streamType)
        member?.let {
            neEduManager.getRtcService().updateStreamRemove(member, action.streamType == NEEduStreamType.VIDEO.type)
            neEduManager.getShareScreenService().updateStreamRemove(it)
        }
    }

    private fun onRoomMemberPropertiesChange(action: RoomMemberPropertiesChangeAction) {
        val member = neEduManager.getMemberService().updateMemberPropertiesCache(action.member, action.properties)
        member?.let {
            neEduManager.getMemberService().updateMemberPropertiesChange(member, action.properties)
            action.properties.avHandsUp?.let {
                if (!member.isOnStage()) {
                    neEduManager.getRtcService().updateMemberOffStageStreamChange(member)
                }
                neEduManager.getHandsUpService().updateHandsUpState(member)
            }
            neEduManager.getBoardService().updatePermission(member, action.properties)
            neEduManager.getShareScreenService().updatePermission(member, action.properties)
        }
    }

}