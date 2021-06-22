/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 */
abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    var context: Context = view.context

    /**
     * typeless is OK
     */
    @JvmField
    var adapter: RecyclerView.Adapter<*>? = null

    /**
     * bound to data type T
     */
    private var data: T? = null
    private val refreshTask = Runnable { bindViewHolder(data!!, null) }

    constructor(parent: ViewGroup, layoutId: Int) : this(
        LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
    )

    /**
     * find all views
     */
    abstract fun findViews()
    fun <V : View?> findViewById(id: Int): V {
        return itemView.findViewById(id)
    }

    /**
     * on bind data
     *
     * @param data
     */
    protected open fun onBindViewHolder(data: T) {}

    /**
     * on bind data
     *
     * @param data
     */
    protected open fun onBindViewHolder(data: T, payloads: MutableList<Any>?) {}

    /*package*/
    fun bindViewHolder(data: Any?, payloads: MutableList<Any>?) {
        this.data = data as T
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(data)
        } else {
            onBindViewHolder(data, payloads)
        }
    }

    protected fun refresh() {
        itemView.removeCallbacks(refreshTask)
        itemView.post(refreshTask)
    }

    /**
     * is clickable, if true item click and long click is delegated
     *
     * @return
     */
    val isClickable: Boolean
        get() = true
    val isFirstItem: Boolean
        get() = adapterPosition == 0
    val isLastItem: Boolean
        get() = adapterPosition == adapter!!.itemCount - 1

}