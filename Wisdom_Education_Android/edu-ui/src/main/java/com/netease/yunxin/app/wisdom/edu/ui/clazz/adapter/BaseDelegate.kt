/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.view.ViewGroup

/**
 */
abstract class BaseDelegate<T> {
    fun createViewHolder(adapter: BaseAdapter<T>?, parent: ViewGroup?, viewType: Int): BaseViewHolder<*>? {
        val vh = onCreateViewHolder(parent, viewType)
        if (vh != null) {
            vh.adapter = adapter
        }
        return vh
    }

    /**
     * crate view holder by view type
     *
     * @param parent
     * @param viewType
     * @return
     */
    abstract fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*>?

    /**
     * get view type by data
     *
     * @param data
     * @param pos
     * @return
     */
    abstract fun getItemViewType(data: T, pos: Int): Int

    fun onDataSetChanged() {}
}