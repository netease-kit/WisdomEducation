/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.clazz.loadmore.LoadMoreStatus
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ItemLoadMoreBinding
import com.netease.yunxin.app.wisdom.edu.ui.databinding.ItemMemberTextListBinding
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.rvadapter.BaseDelegate
import com.netease.yunxin.app.wisdom.rvadapter.BaseViewHolder
import com.netease.yunxin.app.wisdom.rvadapter.OnItemClickListener

class LiveMemberTitleListAdapter(
    val context: Context,
    dataList: MutableList<NEEduMember>,
    listener: OnItemClickListener<NEEduMember>? = null,
    var loadMoreListener: RequestLoadMoreListener? = null
) : BaseAdapter<NEEduMember>(dataList, listener) {
    private val autoLoadMore = 1
    var loadMoreStatus = LoadMoreStatus.DEFAULT
    var isSearch = false

    companion object {
        // viewType
        const val TYPE_NORMAL: Int = 0
        const val TYPE_LOAD_MORE: Int = 1
    }

    init {
        setDelegate(object : BaseDelegate<NEEduMember>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*> {
                return when (viewType) {
                    TYPE_LOAD_MORE -> {
                        val binding = ItemLoadMoreBinding.inflate(LayoutInflater.from(context), parent, false)
                        val viewHolder = LoadMoreViewHolder(binding)
                        this@LiveMemberTitleListAdapter.bindViewClickListener(viewHolder, viewType)
                        viewHolder
                    } else -> {
                        val binding = ItemMemberTextListBinding.inflate(LayoutInflater.from(context), parent, false)
                        MemberViewHolder(binding)
                    }
                }
            }

            override fun getItemViewType(data: NEEduMember, pos: Int): Int {
                if(!isSearch) autoLoadMore(pos)
                return if(data.isHolder()) {
                    TYPE_LOAD_MORE
                } else TYPE_NORMAL
            }
        })
        addChildClickViewIds(R.id.load_more_load_fail_view)
    }

    inner class MemberViewHolder(val binding: ItemMemberTextListBinding) : BaseViewHolder<NEEduMember>(binding.root) {
        override fun findViews() {
        }

        override fun onBindViewHolder(item: NEEduMember) {
            binding.tvMemberName.text = item.userName
        }
    }

    inner class LoadMoreViewHolder(val binding: ItemLoadMoreBinding) : BaseViewHolder<NEEduMember>(binding.root) {
        override fun findViews() {

        }

        override fun onBindViewHolder(data: NEEduMember) {
            convert()
        }

        fun convert() {
            when (loadMoreStatus) {
                LoadMoreStatus.LOADING -> {
                    setVisibleStatus(true, false, false)
                }
                LoadMoreStatus.FAIL -> {
                    setVisibleStatus(false, true, false)
                }
                LoadMoreStatus.END -> {
                    setVisibleStatus(false, false, true)
                }
            }
        }

        private fun setVisibleStatus(vararg visibleArgs: Boolean) {
            setVisible(R.id.load_more_loading_view, visibleArgs[0])
            setVisible(R.id.load_more_load_fail_view, visibleArgs[1])
            setVisible(R.id.load_more_load_end_view, visibleArgs[2])
        }
    }

    private fun autoLoadMore(position: Int) {
        if (position >= itemCount - autoLoadMore) {
            loadMoreListener?.onLoadMoreRequested()
        }
    }

    interface RequestLoadMoreListener {
        fun onLoadMoreRequested()
    }
}

