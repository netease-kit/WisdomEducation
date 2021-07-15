/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.yunxin.app.wisdom.base.util.TimeUtil
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewholder.MsgThumbViewHolder
import com.netease.yunxin.app.wisdom.edu.ui.databinding.*

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
    }

    init {
        setDelegate(object : BaseDelegate<ChatRoomMessage>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*>? {
                var viewHolder = when (viewType) {
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
                    else -> null
                }
                viewHolder?.let { this@ChatAdapter.bindViewClickListener(it, viewType) }
                return viewHolder
            }

            override fun getItemViewType(data: ChatRoomMessage, pos: Int): Int {
                return when (data.msgType) {
                    MsgTypeEnum.tip -> tips
                    MsgTypeEnum.image -> if (data.direct == MsgDirectionEnum.In) leftImage else rightImage
                    else -> if (data.direct == MsgDirectionEnum.In) leftText else rightText
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


    inner class LeftTextViewHolder(val binding: ItemTextMsgLeftBinding) :
        BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text = getSenderName(item, activity)
            binding.tvContent.text = item.content
        }
    }

    inner class RightTextViewHolder(val binding: ItemTextMsgRightBinding) :
        BaseViewHolder<ChatRoomMessage>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: ChatRoomMessage) {
            binding.tvName.text = getSenderName(item, activity)
            binding.tvContent.text = item.content
            if (item.status == MsgStatusEnum.fail) {
                binding.ivMessageItemAlert.visibility = View.VISIBLE
                binding.progressBarMessageItem.visibility = View.GONE
            } else if (item.status == MsgStatusEnum.sending){
                binding.ivMessageItemAlert.visibility = View.GONE
                binding.progressBarMessageItem.visibility = View.VISIBLE
            } else {
                binding.ivMessageItemAlert.visibility = View.GONE
                binding.progressBarMessageItem.visibility = View.GONE
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
}

