/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.rvadapter

import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 */
open class BaseAdapter<T> @JvmOverloads constructor(
    dataSourceList: MutableList<T>,
    listener: OnItemClickListener<T>? = null,
) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {
    /**
     * data source
     */
    private var dataList: MutableList<T> = mutableListOf()

    /**
     * onClick onLongClick callback
     */
    private var listener: OnItemClickListener<*>?

    /**
     * constructor view holder delegate
     */
    private var delegate: BaseDelegate<T>? = null


    /**
     * onClick item child callback
     */
    private var mOnItemChildClickListener: OnItemChildClickListener<T>? = null

    init {
        dataList.addAll(dataSourceList)
    }

    /**
     * just is empty
     *
     * @param delegate
     */
    fun setDelegate(delegate: BaseDelegate<T>?) {
        this.delegate = delegate
    }

    /**
     * just is empty
     *
     * @param dataList
     */
    private fun checkData(dataList: MutableList<T>) {
        var dataList: MutableList<T>? = dataList
        if (dataList == null) {
            dataList = ArrayList(0)
        }
        this.dataList = dataList
    }

    /**
     * set onclick & onLongClick callback
     *
     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener<*>?) {
        this.listener = listener
    }

    /**
     * create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val viewHolder = delegate!!.createViewHolder(this, parent, viewType)
        viewHolder!!.findViews()
        return viewHolder
    }

    /**
     * bind view holder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        holder.bindViewHolder(dataList[position], null)
        listenClick(holder)
    }

    /**
     * bind view holder
     *
     * @param holder
     * @param position
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full
     *                 update.
     */
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, payloads: MutableList<Any>) {
        holder.bindViewHolder(dataList[position], payloads)
        listenClick(holder)
    }

    private fun listenClick(holder: BaseViewHolder<*>) {
        if (listener != null && holder.isClickable) {
            holder.itemView.setOnClickListener(mClickListenerMediator)
            holder.itemView.setOnLongClickListener(mLongClickListenerMediator)
        }
    }

    fun updateDataAndNotify(list: List<T>?) {
        dataList.clear()
        if (list != null && list.isNotEmpty()) {
            dataList.addAll(list)
        }
        delegate!!.onDataSetChanged()
        notifyDataSetChanged()
    }

    fun resetData(list: List<T>?, isNotify: Boolean? = true) {
        dataList.clear()
        if (list != null && list.isNotEmpty()) {
            dataList.addAll(list)
        }
        if(isNotify == true) {
            notifyDataSetChanged()
        }
    }

    fun appendDataAndNotify(t: T?) {
        if (t == null) {
            return
        }
        dataList.add(t)
        delegate!!.onDataSetChanged()
        notifyDataSetChanged()
    }

    fun insertHeadDataAndNotify(t: T?) {
        if (t == null) {
            return
        }
        dataList.add(0, t)
        delegate!!.onDataSetChanged()
        notifyDataSetChanged()
    }

    fun appendDataAndNotify(list: List<T>?) {
        dataList.addAll(list!!)
        delegate!!.onDataSetChanged()
        notifyDataSetChanged()
    }

    fun <X>refreshDataAndNotify(t: T?, payload: X? = null) {
        t?.let{
            for (i in dataList.indices) {
                val element = dataList[i]
                if (element == t) {
                    dataList[i] = t
                    notifyItemChanged(i, payload)
                    break
                }
            }
        }
    }

    fun <X>refreshDataAndNotify(t: T?, payload: X? = null, compare: (T, T?) -> Boolean) {
        t?.let{
            for (i in dataList.indices) {
                val element = dataList[i]
                if (compare(element, t)) {
                    dataList[i] = t
                    notifyItemChanged(i, payload)
                    break
                }
            }
        }
    }

    fun containsData(t: T?): Boolean {
        return dataList.contains(t)
    }

    val isEmpty: Boolean
        get() = dataList.size == 0

    /**
     * get item count
     *
     * @return
     */
    override fun getItemCount(): Int {
        return dataList.size
    }

    /**
     * get item
     *
     * @return
     */
    fun getItem(position: Int): T {
        return dataList[position]
    }

    /**
     * get item view type
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return delegate!!.getItemViewType(dataList[position], position)
    }

    private val mClickListenerMediator = View.OnClickListener { v ->
        if (listener != null) {
            val pos = getViewHolderAdapterPosition(v)
            if (pos < 0) {
                return@OnClickListener
            }
            listener.onClick(v, pos, getData(pos)!!)
        }
    }
    private val mLongClickListenerMediator = OnLongClickListener { v ->
        if (listener != null) {
            val pos = getViewHolderAdapterPosition(v)
            return@OnLongClickListener if (pos < 0) {
                false
            } else listener.onLongClick(v, pos, getData(pos)!!)
        }
        false
    }

    private fun getData(pos: Int): T? {
        return if (pos >= 0) dataList[pos] else null
    }

    fun removeItem(position: Int) {
        dataList.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        delegate!!.onDataSetChanged()
        notifyItemRemoved(position)
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        super.onViewRecycled(holder)
        if (holder is RecyclerView.RecyclerListener) {
            (holder as RecyclerView.RecyclerListener).onViewRecycled(holder)
        }
    }

    val lastData: T?
        get() = if (dataList != null && dataList.size > 0) {
            dataList[dataList.size - 1]
        } else null

    companion object {
        fun getViewHolderAdapterPosition(v: View?): Int {
            if (v != null) {
                val parent = v.parent
                if (parent is RecyclerView) {
                    return parent.getChildAdapterPosition(v)
                }
            }
            return -1
        }
    }

    init {
        checkData(dataList)
        this.listener = listener
    }

    /**
     * 用于保存需要设置点击事件的 item
     */
    private val childClickViewIds = LinkedHashSet<Int>()

    private fun getChildClickViewIds(): LinkedHashSet<Int> {
        return childClickViewIds
    }

    /**
     * 设置需要点击事件的子view
     * @param viewIds IntArray
     */
    fun addChildClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childClickViewIds.add(viewId)
        }
    }

    fun setOnItemChildClickListener(listener: OnItemChildClickListener<T>?) {
        this.mOnItemChildClickListener = listener
    }

    /**
     * 绑定 item 点击事件
     * @param viewHolder VH
     */
    fun bindViewClickListener(viewHolder: RecyclerView.ViewHolder, viewType: Int) {
        mOnItemChildClickListener?.let {
            for (id in getChildClickViewIds()) {
                viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                    if (!childView.isClickable) {
                        childView.isClickable = true
                    }
                    childView.setOnClickListener { v ->
                        val position = viewHolder.adapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnClickListener
                        }
                        setOnItemChildClick(v, position)
                    }
                }
            }
        }
    }

    private fun setOnItemChildClick(v: View, position: Int) {
        mOnItemChildClickListener?.onItemChildClick(this, v, position)
    }

    interface OnItemChildClickListener<T> {
        /**
         * callback method to be invoked when an item child in this view has been click
         * @param adapter  BaseAdapter
         * @param view     The view whihin the ItemView that was clicked
         * @param position The position of the view int the adapter
         */
        fun onItemChildClick(adapter: BaseAdapter<T>?, view: View?, position: Int)
    }
}