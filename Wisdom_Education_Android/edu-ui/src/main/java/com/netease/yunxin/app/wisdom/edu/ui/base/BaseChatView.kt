/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.base

import android.view.View
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.ItemBottomView

interface BaseChatView {
    fun getIMLayout(): View

    fun getChatRoomView(): ItemBottomView

    fun showFragmentWithChatRoom()

    fun hideFragmentWithChatRoom()

    fun getChatroomFragment(): BaseFragment?

    fun getZoomImageLayout(): View

    fun showZoomImageFragment(message: ChatRoomMessage)

    fun hideZoomImageFragment()

    fun updateUnReadCount() {}
}