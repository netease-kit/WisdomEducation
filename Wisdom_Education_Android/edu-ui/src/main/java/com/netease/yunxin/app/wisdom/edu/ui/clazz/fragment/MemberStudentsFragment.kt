/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.MemberTitleListAdapter
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentMemberItemBinding
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding

class MemberStudentsFragment : BaseFragment(R.layout.fragment_member_item) {
    private val binding: FragmentMemberItemBinding by viewBinding()
    private lateinit var adapter: MemberTitleListAdapter

    override fun initData() {
        adapter = MemberTitleListAdapter(requireContext(),
            eduManager.getMemberService().getMemberList().filter { !it.isHost() } as MutableList<NEEduMember>)
        eduManager.getMemberService().onMemberJoin()
            .observe(this, { t -> adapter.updateDataAndNotify(t.filter { !it.isHost() }) })
//        eduManager.getMemberService().onMemberLeave()
//            .observe(this, { t -> adapter.updateDataAndNotify(t.filter { !it.isHost() }) })
    }

    override fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        binding.apply {
            rcvMemberList.layoutManager = layoutManager
            rcvMemberList.adapter = adapter
        }
    }

}