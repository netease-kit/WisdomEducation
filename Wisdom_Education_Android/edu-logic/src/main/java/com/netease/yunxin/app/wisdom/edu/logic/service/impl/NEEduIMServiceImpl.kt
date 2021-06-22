/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.NEEduErrorCode
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomStates
import com.netease.yunxin.app.wisdom.edu.logic.net.service.RoomServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduIMService

/**
 * Created by hzsunyj on 2021/6/9.
 */
internal class NEEduIMServiceImpl : NEEduIMService() {

    private val receiveMessageLD: MediatorLiveData<List<ChatRoomMessage>> = MediatorLiveData()

    // remember last value , otherwise when network reconnect will notify more
    private var muteAllChat: Boolean? = null

    private val muteAllChatLD: MediatorLiveData<Boolean> = MediatorLiveData()

    override fun sendMessage(chatMessage: ChatRoomMessage): LiveData<NEResult<Void>> {
        val sendLD: MediatorLiveData<NEResult<Void>> = MediatorLiveData()
        NEEduManagerImpl.imManager.chatRoomService.sendMessage(chatMessage, false)
            .setCallback(object : RequestCallbackWrapper<Void>() {
                override fun onResult(code: Int, result: Void?, exception: Throwable?) {
                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
                        updateMessages(listOf(chatMessage))
                        sendLD.postValue(NEResult(NEEduErrorCode.SUCCESS.code))
                    } else {
                        sendLD.postValue(NEResult(code))
                    }
                }
            })
        return sendLD
    }

    override fun onMuteAllChat(): LiveData<Boolean> {
        return muteAllChatLD
    }

    override fun updateMuteAllChat(mute: Boolean) {
        if (mute != muteAllChat) {
            muteAllChat = mute
            muteAllChatLD.value = muteAllChat
        }
    }

    override fun onReceiveMessage(): LiveData<List<ChatRoomMessage>> {
        return receiveMessageLD
    }

    private fun updateMessages(messages: List<ChatRoomMessage>) {
        receiveMessageLD.value = messages
    }

    private var incomingChatRoomMsg: Observer<List<ChatRoomMessage>> = Observer { messages ->
        if (messages == null || messages.isEmpty()) {
            return@Observer
        }
        updateMessages(messages)
    }

    override fun enterChatRoom(data: EnterChatRoomData): LiveData<NEResult<EnterChatRoomResultData>> {
        val enterLD: MediatorLiveData<NEResult<EnterChatRoomResultData>> = MediatorLiveData()
        NEEduManagerImpl.imManager.chatRoomService.enterChatRoomEx(data, 1).setCallback(
            object : RequestCallbackWrapper<EnterChatRoomResultData>() {
                override fun onResult(code: Int, result: EnterChatRoomResultData?, exception: Throwable?) {
                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
                        NEEduManagerImpl.imManager.chatRoomServiceObserver
                            .observeReceiveMessage(incomingChatRoomMsg, true)
                        enterLD.postValue(NEResult(NEEduErrorCode.SUCCESS.code, result))
                    } else {
                        enterLD.postValue(NEResult(code, result))
                    }

                }
            })
        return enterLD
    }

    override fun exitChatRoom(roomId: String?) {
        NEEduManagerImpl.imManager.chatRoomService.exitChatRoom(roomId)
        NEEduManagerImpl.imManager.chatRoomServiceObserver.observeReceiveMessage(incomingChatRoomMsg, false)
    }

    override fun muteAllChat(roomUuid: String, state: Int): LiveData<NEResult<Void>> {
        val commonReq = CommonReq(state)
        return RoomServiceRepository.updateRoomStates(roomId = roomUuid, commonReq = commonReq, key = NEEduRoomStates.STATE_MUTECHAT)
    }
}