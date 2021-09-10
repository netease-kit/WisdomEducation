/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.MemberControlListAdapter
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentMemberItemBinding
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding

class MemberStageFragment : BaseFragment(R.layout.fragment_member_item),
    BaseAdapter.OnItemChildClickListener<NEEduMember> {
    private val binding: FragmentMemberItemBinding by viewBinding()
    private lateinit var adapter: MemberControlListAdapter

    override fun initData() {
        adapter = MemberControlListAdapter(requireContext(),
            eduManager.getHandsUpService().getOnStageMemberList().filter { !it.isHost() } as MutableList<NEEduMember>)
        adapter.setOnItemChildClickListener(this)
        if (eduManager.getEntryMember().isHost()) adapter.isGrantMore = true
        eduManager.getHandsUpService().let { it1 ->
            it1.onHandsUpStateChange()
                .observe(this, { adapter.updateDataAndNotify(it1.getOnStageMemberList().filter { !it.isHost() }) })
        }
        eduManager.getRtcService().onStreamChange().observe(this, { t -> adapter
            .refreshDataAndNotify<Void>(t.first) })
        eduManager.getBoardService().onPermissionGranted().observe(this, { t -> adapter
            .refreshDataAndNotify<Void>(t) })
        eduManager.getShareScreenService().onPermissionGranted().observe(this, { t -> adapter
            .refreshDataAndNotify<Void>(t) })
    }

    override fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        binding.apply {
            rcvMemberList.layoutManager = layoutManager
            rcvMemberList.adapter = adapter
        }
    }

    override fun onItemChildClick(adapter: BaseAdapter<NEEduMember>?, v: View?, position: Int) {
        activity?.let { it as BaseClassActivity }?.apply {
            adapter?.let { it1 ->
                val member = it1.getItem(position)
                val isLocalUser = eduManager.getEntryMember() == member
                when (v!!.id) {
                    R.id.iv_member_more -> showActionSheetDialog(it1.getItem(position))
                    R.id.iv_member_audio -> if (isLocalUser) switchLocalAudio().observe(this, { })
                    else switchRemoteUserAudio(it1.getItem(position))
                    R.id.iv_member_video -> if (isLocalUser) switchLocalVideo().observe(this, { })
                    else switchRemoteUserVideo(it1.getItem(position))
                }
            }
        }
    }

}