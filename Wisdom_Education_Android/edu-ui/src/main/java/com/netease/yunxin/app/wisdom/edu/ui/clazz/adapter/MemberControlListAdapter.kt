/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ItemMemberControlListBinding
import java.util.*

class MemberControlListAdapter(
    val context: Context,
    dataList: MutableList<NEEduMember>,
    listener: OnItemClickListener<NEEduMember>? = null,
) : BaseAdapter<NEEduMember>(dataList, listener) {

    var isGrantMore: Boolean = false

    init {
        setDelegate(object : BaseDelegate<NEEduMember>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*>? {
                val binding = ItemMemberControlListBinding.inflate(LayoutInflater.from(context), parent, false)
                var viewHolder = MemberViewHolder(binding)
                this@MemberControlListAdapter.bindViewClickListener(viewHolder, viewType)
                return viewHolder
            }

            override fun getItemViewType(data: NEEduMember, pos: Int): Int {
                return 0
            }
        })
        addChildClickViewIds(R.id.iv_member_video, R.id.iv_member_audio, R.id.iv_member_more)
    }

    inner class MemberViewHolder(val binding: ItemMemberControlListBinding) :
        BaseViewHolder<NEEduMember>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: NEEduMember) {
            binding.tvMemberName.text = item.userName
            if (item.isGrantedScreenShare()) {
                binding.ivScreenshareStatus.visibility = View.VISIBLE
            } else {
                binding.ivScreenshareStatus.visibility = View.INVISIBLE
            }
            if (item.isGrantedWhiteboard()) {
                binding.ivWhiteboardStatus.visibility = View.VISIBLE
            } else {
                binding.ivWhiteboardStatus.visibility = View.INVISIBLE
            }
            binding.ivMemberAudio.isSelected = item.hasAudio()
            binding.ivMemberVideo.isSelected = item.hasVideo()
            if (!isGrantMore) binding.ivMemberMore.visibility = View.INVISIBLE
        }
    }
}

