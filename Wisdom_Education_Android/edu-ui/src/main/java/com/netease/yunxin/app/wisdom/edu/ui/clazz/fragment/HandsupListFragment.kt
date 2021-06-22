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
import com.netease.yunxin.app.wisdom.edu.ui.clazz.BigClassBaseActivity
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.BaseAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.HandsUpListAdapter
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentHandsUpListBinding
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding

class HandsUpListFragment : BaseFragment(R.layout.fragment_hands_up_list),
    BaseAdapter.OnItemChildClickListener<NEEduMember> {
    private val binding: FragmentHandsUpListBinding by viewBinding()
    private lateinit var adapter: HandsUpListAdapter

    override fun initData() {
        adapter = HandsUpListAdapter(requireContext(), getHandsUpList())
        adapter.setOnItemChildClickListener(this)
        eduManager.getHandsUpService().let { it1 ->
            it1.onHandsUpStateChange().observe(this, {
                val handsUpList = getHandsUpList()
                adapter.updateDataAndNotify(handsUpList)
                binding.titleHandsup.text = getString(R.string.raise_hand_to_apply_count, handsUpList.size)
            })
        }
    }

    override fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        binding.apply {
            rcvHandsupList.layoutManager = layoutManager
            rcvHandsupList.adapter = adapter
            ivHandsupHide.setOnClickListener {
                hideFragment()
            }
            titleHandsup.text = getString(R.string.raise_hand_to_apply_count, getHandsUpList().size)
        }
    }

    override fun onItemChildClick(adapter: BaseAdapter<NEEduMember>?, v: View?, position: Int) {
        when (v!!.id) {
            R.id.btn_accept -> activity?.let {
                adapter?.let { it1 ->
                    (activity as BigClassBaseActivity).apply {
                        acceptStuRemoteHandsUp(it1.getItem(position).userUuid)
                        toastOperateSuccess()
                    }
                    if (getHandsUpList().size == 1) {
                        hideFragment()
                    }

                }
            }
            R.id.btn_reject -> activity?.let {
                adapter?.let { it1 ->
                    (activity as BigClassBaseActivity).apply {
                        rejectStuRemoteHandsUp(it1.getItem(position).userUuid)
                        toastOperateSuccess()
                    }
                    if (getHandsUpList().size == 1) {
                        hideFragment()
                    }
                }
            }
        }
    }

    private fun hideFragment() {
        (activity as BigClassBaseActivity).hideFragmentWithHandsUp()
    }

    private fun getHandsUpList(): MutableList<NEEduMember> {
        return eduManager.getHandsUpService().getHandsUpApplyList()
    }

}