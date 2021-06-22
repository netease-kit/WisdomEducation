/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.yunxin.app.wisdom.base.util.ClipboardUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.ChatAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.ItemClickListerAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ActionSheetDialog
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel.ChatRoomViewModel
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentChatroomBinding
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding

class ChatRoomFragment : BaseFragment(R.layout.fragment_chatroom),
    View.OnClickListener {
    private val binding: FragmentChatroomBinding by viewBinding()
    private val viewModel: ChatRoomViewModel by activityViewModels()
    private lateinit var adapter: ChatAdapter
    private var isMuteAll = false
    private var roomInfo: ChatRoomInfo? = null
    private val list: MutableList<ChatRoomMessage> = mutableListOf()

    private var lastTime = 0L

    private var firstTips = true

    companion object {
        const val showTimeInterval: Int = 5 * 60 * 1000
        const val COPY_ACTION: Int = 1
    }

    val receiveMessageObserver: (t: List<ChatRoomMessage>) -> Unit = {
        it?.also{
            viewModel.addUnread(it.size)
        }.forEach { t ->
            addMessage(t)
        }
    }

    override fun initData() {
        enterRoom()
        viewModel.onMuteAllChat().observe(this, {
            if (!eduManager.getEntryMember().isHost()) {
                if (firstTips) {// 首次进入，如果是非全体静音，不需要toast 提示
                    firstTips = false
                    if (it) {
                        ToastUtil.showShort(getString(R.string.chat_room_has_been_banned))
                    }
                } else {
                    ToastUtil.showShort(getString(if (it) R.string.chat_room_has_been_banned else R.string.chat_room_has_been_allowed))
                }
                setEditTextEnable(!it)
            }
        })
    }

    private fun enterRoom() {
        val activity = activity as BaseClassActivity?
        activity?.let {
            val data = EnterChatRoomData(activity.eduRoom?.chatRoomId())
            var suffix =
                if (eduManager.getEntryMember().role == NEEduRoleType.HOST.value) getString(R.string.teacher_label) else getString(
                    R.string.student_label)
            data.nick = "${activity.entryMember.userName}${suffix}"
            viewModel.enterChatRoom(data).observe(this, { t ->
                if (t.success()) {
                    roomInfo = t.data?.roomInfo
                } else {
                    when (t.code) {
                        ResponseCode.RES_CHATROOM_BLACKLIST.toInt() -> {
                            ToastUtil.showShort(getString(R.string.have_been_blacklisted))
                        }
                        ResponseCode.RES_ENONEXIST.toInt() -> {
                            ToastUtil.showShort(getString(R.string.chat_room_does_not_exist))
                        }
                        else -> {
                            ToastUtil.showShort("enter chat room failed, code=${t.code}")
                        }
                    }
                }
            })
        }
    }


    override fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(activity as BaseClassActivity, list, object : ItemClickListerAdapter<ChatRoomMessage>() {
            override fun onLongClick(v: View?, pos: Int, data: ChatRoomMessage): Boolean {
                if (data.msgType == MsgTypeEnum.text) {
                    showLongClickDialog(data.content)
                    return true
                }
                return false
            }
        })
        binding.apply {
            rcvMsg.layoutManager = layoutManager
            rcvMsg.adapter = adapter
            btnSendMessage.setOnClickListener(this@ChatRoomFragment)
        }
        setEditTextEnable(!isMuteAll)
        viewModel.onReceiveMessage().observeForever(receiveMessageObserver)
    }

    private fun showLongClickDialog(content: String) {
        val dialog = ActionSheetDialog(requireActivity())
        dialog.addAction(COPY_ACTION, getString(R.string.copy))
        dialog.setOnItemClickListener(object : ItemClickListerAdapter<ActionSheetDialog.ActionItem>() {
            override fun onClick(v: View?, pos: Int, data: ActionSheetDialog.ActionItem) {
                dialog.dismiss()
                when (data?.action) {
                    COPY_ACTION -> {
                        ClipboardUtil.copyText(requireContext(), content)
                        ToastUtil.showShort(requireContext().getString(R.string.copy_success))
                    }
                }
            }
        })
        dialog.show()
    }

    private fun setEditTextEnable(isEnable: Boolean) {
        binding.editSendMsg?.let {
            it.isEnabled = isEnable
            if (isEnable) {
                it.setHint(R.string.hint_im_message)
            } else {
                it.setHint(R.string.chat_muting)
            }
        }
    }

    private fun addMessage(chatRoomMessage: ChatRoomMessage) {
        binding.rcvMsg?.let {
            val currTime = chatRoomMessage.time
            if (currTime - lastTime > showTimeInterval) {
                val tipsMessage: ChatRoomMessage = ChatRoomMessageBuilder.createTipMessage(roomInfo!!.roomId)
                val list1: MutableList<ChatRoomMessage> = mutableListOf()
                list1.add(tipsMessage)
                list1.add(chatRoomMessage)
                adapter.appendDataAndNotify(list1)
            } else {
                adapter.appendDataAndNotify(chatRoomMessage)
            }
            it.scrollToPosition(adapter.itemCount)
            val activity = activity as BaseClassActivity?
            activity!!.updateUnReadCount()
            lastTime = currTime
        }
    }

    override fun onClick(v: View?) {
        if (!binding.editSendMsg.isEnabled) {
            return
        }
        val text = binding.editSendMsg.text.toString()
        if (text.trim { it <= ' ' }.isNotEmpty()) {
            if (context is BaseClassActivity) {
                binding.editSendMsg.setText("")
                roomInfo?.let {
                    val chatMessage = ChatRoomMessageBuilder.createChatRoomTextMessage(it.roomId, text)
                    viewModel.sendMessage(chatMessage)
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.onReceiveMessage().removeObserver(receiveMessageObserver)
        viewModel.exitChatRoom()
        super.onDestroy()
    }
}