/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.edu.ui.clazz.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.netease.yunxin.app.wisdom.edu.ui.R
import java.io.File
import java.util.*

class MsgThumbImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    /**
     * *************************** External interfaces **********************************
     */
    fun loadAsResource(resId: Int) {
        Glide.with(context.applicationContext).load(resId).apply(
            RequestOptions()
                .transform(
                    CenterCrop(), RoundedCorners(getRoundCornerRadius())
                )
        ).into(this)
    }

    fun loadAsPath(path: String?, width: Int, height: Int, maskId: Int, ext: String) {
        if (TextUtils.isEmpty(path)) {
            loadAsResource(R.drawable.ic_image_default)
            return
        }
        val builder: RequestBuilder<*> = if (isGif(ext)) {
            Glide.with(context.applicationContext).asGif().load(File(path)).apply(
                RequestOptions()
                    .transform(
                        CenterCrop(), RoundedCorners(getRoundCornerRadius())
                    )
            )
        } else {
            val options = RequestOptions()
                .override(width, height)
                .fitCenter()
                .placeholder(R.drawable.ic_image_default)
                .error(R.drawable.ic_image_default)
            Glide.with(context.applicationContext)
                .asBitmap()
                .apply(options)
                .load(File(path)).apply(
                    RequestOptions()
                        .transform(
                            CenterCrop(), RoundedCorners(getRoundCornerRadius())
                        )
                )
        }
        builder.into(this)
    }

    companion object {
        fun isGif(extension: String): Boolean {
            return !TextUtils.isEmpty(extension) && extension.lowercase(Locale.getDefault()) == "gif"
        }
    }

    private fun getRoundCornerRadius(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.common_dp_4)
    }
}