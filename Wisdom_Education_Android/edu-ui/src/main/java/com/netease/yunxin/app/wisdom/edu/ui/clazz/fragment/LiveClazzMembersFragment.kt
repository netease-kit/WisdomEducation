/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.netease.yunxin.app.wisdom.base.util.CommonUtil
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseMemberView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.LiveMemberTitleListAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.loadmore.LoadMoreConstant
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel.ChatRoomViewModel
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentLiveclazzMembersBinding
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding

class LiveClazzMembersFragment : BaseFragment(R.layout.fragment_liveclazz_members), BaseAdapter.OnItemChildClickListener<NEEduMember> {
    private var patternStr: String = ""
    private val binding: FragmentLiveclazzMembersBinding by viewBinding()
    private lateinit var adapter: LiveMemberTitleListAdapter
    private val chatViewModel: ChatRoomViewModel by activityViewModels()

    override fun initData() {
        adapter = LiveMemberTitleListAdapter(requireContext(),
            eduManager.getMemberService().getMemberList().filter { !it.isHost() } as MutableList<NEEduMember>,
            loadMoreListener = requestLoadMoreListener)
        eduManager.getMemberService().onMemberJoin().observe(this, { t ->
            val memberList = t.filter { !it.isHost() } as MutableList<NEEduMember>
            if(memberList.size >= LoadMoreConstant.LOAD_MORE_PAGE && memberList.all { !it.isHolder() }) {
                // add holder to support load more
                memberList.add(NEEduMember.buildLoadMoreHoldMember(NEEduRoleType.BROADCASTER))
            }
            adapter.loadMoreStatus = chatViewModel.loadMoreStatus
            adapter.isSearch = false
            adapter.updateDataAndNotify(memberList.sortedByDescending { it.time })
            updateAllMembersText()
        })

    }

    private fun updateAllMembersText() {
        chatViewModel.fetchRoomInfo {
            var userCount = it.onlineUserCount
            if (eduManager.getMemberService().getMemberList().any { it1 -> it1.isHost() }) {
                userCount -= 1
            }
            binding.titleMember.text = getString(R.string.all_room_members, userCount)
        }
    }

    override fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        binding.apply {
            rcvMemberList.layoutManager = layoutManager
            rcvMemberList.adapter = adapter
            adapter.setOnItemChildClickListener(this@LiveClazzMembersFragment)

            btMembersSearch.visibility = View.VISIBLE
            etMembersSearch.visibility = View.VISIBLE
            btMembersSearch.setOnClickListener {
                val memberList = eduManager.getMemberService().getMemberList()
                    .filter { !it.isHost() && it.userName.contains(patternStr) } as MutableList<NEEduMember>
                adapter.isSearch = true
                adapter.updateDataAndNotify(memberList)
            }
            ivClearText.setOnClickListener {
                etMembersSearch.setText("")
            }
            etMembersSearch.addTextChangedListener(onTextChangedListener)

            ivMemberHide.setOnClickListener {
                (activity as BaseMemberView).hideFragmentWithMembers()
            }
        }
    }

    private val onTextChangedListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            binding.apply {
                patternStr = etMembersSearch.text.toString()
                if (etMembersSearch.length() > 0) {
                    ivClearText.visibility = View.VISIBLE
                } else {
                    ivClearText.visibility = View.GONE
                }
            }

        }
    }

    private val requestLoadMoreListener = object : LiveMemberTitleListAdapter.RequestLoadMoreListener {
        override fun onLoadMoreRequested() {
            binding.rcvMemberList.postDelayed({
                chatViewModel.fetchLiveRoomMembers()
            }, LoadMoreConstant.LOAD_MORE_DELAY)
        }
    }

    override fun onItemChildClick(adapter: BaseAdapter<NEEduMember>?, v: View?, position: Int) {
        when (v!!.id) {
            R.id.load_more_load_fail_view -> activity?.let {
                chatViewModel.fetchLiveRoomMembers()
            }
        }
    }

    override fun hideKeyBoard() {
        super.hideKeyBoard()
        activity?.let { CommonUtil.hideKeyBoard(it, binding.etMembersSearch) }
    }
}