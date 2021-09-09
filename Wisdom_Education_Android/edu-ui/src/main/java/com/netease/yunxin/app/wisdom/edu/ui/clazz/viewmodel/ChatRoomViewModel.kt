/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData
import com.netease.nimlib.sdk.msg.model.AttachmentProgress
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.util.filter
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduIMService

class ChatRoomViewModel : BaseViewModel() {

    private val imService: NEEduIMService = eduManager.getIMService()

    private var roomInfo: ChatRoomInfo? = null

    private var unreadMsgLD: MediatorLiveData<Int> = MediatorLiveData()

    private var unreadMsgCount: Int = 0

    private var onReceiveMessage = imService.onReceiveMessage().map { it.filter { t -> t.sessionId == roomInfo?.roomId } }

    private var onMessageStatusChange = imService.onMessageStatusChange().filter { t -> t?.sessionId == roomInfo?.roomId }

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
            if (it.success()) roomInfo = it.data!!.roomInfo
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
}