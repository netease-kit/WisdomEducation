/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.edu.ui.clazz.viewholder

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewbinding.ViewBinding
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.attachment.FileAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.yunxin.app.wisdom.base.util.BitmapDecoder
import com.netease.yunxin.app.wisdom.base.util.ImageUtil
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.MsgThumbImageView
import com.netease.yunxin.app.wisdom.rvadapter.BaseViewHolder
import java.io.File

abstract class MsgThumbViewHolder(bd: ViewBinding) : BaseViewHolder<ChatRoomMessage>(bd.root) {
    private lateinit var thumbnail: MsgThumbImageView
    private lateinit var alertButton: ImageView
    private var progressBar: ProgressBar? = null
    private lateinit var message: ChatRoomMessage

    fun initParameter(
        message: ChatRoomMessage,
        thumbnail: MsgThumbImageView,
        alertButton: ImageView,
        progressBar: ProgressBar? = null
    ) {
        this.message = message
        this.thumbnail = thumbnail
        this.alertButton = alertButton
        this.progressBar = progressBar
    }

    fun refreshStatus() {
        if (message.attachStatus === AttachStatusEnum.fail || message.status === MsgStatusEnum.fail) {
            alertButton.visibility = View.VISIBLE
        } else {
            alertButton.visibility = View.GONE
        }
        if (message.status === MsgStatusEnum.sending
            || isReceivedMessage() && message.attachStatus === AttachStatusEnum.transferring
        ) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    fun loadThumb() {
        val msgAttachment = message.attachment as FileAttachment
        val path = msgAttachment.path
        val thumbPath = msgAttachment.thumbPath
        if (!TextUtils.isEmpty(thumbPath)) {
            loadThumbnailImage(thumbPath, false, msgAttachment.extension)
        } else if (!TextUtils.isEmpty(path)) {
            loadThumbnailImage(thumbFromSourceFile(path), true, msgAttachment.extension)
        } else {
            loadThumbnailImage(null, false, msgAttachment.extension)
            if (message.attachStatus === AttachStatusEnum.transferred
                || message.attachStatus === AttachStatusEnum.def
            ) {
                downloadAttachment(object : RequestCallback<Void?> {
                    override fun onSuccess(param: Void?) {
                        loadThumbnailImage(msgAttachment.thumbPath, false, msgAttachment.extension)
                        refreshStatus()
                    }

                    override fun onFailed(code: Int) {}
                    override fun onException(exception: Throwable) {}
                })
            }
        }
        refreshStatus()
    }

    private fun loadThumbnailImage(path: String?, isOriginal: Boolean, ext: String) {
        setImageSize(path)
        if (path != null) {
            thumbnail.loadAsPath(
                path,
                getImageMaxEdge(),
                getImageMaxEdge(),
                maskBg(),
                ext
            )
        } else {
            thumbnail.loadAsResource(R.drawable.ic_image_default)
        }
    }

    fun loadThumbnailImage(path: String?, ext: String) {
        setImageSize(path)
        if (path != null) {
            thumbnail.loadAsPath(path, getImageMaxEdge(), getImageMaxEdge(), maskBg(), ext)
        } else {
            thumbnail.loadAsResource(R.drawable.ic_image_default)
        }
    }

    private fun setImageSize(thumbPath: String?) {
        var bounds: IntArray? = null
        if (thumbPath != null) {
            bounds = BitmapDecoder.decodeBound(File(thumbPath))
        }
        if (bounds == null) {
            if (message.msgType === MsgTypeEnum.image) {
                val attachment = message.attachment as ImageAttachment
                bounds = intArrayOf(attachment.width, attachment.height)
            }
        }
        if (bounds != null) {
            val imageSize: ImageUtil.ImageSize = ImageUtil.getThumbnailDisplaySize(
                bounds[0].toFloat(),
                bounds[1].toFloat(), getImageMaxEdge().toFloat(), getImageMinEdge().toFloat()
            )
            setLayoutParams(imageSize.width, imageSize.height, thumbnail)
        }
    }

    private fun maskBg(): Int {
        return R.drawable.bg_message_item_round_bg
    }

    fun getImageMaxEdge(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.common_dp_190)
    }

    fun getImageMinEdge(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.common_dp_190)
    }

    protected open fun isReceivedMessage(): Boolean {
        return message.direct == MsgDirectionEnum.In
    }

    // set the width and height of the control
    protected open fun setLayoutParams(width: Int, height: Int, vararg views: View) {
        for (view in views) {
            val maskParams = view.layoutParams
            maskParams.width = width
            maskParams.height = height
            view.layoutParams = maskParams
        }
    }

    private fun thumbFromSourceFile(path: String?): String? {
        return path
    }

    /**
     * Download attachments or thumbnail images
     */
    protected open fun downloadAttachment(callback: RequestCallback<Void?>?) {
        if (message.attachment != null && message.attachment is FileAttachment) NIMClient.getService(
            MsgService::class.java
        ).downloadAttachment(message, true).setCallback(callback)
    }
}