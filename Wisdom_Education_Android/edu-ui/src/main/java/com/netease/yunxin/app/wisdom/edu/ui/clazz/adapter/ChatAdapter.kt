/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.yunxin.app.wisdom.base.util.TimeUtil
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ItemMsgLeftBinding
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ItemMsgRightBinding
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ItemMsgTipsBinding

/**
 * Created by hzsunyj on 2021/6/4.
 */
class ChatAdapter(
    val activity: BaseClassActivity,
    dataList: MutableList<ChatRoomMessage>,
    listener: OnItemClickListener<ChatRoomMessage>? = null,
) : BaseAdapter<ChatRoomMessage>(dataList, listener) {

    companion object {
        const val tips: Int = 0
        const val left: Int = 1
        const val right: Int = 2
    }

    init {
        setDelegate(object : BaseDelegate<ChatRoomMessage>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*>? {
                return when (viewType) {
                    tips -> TipsViewHolder(ItemMsgTipsBinding.inflate(LayoutInflater.from(activity), parent, false))
                    left -> LeftViewHolder(ItemMsgLeftBinding.inflate(LayoutInflater.from(activity), parent, false))
                    right -> RightViewHolder(ItemMsgRightBinding.inflate(LayoutInflater.from(activity), parent, false))
                    else -> null
                }
            }

            override fun getItemViewType(data: ChatRoomMessage, pos: Int): Int {
                return when (data.msgType) {
                    MsgTypeEnum.tip -> tips
                    else -> if (data.direct == MsgDirectionEnum.In) left else right
                }
            }

        })
    }

    inner class TipsViewHolder(val binding: ItemMsgTipsBinding) : BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text = TimeUtil.getNowDatetime(item.time)
        }
    }


    inner class LeftViewHolder(val binding: ItemMsgLeftBinding) : BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text =
                if (TextUtils.isEmpty(item.fromNick)) item.chatRoomMessageExtension.senderNick else item
                    .fromNick
            binding.tvContent.text = item.content
        }
    }

    inner class RightViewHolder(val binding: ItemMsgRightBinding) : BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text = (activity.eduManager.getEntryMember().userName)
            binding.tvContent.text = item.content
        }
    }
}

