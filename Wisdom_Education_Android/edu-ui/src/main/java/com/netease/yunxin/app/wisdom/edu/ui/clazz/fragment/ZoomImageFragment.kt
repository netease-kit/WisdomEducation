/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.fragment

import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.netease.nimlib.sdk.AbortableFuture
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.yunxin.app.wisdom.base.util.BitmapDecoder
import com.netease.yunxin.app.wisdom.base.util.ImageUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseFragment
import com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel.ChatRoomViewModel
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.MultiTouchZoomableImageView
import com.netease.yunxin.app.wisdom.edu.ui.databinding.FragmentZoomImageBinding
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 */
class ZoomImageFragment : BaseFragment(R.layout.fragment_zoom_image) {
    private val binding: FragmentZoomImageBinding by viewBinding()
    private val viewModel: ChatRoomViewModel by activityViewModels()
    private var message: IMMessage? = null
    private var downloadFuture: AbortableFuture<Void>? = null
    private lateinit var loadingLayout: LinearLayout
    private lateinit var image: MultiTouchZoomableImageView
    private lateinit var simpleImageView: ImageView
    private var mode: Int = 0

    companion object {
        const val INTENT_EXTRA_IMAGE = "INTENT_EXTRA_IMAGE"
        const val MODE_NORMAL = 0
        const val MODE_GIF = 1
    }

    override fun parseArguments() {
        super.parseArguments()
        this.message = arguments?.getSerializable(INTENT_EXTRA_IMAGE) as IMMessage
        mode = if (ImageUtil.isGif((message!!.attachment as ImageAttachment).extension)) MODE_GIF else MODE_NORMAL
    }

    override fun initViews() {
        binding.let {
            it.ivHideZoomImage.setOnClickListener {
                hideFragment()
            }
            loadingLayout = it.messageItemThumbProgressCover
            image = it.watchImageView
            simpleImageView = it.simpleImageView
        }

        if (mode == MODE_GIF) {
            simpleImageView.visibility = View.VISIBLE
        } else if (mode == MODE_NORMAL) {
            simpleImageView.visibility = View.GONE
        }
        loadMsgAndDisplay()
        viewModel.onMessageStatusChange().observeForever(messageStatusChangeObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        downloadFuture?.let {
            it.abort()
            downloadFuture = null
        }
    }

    private fun resetDownload() {
        downloadFuture?.let {
            it.abort()
            downloadFuture = null
        }
    }

    // 加载并显示
    private fun loadMsgAndDisplay() {
        if (mode == MODE_NORMAL) {
            resetDownload()
            requestOriImage(message!!)
        } else if (mode == MODE_GIF) {
            displaySimpleImage()
        }
    }

    private fun displaySimpleImage() {
        val path = (message!!.attachment as ImageAttachment).path
        val thumbPath = (message!!.attachment as ImageAttachment).thumbPath
        if (!TextUtils.isEmpty(path)) {
            Glide.with(this).asGif().load(File(path)).into(simpleImageView)
            return
        }
        if (!TextUtils.isEmpty(thumbPath)) {
            Glide.with(this).asGif().load(File(thumbPath)).into(simpleImageView)
        }
        if (message!!.direct == MsgDirectionEnum.In) {
            requestOriImage(message!!)
        }
    }

    // 若图片已下载，直接显示图片；若图片未下载，则下载图片
    private fun requestOriImage(msg: IMMessage) {
        if (isOriginImageHasDownloaded(msg)) {
            onDownloadSuccess(msg)
            return
        }

        // async download original image
        onDownloadStart(msg)
        message = msg // 下载成功之后，判断是否是同一条消息时需要使用
        downloadFuture = NIMClient.getService(MsgService::class.java).downloadAttachment(msg, false)
        downloadFuture?.setCallback(object : RequestCallback<Void?> {
            override fun onSuccess(param: Void?) {
                onDownloadSuccess(msg)
            }

            override fun onFailed(code: Int) {}
            override fun onException(exception: Throwable) {}
        })
    }

    private fun isOriginImageHasDownloaded(message: IMMessage): Boolean {
        return (message.attachStatus == AttachStatusEnum.transferred &&
                !TextUtils.isEmpty((message.attachment as ImageAttachment).path))
                || message!!.direct == MsgDirectionEnum.Out
    }

    /**
     * ******************************** 设置图片 *********************************
     */
    private fun setThumbnail(msg: IMMessage) {
        val thumbPath = (msg.attachment as ImageAttachment).thumbPath
        val path = (msg.attachment as ImageAttachment).path
        var bitmap: Bitmap? = null
        if (!TextUtils.isEmpty(thumbPath)) {
            bitmap = BitmapDecoder.decodeSampledForDisplay(thumbPath)
            bitmap = ImageUtil.rotateBitmapInNeeded(thumbPath, bitmap)
        } else if (!TextUtils.isEmpty(path)) {
            bitmap = BitmapDecoder.decodeSampledForDisplay(path)
            bitmap = ImageUtil.rotateBitmapInNeeded(path, bitmap)
        }
        if (bitmap != null) {
            image.setImageBitmap(bitmap)
            return
        }
        image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(requireActivity(), getImageResOnLoading()))
    }

    private fun setImageView(msg: IMMessage) {
        val path = (msg.attachment as ImageAttachment).path
        if (TextUtils.isEmpty(path)) {
            image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(requireActivity(), getImageResOnLoading()))
            return
        }
        var bitmap: Bitmap? = BitmapDecoder.decodeSampledForDisplay(path, false)
        bitmap = ImageUtil.rotateBitmapInNeeded(path, bitmap)
        if (bitmap == null) {
            ToastUtil.showShort(R.string.picker_image_error)
            image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(requireActivity(), getImageResOnFailed()))
        } else {
            image.setImageBitmap(bitmap)
        }
    }

    private fun getImageResOnLoading(): Int {
        return R.drawable.ic_image_default
    }

    private fun getImageResOnFailed(): Int {
        return R.drawable.ic_image_download_failed
    }

    private fun onDownloadStart(msg: IMMessage) {
        if (TextUtils.isEmpty((msg.attachment as ImageAttachment).path)) {
            loadingLayout.visibility = View.VISIBLE
        } else {
            loadingLayout.visibility = View.GONE
        }
        if (mode == MODE_NORMAL) {
            setThumbnail(msg)
        }
    }

    private fun onDownloadSuccess(msg: IMMessage) {
        loadingLayout.visibility = View.GONE
        if (mode == MODE_NORMAL) {
            GlobalScope.launch(Dispatchers.Main) { setImageView(msg) }
        } else if (mode == MODE_GIF) {
            displaySimpleImage()
        }
    }

    private fun onDownloadFailed() {
        loadingLayout.visibility = View.GONE
        if (mode == MODE_NORMAL) {
            image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(requireActivity(), getImageResOnFailed()))
        } else if (mode == MODE_GIF) {
            simpleImageView.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(requireActivity(), getImageResOnFailed()))
        }
        ToastUtil.showShort(R.string.download_picture_fail)
    }

    private val messageStatusChangeObserver = Observer { it: ChatRoomMessage ->
        if (!it.isTheSame(message)) {
            return@Observer
        }
        if (isOriginImageHasDownloaded(it)) {
            onDownloadSuccess(it)
        } else if (it.attachStatus == AttachStatusEnum.fail) {
            onDownloadFailed()
        }
    }

    private fun hideFragment() {
        (activity as BaseClassActivity).hideZoomImageFragment()
    }

    override fun onDestroy() {
        viewModel.onMessageStatusChange().removeObserver(messageStatusChangeObserver)
        super.onDestroy()
    }
}