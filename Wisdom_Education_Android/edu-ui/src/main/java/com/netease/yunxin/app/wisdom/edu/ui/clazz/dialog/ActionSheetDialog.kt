/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.rvadapter.BaseDelegate
import com.netease.yunxin.app.wisdom.rvadapter.BaseViewHolder
import com.netease.yunxin.app.wisdom.rvadapter.OnItemClickListener

/**
 *
 */
open class ActionSheetDialog : BottomSheetDialog {
    class ActionItem @JvmOverloads constructor(var action: Int, var text: String, var textColor: Int = DEFAULT_COLOR) {
        var attach: Any? = null

        companion object {
            val DEFAULT_COLOR = R.color.black
            const val TITLE_ACTION = -1
            const val CANCEL_ACTION = -2
        }
    }

    constructor(context: Context) : super(context, R.style.ActionSheetDialogStyle)
    constructor(context: Context, theme: Int) : super(context, theme)
    constructor(
        context: Context, cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?,
    ) : super(context, cancelable, cancelListener)

    private var actions: MutableList<ActionItem> = mutableListOf()
    private var recyclerView: RecyclerView? = null
    private var llAction: LinearLayout? = null
    private var listener: OnItemClickListener<ActionItem>? = null
    fun setTitle(title: String) {
        addAction(ActionItem(ActionItem.TITLE_ACTION, title))
    }

    fun clearActions() {
        actions.clear()
    }

    fun addAction(action: Int, title: String) {
        addAction(ActionItem(action, title))
    }


    fun addAction(action: Int, title: String, attach: String?) {
        val item = ActionItem(action, title)
        item.attach = attach
        addAction(item)
    }

    fun appendAction(action: Int, title: String, attach: String?) {
        val item = ActionItem(action, title)
        item.attach = attach
        var find = false
        for (sub in this.actions) {
            if (sub.action == item.action) {
                sub.text = item.text
                sub.attach = item.attach
                find = true
                break
            }
        }
        if (!find) {
            actions.add(item)
        }
    }

    fun addAction(action: Int, title: String, color: Int) {
        addAction(ActionItem(action, title, color))
    }

    private fun addAction(item: ActionItem) {
        actions.add(item)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<ActionItem>) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.action_sheet)
        findViews()
        initData()
        super.onCreate(savedInstanceState)
    }

    private fun findViews() {
        recyclerView = findViewById(R.id.action_list)
        llAction = findViewById(R.id.ll_action)
    }

    private fun initData() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        addCancelAction()
        val adapter: BaseAdapter<ActionItem> = BaseAdapter(actions, listener)
        adapter.setDelegate(object : BaseDelegate<ActionItem>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*> {
                when (viewType) {
                    0 -> return TitleViewHolder(parent)
                    1 -> return CancelViewHolder(parent)
                }
                return ActionViewHolder(parent)
            }

            override fun getItemViewType(data: ActionItem, pos: Int): Int {
                if (data.action == ActionItem.TITLE_ACTION) {
                    return 0
                } else if (data.action == ActionItem.CANCEL_ACTION) {
                    return 1
                }
                return 2
            }
        })
        recyclerView!!.adapter = adapter
        llAction?.setOnClickListener { dismiss() }
    }

    /**
     * 是否包含过量的action，ui适配
     */
    var overCapacity: Boolean = false


    private fun addCancelAction() {
        for (item in actions) {
            if (item.action == ActionItem.CANCEL_ACTION) {
                actions.remove(item)
                break
            }
        }
        if (!overCapacity) {
            // move 2 last
            addAction(ActionItem(ActionItem.CANCEL_ACTION, context.getString(R.string.cancel)))
        }
    }

    private inner class ActionViewHolder
    /**
     * single view may be direct construction, eg: TextView view = new TextView(context);
     *
     * @param parent current no use, may be future use
     */
        (parent: ViewGroup?) : BaseViewHolder<ActionItem?>(
        parent!!, R.layout.action_sheet_item
    ) {
        private var actionItem: TextView? = null
        private var splitLine: View? = null
        override fun findViews() {
            actionItem = itemView.findViewById(R.id.action_text)
            splitLine = itemView.findViewById(R.id.split_line)
        }

        override fun onBindViewHolder(data: ActionItem?) {
            if (isFirstItem) {
                if (adapter!!.itemCount == 2) { // 只有当前和cancel
                    actionItem!!.setBackgroundResource(R.drawable.white_round_box_13dp_shape_selector)
                    splitLine!!.visibility = View.GONE
                } else {
                    actionItem!!.setBackgroundResource(R.drawable.white_top_round_box_13dp_shape_selector)
                }
            } else {
                splitLine!!.visibility = View.VISIBLE
                if (adapterPosition == adapter!!.itemCount - 2 && !overCapacity) { // 除了cancel的最后一条
                    actionItem!!.setBackgroundResource(R.drawable.white_bottom_round_box_13dp_shape_selector)
                } else {
                    actionItem!!.setBackgroundResource(R.drawable.white_middle_item_selector)
                }
            }
            actionItem!!.setTextColor(itemView.context.resources.getColor(data!!.textColor))
            actionItem!!.text = data.text
        }
    }

    private inner class TitleViewHolder
    /**
     * single view may be direct construction, eg: TextView view = new TextView(context);
     *
     * @param parent current no use, may be future use
     */
        (parent: ViewGroup?) : BaseViewHolder<ActionItem?>(
        parent!!, R.layout.action_sheet_title
    ) {
        private var actionItem: TextView? = null
        override fun findViews() {
            actionItem = itemView.findViewById(R.id.action_text)
        }

        override fun onBindViewHolder(data: ActionItem?) {
            actionItem!!.text = data!!.text
        }
    }

    private inner class CancelViewHolder
    /**
     * single view may be direct construction, eg: TextView view = new TextView(context);
     *
     * @param parent current no use, may be future use
     */
        (parent: ViewGroup?) : BaseViewHolder<ActionItem?>(
        parent!!, R.layout.action_sheet_cancel
    ) {
        private var actionItem: TextView? = null
        override fun findViews() {
            actionItem = itemView.findViewById(R.id.action_text)
        }

        override fun onBindViewHolder(data: ActionItem?) {
            actionItem!!.text = data!!.text
        }
    }

    override fun show() {
        super.show()
        if (overCapacity) setCanceledOnTouchOutside(true)
        addCancelAction()
        recyclerView!!.adapter!!.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        // for landscape mode
        val behavior: BottomSheetBehavior<*> = behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}