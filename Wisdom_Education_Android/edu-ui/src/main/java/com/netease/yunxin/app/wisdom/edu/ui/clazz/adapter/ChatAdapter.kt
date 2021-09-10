/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.yunxin.app.wisdom.base.util.TimeUtil
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewholder.MsgThumbViewHolder
import com.netease.yunxin.app.wisdom.edu.ui.databinding.*
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.rvadapter.BaseDelegate
import com.netease.yunxin.app.wisdom.rvadapter.BaseViewHolder
import com.netease.yunxin.app.wisdom.rvadapter.OnItemClickListener

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
        const val leftText: Int = 1
        const val rightText: Int = 2
        const val leftImage: Int = 3
        const val rightImage: Int = 4
        const val unknown: Int = 100
    }

    init {
        setDelegate(object : BaseDelegate<ChatRoomMessage>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*>? {
                val viewHolder = when (viewType) {
                    tips -> TipsViewHolder(ItemMsgTipsBinding.inflate(LayoutInflater.from(activity), parent, false))
                    leftText -> LeftTextViewHolder(
                        ItemTextMsgLeftBinding.inflate(
                            LayoutInflater.from(activity),
                            parent,
                            false
                        )
                    )
                    rightText -> RightTextViewHolder(
                        ItemTextMsgRightBinding.inflate(
                            LayoutInflater.from(activity),
                            parent,
                            false
                        )
                    )
                    leftImage -> LeftImageViewHolder(
                        ItemImageMsgLeftBinding.inflate(
                            LayoutInflater.from(activity),
                            parent,
                            false
                        )
                    )
                    rightImage -> RightImageViewHolder(
                        ItemImageMsgRightBinding.inflate(
                            LayoutInflater.from(activity),
                            parent,
                            false
                        )
                    )
                    else -> UnknownTipsViewHolder(ItemUnknownMsgTipsBinding.inflate(LayoutInflater.from(activity),
                        parent,
                        false))
                }
                viewHolder.let { this@ChatAdapter.bindViewClickListener(it, viewType) }
                return viewHolder
            }

            override fun getItemViewType(data: ChatRoomMessage, pos: Int): Int {
                return when (data.msgType) {
                    MsgTypeEnum.tip -> tips
                    MsgTypeEnum.image -> if (data.direct == MsgDirectionEnum.In) leftImage else rightImage
                    MsgTypeEnum.text -> if (data.direct == MsgDirectionEnum.In) leftText else rightText
                    else -> unknown
                }
            }

        })
        addChildClickViewIds(
            R.id.iv_message_item_alert,
            R.id.iv_message_item_thumb_thumbnail,
            R.id.message_item_thumb_progress_cover
        )
    }

    inner class TipsViewHolder(val binding: ItemMsgTipsBinding) : BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text = TimeUtil.getNowDatetime(item.time)
        }
    }

    inner class UnknownTipsViewHolder(val binding: ItemUnknownMsgTipsBinding) :
        BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
        }
    }


    inner class LeftTextViewHolder(val binding: ItemTextMsgLeftBinding) :
        BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text = getSenderName(item, activity)
            binding.tvContent.text = item.content ?: ""
        }
    }

    inner class RightTextViewHolder(val binding: ItemTextMsgRightBinding) :
        BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text = getSenderName(item, activity)
            binding.tvContent.text = item.content
            when (item.status) {
                MsgStatusEnum.fail -> {
                    binding.ivMessageItemAlert.visibility = View.VISIBLE
                    binding.progressBarMessageItem.visibility = View.GONE
                }
                MsgStatusEnum.sending -> {
                    binding.ivMessageItemAlert.visibility = View.GONE
                    binding.progressBarMessageItem.visibility = View.VISIBLE
                }
                else -> {
                    binding.ivMessageItemAlert.visibility = View.GONE
                    binding.progressBarMessageItem.visibility = View.GONE
                }
            }
        }
    }

    inner class LeftImageViewHolder(val binding: ItemImageMsgLeftBinding) : MsgThumbViewHolder(binding) {
        override fun findViews() {

        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            initParameter(
                item,
                binding.ivMessageItemThumbThumbnail,
                binding.ivMessageItemAlert
            )
            binding.tvName.text = getSenderName(item, activity)
            loadThumb()
        }
    }

    inner class RightImageViewHolder(val binding: ItemImageMsgRightBinding) : MsgThumbViewHolder(binding) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            initParameter(
                item,
                binding.ivMessageItemThumbThumbnail,
                binding.ivMessageItemAlert,
                binding.progressBarMessageItem
            )
            binding.tvName.text = getSenderName(item, activity)
            loadThumb()
        }
    }

    fun getSenderName(message: ChatRoomMessage, activity: BaseClassActivity): String {
        return if (message.direct == MsgDirectionEnum.In) {
            if (TextUtils.isEmpty(message.fromNick)) message.chatRoomMessageExtension.senderNick else message.fromNick
        } else {
            activity.run {
                val suffix =
                    if (eduManager.getEntryMember().role == NEEduRoleType.HOST.value)
                        getString(R.string.teacher_label)
                    else
                        getString(R.string.student_label)
                "${entryMember.userName}$suffix"
            }
        }
    }
}

