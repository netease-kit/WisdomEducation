/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import androidx.lifecycle.LiveData
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData
import com.netease.nimlib.sdk.msg.model.AttachmentProgress
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduCMDBody

/**
 * Messaging methods
 *
 */
abstract class NEEduIMService : INEEduService() {
    /**
     * Send messages
     *
     * @param message The message that is sent
     *
     */
    abstract fun sendMessage(message: ChatRoomMessage): LiveData<NEResult<Void>>

    /**
     * Receive messages
     */
    abstract fun onReceiveMessage(): LiveData<List<ChatRoomMessage>>

    /**
     * Receive custom CMD messages
     */
    abstract fun onReceiveCustomCMDMessage(): LiveData<NEEduCMDBody>

    /**
     * Notification for state changes of image messages
     */
    abstract fun onMessageStatusChange(): LiveData<ChatRoomMessage>

    /**
     * Notification for progress of uploading or downloading message attachments
     */
    abstract fun onAttachmentProgressChange(): LiveData<AttachmentProgress>

    /**
     * Mute all members
     *
     * @param mute Specify whether to mute all members
     */
    internal abstract fun updateMuteAllChat(mute: Boolean)

    /**
     * Notification for mute status changes
     */
    abstract fun onMuteAllChat(): LiveData<Boolean>

    /**
     * Join a chat room
     */
    abstract fun enterChatRoom(data: EnterChatRoomData): LiveData<NEResult<EnterChatRoomResultData>>

    /**
     * Leave a chat room
     */
    abstract fun exitChatRoom(roomId: String?)


    /**
     * Mute all members
     *
     * @param roomUuid
     * @param state
     * @return
     */
    abstract fun muteAllChat(roomUuid: String, state: Int): LiveData<NEResult<Void>>
}

