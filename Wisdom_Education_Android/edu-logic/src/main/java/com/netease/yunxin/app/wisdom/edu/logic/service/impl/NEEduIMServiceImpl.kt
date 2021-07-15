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
import com.netease.nimlib.sdk.msg.model.AttachmentProgress
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.NEEduErrorCode
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomStates
import com.netease.yunxin.app.wisdom.edu.logic.net.service.RoomServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduIMService
import com.netease.yunxin.app.wisdom.im.IMManager

/**
 * Created by hzsunyj on 2021/6/9.
 */
internal class NEEduIMServiceImpl : NEEduIMService() {

    private val receiveMessageLD: MediatorLiveData<List<ChatRoomMessage>> = MediatorLiveData()
    private val messageStatusChangeLD: MediatorLiveData<ChatRoomMessage> = MediatorLiveData()
    private val attachmentProgressChangeLD: MediatorLiveData<AttachmentProgress> = MediatorLiveData()


    // remember last value , otherwise when network reconnect will notify more
    private var muteAllChat: Boolean? = null

    private val muteAllChatLD: MediatorLiveData<Boolean> = MediatorLiveData()

    var imManager: IMManager = NEEduManagerImpl.imManager

    override fun sendMessage(chatMessage: ChatRoomMessage): LiveData<NEResult<Void>> {
        val sendLD: MediatorLiveData<NEResult<Void>> = MediatorLiveData()
        imManager.chatRoomService.sendMessage(chatMessage, false)
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

    override fun onMessageStatusChange(): LiveData<ChatRoomMessage> {
        return messageStatusChangeLD
    }

    private fun updateMessageStatus(messages: ChatRoomMessage) {
        messageStatusChangeLD.value = messages
    }

    override fun onAttachmentProgressChange(): LiveData<AttachmentProgress> {
        return attachmentProgressChangeLD
    }

    private fun updateAttachmentProgress(progress: AttachmentProgress) {
        attachmentProgressChangeLD.value = progress
    }

    private var incomingChatRoomMsg: Observer<List<ChatRoomMessage>> = Observer { messages ->
        if (messages == null || messages.isEmpty()) {
            return@Observer
        }
        updateMessages(messages)
    }

    /**
     * 消息状态变化观察者
     */
    private val messageStatusObserver =
        Observer<ChatRoomMessage> { message -> updateMessageStatus(message) }

    /**
     * 消息附件上传/下载进度观察者
     */
    private val attachmentProgressObserver =
        Observer<AttachmentProgress> { progress -> updateAttachmentProgress(progress) }

    override fun enterChatRoom(data: EnterChatRoomData): LiveData<NEResult<EnterChatRoomResultData>> {
        val enterLD: MediatorLiveData<NEResult<EnterChatRoomResultData>> = MediatorLiveData()
        imManager.chatRoomService.enterChatRoomEx(data, 1).setCallback(
            object : RequestCallbackWrapper<EnterChatRoomResultData>() {
                override fun onResult(code: Int, result: EnterChatRoomResultData?, exception: Throwable?) {
                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
                        imManager.chatRoomServiceObserver
                            .observeReceiveMessage(incomingChatRoomMsg, true)
                        imManager.chatRoomServiceObserver.observeMsgStatus(messageStatusObserver, true)
                        imManager.chatRoomServiceObserver.observeAttachmentProgress(
                            attachmentProgressObserver,
                            true
                        )
                        enterLD.postValue(NEResult(NEEduErrorCode.SUCCESS.code, result))
                    } else {
                        enterLD.postValue(NEResult(code, result))
                    }

                }
            })
        return enterLD
    }

    override fun exitChatRoom(roomId: String?) {
        imManager.chatRoomService.exitChatRoom(roomId)
        imManager.chatRoomServiceObserver.observeReceiveMessage(incomingChatRoomMsg, false)
        imManager.chatRoomServiceObserver.observeMsgStatus(messageStatusObserver, false)
        imManager.chatRoomServiceObserver.observeAttachmentProgress(attachmentProgressObserver, false)
    }

    override fun muteAllChat(roomUuid: String, state: Int): LiveData<NEResult<Void>> {
        val commonReq = CommonReq(state)
        return RoomServiceRepository.updateRoomStates(
            roomId = roomUuid,
            commonReq = commonReq,
            key = NEEduRoomStates.STATE_MUTECHAT
        )
    }
}