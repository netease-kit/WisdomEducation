/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.education.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import com.netease.yunxin.app.wisdom.education.R

/**
 */
open class LoadingDialog : AlertDialog {
    private var mProgress: ProgressBar? = null
    private var mMessageView: TextView? = null
    private var mMessage: CharSequence? = null

    protected constructor(context: Context, @StyleRes themeResId: Int = R.style.LoadingDialogStyle) : super(context,
        themeResId)

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?,
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLayout()
        initView()
        super.onCreate(savedInstanceState)
    }

    /**
     * load layout
     */
    private fun loadLayout() {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.loading_layout, null)
        mProgress = view.findViewById(R.id.loading)
        mMessageView = view.findViewById(R.id.message)
        setView(view)
    }

    /**
     * init view state
     */
    private fun initView() {
        if (mMessage != null) {
            setMessage(mMessage!!)
        }
    }

    override fun setMessage(message: CharSequence?) {
        if (mProgress != null) {
            if (TextUtils.isEmpty(message)) {
                mMessageView?.visibility = View.GONE
            } else {
                mMessageView?.visibility = View.VISIBLE
                mMessageView?.text = message
            }
        } else {
            mMessage = message
        }
    }

    companion object {
        @JvmOverloads
        fun show(
            context: Context,
            message: CharSequence? = null,
            cancelOnTouchOutside: Boolean = false,
        ): LoadingDialog {
            return show(context, message, true, cancelOnTouchOutside, null)
        }

        @JvmOverloads
        fun show(
            context: Context, message: CharSequence?, cancelable: Boolean,
            cancelOnTouchOutside: Boolean, cancelListener: DialogInterface.OnCancelListener? = null,
        ): LoadingDialog {
            val dialog = LoadingDialog(context)
            dialog.setMessage(message)
            dialog.setCancelable(cancelable)
            dialog.setCanceledOnTouchOutside(cancelOnTouchOutside)
            dialog.setOnCancelListener(cancelListener)
            //dialog.getWindow().setDimAmount(0);
            dialog.show()
            return dialog
        }
    }
}