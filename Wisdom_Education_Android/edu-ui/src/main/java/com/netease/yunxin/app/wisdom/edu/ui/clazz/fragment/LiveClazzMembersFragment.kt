/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseMemberView
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.MemberTitleListAdapter
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentLiveclazzMembersBinding
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding

class LiveClazzMembersFragment : BaseFragment(R.layout.fragment_liveclazz_members) {
    private var patternStr: String = ""
    private val binding: FragmentLiveclazzMembersBinding by viewBinding()
    private lateinit var adapter: MemberTitleListAdapter

    override fun initData() {
        adapter = MemberTitleListAdapter(requireContext(),
            eduManager.getMemberService().getMemberList().filter { !it.isHost() } as MutableList<NEEduMember>)
        eduManager.getMemberService().onMemberJoin().observe(this, { t ->
            adapter.updateDataAndNotify(t.filter { !it.isHost() })
            updateAllMembersText()
        })
        updateAllMembersText()
    }

    private fun updateAllMembersText() {
        eduManager.getMemberService().getMemberList().filter { !it.isHost() }.let { t ->
            binding.titleMember.text = getString(R.string.all_room_members, t.size)
        }
    }

    override fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        binding.apply {
            rcvMemberList.layoutManager = layoutManager
            rcvMemberList.adapter = adapter

            btMembersSearch.visibility = View.VISIBLE
            etMembersSearch.visibility = View.VISIBLE
            btMembersSearch.setOnClickListener {
                var memberList = eduManager.getMemberService().getMemberList()
                    .filter { !it.isHost() && it.userName.contains(patternStr) } as MutableList<NEEduMember>
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

}