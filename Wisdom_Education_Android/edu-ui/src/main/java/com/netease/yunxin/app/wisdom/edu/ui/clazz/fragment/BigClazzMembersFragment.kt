/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.setOnClickThrottleFirst
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStateValue
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.clazz.BaseNormalClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel.ChatRoomViewModel
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentBigclazzMembersBinding
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding
import com.netease.yunxin.kit.alog.ALog

class BigClazzMembersFragment : BaseFragment(R.layout.fragment_bigclazz_members) {
    private val binding: FragmentBigclazzMembersBinding by viewBinding()
    private val viewModel: ChatRoomViewModel by activityViewModels()
    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun initData() {
        eduManager.getHandsUpService().onHandsUpStateChange().observe(this, { updateAttachmentMembersText() })
        eduManager.getMemberService().onMemberJoin().observe(this, { updateAllMembersText() })
        viewModel.onMuteAllChat().observe(this, {
            if (eduManager.getEntryMember().isHost()) {
                binding.muteChatAll.isSelected = it
            }
        })
        updateAttachmentMembersText()
        updateAllMembersText()

    }

    private fun updateAttachmentMembersText() {
        eduManager.getHandsUpService().getOnStageMemberList().filter { !it.isHost() }.let { t ->
            binding.tablayout.getTabAt(0)?.text = getString(R.string.attachment_members, t.size)
        }
    }

    private fun updateAllMembersText() {
        eduManager.getMemberService().getMemberList().filter { !it.isHost() }.let { t ->
            binding.tablayout.getTabAt(1)?.text = getString(R.string.all_members, t.size)
        }
    }

    override fun initViews() {
        binding.apply {
            fragmentList.add(MemberStageFragment())
            fragmentList.add(MemberStudentsFragment())
            //Initialize viewPage
            viewpager.adapter = object : FragmentStateAdapter(requireActivity()) {
                override fun getItemCount(): Int {
                    return fragmentList.size
                }

                override fun createFragment(position: Int): Fragment {
                    return fragmentList[position]
                }
            }
            val titles = arrayOf(getString(R.string.attachment_members, 0), getString(R.string.all_members, 0))
            TabLayoutMediator(
                tablayout, viewpager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }.attach()
            ivMemberHide.setOnClickListener {
                (activity as BaseNormalClassActivity).hideFragmentWithMembers()
            }
            if (eduManager.getEntryMember().isHost()) {
                muteAudioAll.visibility = View.VISIBLE
                muteAudioAll.setOnClickThrottleFirst {
                    eduManager.getRtcService()
                        .muteAllAudio(roomUuid = eduManager.getRoom().roomUuid, NEEduStateValue.OPEN)
                        .observe(this@BigClazzMembersFragment, {
                            if (it.success()) {
                                ALog.i(tag, "muteAudioAll success")
                                ToastUtil.showShort(R.string.operation_successful)
                            } else {
                                ALog.i(tag, "muteAudioAll fail code=${it.code}")
                                ToastUtil.showShort(R.string.operation_fail)
                            }
                        })
                }

                muteChatAll.visibility = View.VISIBLE
                ivHintMuteAudioAll.visibility = View.VISIBLE
                muteChatAll.setOnClickThrottleFirst {
                    eduManager.getIMService().muteAllChat(
                        roomUuid = eduManager.getRoom().roomUuid,
                        if (!muteChatAll.isSelected) NEEduStateValue.OPEN else NEEduStateValue.CLOSE
                    ).observe(this@BigClazzMembersFragment, {
                        if (it.success()) {
                            ALog.i(tag, "muteChatAll success")
                            ToastUtil.showShort(R.string.operation_successful)
                        } else {
                            ALog.i(tag, "muteChatAll fail code=${it.code}")
                            ToastUtil.showShort(R.string.operation_fail)
                        }
                    })
                }
                ivHintMuteAudioAll.setOnClickListener {
                    hintsMuteAllView.show()
                }
            }
        }
    }

}