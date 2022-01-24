/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType
import com.netease.nimlib.sdk.chatroom.model.*
import com.netease.nimlib.sdk.msg.model.AttachmentProgress
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.util.filter
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStreams
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduIMService
import com.netease.yunxin.app.wisdom.edu.ui.clazz.loadmore.LoadMoreConstant
import com.netease.yunxin.app.wisdom.edu.ui.clazz.loadmore.LoadMoreStatus
import com.netease.yunxin.kit.alog.ALog

class ChatRoomViewModel : BaseViewModel() {
    private val tag: String = "ChatRoomViewModel"

    private val imService: NEEduIMService = eduManager.getIMService()

    private var roomInfo: ChatRoomInfo? = null

    private var unreadMsgLD: MediatorLiveData<Int> = MediatorLiveData()

    private var unreadMsgCount: Int = 0

    private var enterTime: Long = 0 // The enterTime of a guest

    /**
     *
     */
    var nextLoadEnable = true

    var loadMoreStatus: LoadMoreStatus = LoadMoreStatus.DEFAULT

    private var onReceiveMessage =
        imService.onReceiveMessage().map { it.filter { t -> t.sessionId == roomInfo?.roomId } }

    private var onMessageStatusChange =
        imService.onMessageStatusChange().filter { t -> t?.sessionId == roomInfo?.roomId }

    fun onMuteAllChat(): LiveData<Boolean> {
        return imService.onMuteAllChat()
    }

    fun onReceiveMessage(): LiveData<List<ChatRoomMessage>> {
        return onReceiveMessage
    }

    fun onMessageStatusChange(): LiveData<ChatRoomMessage> {
        return onMessageStatusChange
    }

    fun onAttachmentProgressChange(): LiveData<AttachmentProgress> {
        return imService.onAttachmentProgressChange()
    }

    fun enterChatRoom(data: EnterChatRoomData): LiveData<NEResult<EnterChatRoomResultData>> {
        return imService.enterChatRoom(data).map {
            if (it.success()) {
                roomInfo = it.data!!.roomInfo
                if (eduManager.isLiveClass()) fetchLiveRoomMembers()
            }
            it
        }
    }

    fun exitChatRoom() {
        imService.exitChatRoom(roomInfo?.roomId)
    }

    fun sendMessage(chatMessage: ChatRoomMessage): LiveData<NEResult<Void>> {
        return imService.sendMessage(chatMessage)
    }

    fun addUnread(msgCount: Int) {
        unreadMsgCount += msgCount
        unreadMsgLD.postValue(unreadMsgCount)
    }

    fun clearUnread() {
        unreadMsgCount = 0
        unreadMsgLD.postValue(unreadMsgCount)
    }

    fun onUnreadChange(): LiveData<Int> {
        return unreadMsgLD
    }

    /**
     * fetch more live room members
     *
     */
    fun fetchLiveRoomMembers() {
        if (!nextLoadEnable) {
            loadMoreStatus = LoadMoreStatus.END
            eduManager.getMemberService().updateMemberJoin(mutableListOf(), true)
            return
        }
        NIMClient.getService(ChatRoomService::class.java)
            .fetchRoomMembers(roomInfo?.roomId, MemberQueryType.GUEST, enterTime, LoadMoreConstant.LOAD_MORE_PAGE)
            .setCallback(object : RequestCallbackWrapper<List<ChatRoomMember>>() {
                override fun onResult(code: Int, result: List<ChatRoomMember>?, exception: Throwable?) {
                    val success = code == ResponseCode.RES_SUCCESS.toInt()
                    if (success) {
                        if (result == null || result.isEmpty()) {
                            nextLoadEnable = false
                        }
                        result!!.filter {
                            eduManager.getMemberService().getMemberList()
                                .firstOrNull { it1 -> it1.isHost() }?.userUuid?.let { it1 ->
                                    it1 != it.account
                                } ?: true
                        }.map {
                            enterTime = it.enterTime
                            NEEduMember(
                                NEEduRoleType.BROADCASTER.value,
                                it.nick,
                                it.account,
                                0L,
                                it.enterTime,
                                NEEduStreams(null, null),
                                null
                            )
                        }.apply {
                            loadMoreStatus = if (size < LoadMoreConstant.LOAD_MORE_PAGE) {
                                LoadMoreStatus.END
                            } else {
                                LoadMoreStatus.LOADING
                            }
                            eduManager.getMemberService().updateMemberJoin(this, true)
                        }
                    } else {
                        loadMoreStatus = LoadMoreStatus.FAIL
                        eduManager.getMemberService().updateMemberJoin(mutableListOf(), true)
                        ALog.e(tag, "fetch members by page failed, code:$code")
                    }
                }
            })
    }


    fun fetchRoomInfo(callback: (param: ChatRoomInfo) -> Unit) {
        roomInfo?.apply {
            NIMClient.getService(ChatRoomService::class.java).fetchRoomInfo(roomId).setCallback(
                object : RequestCallback<ChatRoomInfo> {
                    override fun onSuccess(param: ChatRoomInfo) {
                        callback(param)
                    }

                    override fun onFailed(code: Int) {
                        ALog.d(tag, "fetch room info failed:$code")
                    }

                    override fun onException(exception: Throwable) {
                        ALog.d(tag, "fetch room info exception:$exception")
                    }
                })
        }
    }


}