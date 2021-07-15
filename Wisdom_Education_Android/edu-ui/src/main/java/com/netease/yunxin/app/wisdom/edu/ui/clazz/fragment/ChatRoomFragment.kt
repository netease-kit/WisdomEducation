/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import com.netease.yunxin.app.wisdom.base.util.CommonUtil.setOnClickThrottleFirst
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.AttachmentProgress
import com.netease.yunxin.app.wisdom.base.util.ClipboardUtil
import com.netease.yunxin.app.wisdom.base.util.FileUtils
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.BaseAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.ChatAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.ItemClickListerAdapter
import com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog.ActionSheetDialog
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel.ChatRoomViewModel
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentChatroomBinding
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding
import java.io.File

class ChatRoomFragment : BaseFragment(R.layout.fragment_chatroom),
    View.OnClickListener, BaseAdapter.OnItemChildClickListener<ChatRoomMessage> {
    private val binding: FragmentChatroomBinding by viewBinding()
    private val viewModel: ChatRoomViewModel by activityViewModels()
    private lateinit var adapter: ChatAdapter
    private var isMuteAll = false
    private var roomInfo: ChatRoomInfo? = null
    private val list: MutableList<ChatRoomMessage> = mutableListOf()
    private var lastTime = 0L
    private var firstTips = true

    private lateinit var takePictureLaunch: ActivityResultLauncher<Uri>
    private lateinit var choosePictureLaunch: ActivityResultLauncher<Uri>
    private var takePictureFile: File? = null

    companion object {
        const val showTimeInterval: Int = 5 * 60 * 1000
        const val COPY_ACTION: Int = 1

        private const val ACTION_TAKE_PICTURE = 5
        private const val ACTION_CHOOSE_PICTURE = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        takePictureLaunch = registerForActivityResult(TakePicture()) {
            sendImageMessage(it, takePictureFile)
        }
        choosePictureLaunch = registerForActivityResult(ChoosePicture()) {
            sendImageMessage(it)
        }
    }

    private fun sendImageMessage(uri: Uri?, pictureFile: File? = null) {
        var path: String? = uri?.let { FileUtils.filePathFromUri(requireActivity(), it) }
        var file: File? = path?.let { File(path) }
        if (file == null || !file.exists()) {
            file = pictureFile
        }
        if (file == null) return
        roomInfo?.let {
            val chatMessage =
                ChatRoomMessageBuilder.createChatRoomImageMessage(it.roomId, file, file?.name)
            addMessage(chatMessage)
            viewModel.sendMessage(chatMessage)
        }
    }

    val receiveMessageObserver = { it: List<ChatRoomMessage> ->
        it?.also {
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
                onMuteAllChat(it)
            }
        })
    }

    private fun enterRoom() {
        val activity = activity as BaseClassActivity?
        activity?.let {
            val data = EnterChatRoomData(activity.eduRoom?.chatRoomId())
            var suffix =
                if (eduManager.getEntryMember().role == NEEduRoleType.HOST.value) getString(R.string.teacher_label) else getString(
                    R.string.student_label
                )
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
        adapter.setOnItemChildClickListener(this)
        binding.apply {
            rcvMsg.layoutManager = layoutManager
            rcvMsg.adapter = adapter
            btnSendMessage.setOnClickThrottleFirst(this@ChatRoomFragment)
            ivChatPic.setOnClickThrottleFirst(this@ChatRoomFragment)
        }
        onMuteAllChat(false)
        viewModel.onReceiveMessage().observeForever(receiveMessageObserver)
        viewModel.onMessageStatusChange().observeForever(messageStatusChangeObserver)
        viewModel.onAttachmentProgressChange().observeForever(attachmentProgressChangeObserver)
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

    private fun onMuteAllChat(isMuteAll: Boolean) {
        this.isMuteAll = isMuteAll
        binding.editSendMsg?.let {
            it.isEnabled = !isMuteAll
            if (isMuteAll) {
                it.setHint(R.string.chat_muting)
            } else {
                it.setHint(R.string.hint_im_message)
            }
        }
    }

    private fun addMessage(chatRoomMessage: ChatRoomMessage) {
        binding.rcvMsg?.let {
            if(adapter.containsData(chatRoomMessage)) {
                adapter.refreshDataAndNotify(chatRoomMessage, null, compare = { it1, it2 ->
                    it1.isTheSame(it2)
                })
            } else {
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
                it.scrollToPosition(adapter.itemCount - 1)
                val activity = activity as BaseClassActivity?
                activity!!.updateUnReadCount()
                lastTime = currTime
            }
        }
    }

    override fun onClick(v: View?) {
        if (v == binding.btnSendMessage) {
            if (isMuteAll) {
                return
            }
            val text = binding.editSendMsg.text.toString()
            if (text.trim { it <= ' ' }.isNotEmpty()) {
                if (context is BaseClassActivity) {
                    binding.editSendMsg.setText("")
                    roomInfo?.let {
                        val chatMessage = ChatRoomMessageBuilder.createChatRoomTextMessage(it.roomId, text)
                        addMessage(chatMessage)
                        viewModel.sendMessage(chatMessage)
                    }
                }
            }
        } else if (v == binding.ivChatPic) {
            if (isMuteAll) {
                ToastUtil.showShort(getString(R.string.chat_muting))
            } else {
                showPickPhotoActionSheetDialog()
            }
        }
    }

    private fun showPickPhotoActionSheetDialog() {
        val dialog = ActionSheetDialog(requireContext())
        dialog.addAction(
            ACTION_TAKE_PICTURE,
            getString(R.string.take_picture)
        )
        dialog.addAction(
            ACTION_CHOOSE_PICTURE,
            getString(R.string.select_from_your_phone_photo_album)
        )
        dialog.setOnItemClickListener(object : ItemClickListerAdapter<ActionSheetDialog.ActionItem>() {
            override fun onClick(v: View?, pos: Int, data: ActionSheetDialog.ActionItem) {
                super.onClick(v, pos, data)
                dialog.dismiss()
                when (data.action) {
                    ACTION_TAKE_PICTURE -> takePicture()
                    ACTION_CHOOSE_PICTURE -> choosePicture()
                }
            }
        })
        dialog.show()
    }


    inner class TakePicture : ActivityResultContract<Uri, Uri?>() {
        private var input: Uri? = null

        @CallSuper
        override fun createIntent(context: Context, input: Uri?): Intent {
            this.input = input
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, input)
            return takePictureIntent
        }

        override fun getSynchronousResult(context: Context, input: Uri?): SynchronousResult<Uri?>? {
            return null
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode != Activity.RESULT_OK) {
                takePictureFile = null
                null
            } else {
                input
            }
        }
    }

    inner class ChoosePicture : ActivityResultContract<Uri, Uri?>() {

        @CallSuper
        override fun createIntent(context: Context, input: Uri?): Intent {
            val choosePictureIntent: Intent
            if (Build.VERSION.SDK_INT < 19) {
                choosePictureIntent = Intent(Intent.ACTION_GET_CONTENT)
                choosePictureIntent.type = "image/*"
            } else {
                choosePictureIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            }
            return choosePictureIntent
        }

        override fun getSynchronousResult(context: Context, input: Uri?): SynchronousResult<Uri?>? {
            return null
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (intent == null || resultCode != Activity.RESULT_OK)
                null
            else
                intent.data
        }
    }

    fun takePicture() {
        var photoURI: Uri? = null
        takePictureFile = if (FileUtils.existSDCard()) {
            File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
        } else {
            Environment.getDataDirectory()
        }
        takePictureFile = takePictureFile?.let { FileUtils.createFile(it, "IMG_", ".jpg") }
        if (takePictureFile != null) {
            photoURI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //Android N必须使用这种方式
                FileProvider.getUriForFile(
                    requireContext(), requireContext().packageName.toString() +
                            ".generic.file.provider", takePictureFile!!
                )
            } else {
                Uri.fromFile(takePictureFile)
            }
            photoURI?.let {
                takePictureLaunch.launch(photoURI)
            }
        }
    }

    fun choosePicture() {
        choosePictureLaunch.launch(null)
    }

    private val messageStatusChangeObserver = { it: ChatRoomMessage ->
        adapter.refreshDataAndNotify(it!!, null, compare = { it1, it2 ->
            it1.isTheSame(it2)
        })
    }

    private val attachmentProgressChangeObserver = { _: AttachmentProgress ->
    }

    override fun onItemChildClick(adapter: BaseAdapter<ChatRoomMessage>?, v: View?, position: Int) {
        when (v!!.id) {
            R.id.iv_message_item_alert -> activity?.let {
                adapter?.let { it1 ->
                    it1.refreshDataAndNotify(it1.getItem(position), null, compare = { it1, it2 ->
                        it1.isTheSame(it2)
                    })
                    viewModel.sendMessage(it1.getItem(position))
                }
            }
            R.id.iv_message_item_thumb_thumbnail, R.id.message_item_thumb_progress_cover -> activity?.let {
                adapter?.getItem(position)?.let { it1 ->
                    if (it1.msgType == MsgTypeEnum.image) {
                        if (context is BaseClassActivity) {
                            (context as BaseClassActivity).showZoomImageFragment(it1)
                        }
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        viewModel.onReceiveMessage().removeObserver(receiveMessageObserver)
        viewModel.onMessageStatusChange().removeObserver(messageStatusChangeObserver)
        viewModel.onAttachmentProgressChange().removeObserver(attachmentProgressChangeObserver)
        viewModel.exitChatRoom()
        super.onDestroy()
    }
}