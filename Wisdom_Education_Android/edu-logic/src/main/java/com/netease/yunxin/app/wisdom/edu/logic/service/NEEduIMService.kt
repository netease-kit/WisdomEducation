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

/**
 * 提供可供 App 调用的聊天消息，透传相关方法
 *
 */
abstract class NEEduIMService : INEEduService() {
    /**
     * 发送消息
     *
     * @param message 消息内容
     *
     */
    abstract fun sendMessage(message: ChatRoomMessage): LiveData<NEResult<Void>>

    /**
     * 收到聊天消息
     */
    abstract fun onReceiveMessage(): LiveData<List<ChatRoomMessage>>

    /**
     * 图片消息状态变化通知
     */
    abstract fun onMessageStatusChange(): LiveData<ChatRoomMessage>

    /**
     * 消息附件上传/下载进度通知
     */
    abstract fun onAttachmentProgressChange(): LiveData<AttachmentProgress>

    /**
     * 全体聊天禁言
     *
     * @param mute 是否禁言全体聊天
     */
    internal abstract fun updateMuteAllChat(mute: Boolean)

    /**
     * 聊天禁言状态发生变化通知
     */
    abstract fun onMuteAllChat(): LiveData<Boolean>

    /**
     * 加入聊天室
     */
    abstract fun enterChatRoom(data: EnterChatRoomData): LiveData<NEResult<EnterChatRoomResultData>>

    /**
     * 退出聊天室
     */
    abstract fun exitChatRoom(roomId: String?)


    /**
     * 全体禁言
     *
     * @param roomUuid
     * @param state
     * @return
     */
    abstract fun muteAllChat(roomUuid: String, state: Int): LiveData<NEResult<Void>>
}

