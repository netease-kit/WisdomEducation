/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.education.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.util.NetworkUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.edu.logic.NEEduErrorCode
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduSceneType
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding
import com.netease.yunxin.app.wisdom.education.BuildConfig
import com.netease.yunxin.app.wisdom.education.R
import com.netease.yunxin.app.wisdom.education.databinding.MainFragmentBinding
import com.superlht.htloading.view.HTLoading

class MainFragment : Fragment(R.layout.main_fragment) {
    private val binding: MainFragmentBinding by viewBinding()
    private var starting = false
    private var sceneType: NEEduSceneType? = null
    private var htLoading: HTLoading? = null

    private lateinit var classOptions: NEEduClassOptions

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etSceneType.setOnClickListener(onClickListener)
        binding.tvOne2one.setOnClickListener(onClickListener)
        binding.tvSmallClass.setOnClickListener(onClickListener)
        binding.tvLargeClass.setOnClickListener(onClickListener)
        binding.btnJoin.setOnClickListener(onClickListener)
        binding.tvBack.setOnClickListener(onClickListener)
        binding.radioGroupRole.clearCheck()

        binding.etRoomId.addTextChangedListener(onTextChangedListener)
        binding.etClassName.addTextChangedListener(onTextChangedListener)
        binding.etNickName.addTextChangedListener(onTextChangedListener)
        binding.etSceneType.addTextChangedListener(onTextChangedListener)
        binding.radioGroupRole.setOnCheckedChangeListener { _, _ -> switchJoinBtn() }
        if (BuildConfig.ENV == "test") {
            binding.tips.text = "${context?.getString(R.string.app_tips)}(test)"
        }
    }

    private fun checkInputLegal(showToast: Boolean): Boolean {
        val roomIdStr: String = binding.etRoomId.text.toString()
        if (TextUtils.isEmpty(roomIdStr)) {
            if (showToast) ToastUtil.showShort(R.string.room_id_should_not_be_empty)
            return false
        }
        val roomNameStr: String = binding.etClassName.text.toString()
        if (TextUtils.isEmpty(roomNameStr)) {
            if (showToast) ToastUtil.showShort(R.string.room_name_should_not_be_empty)
            return false
        }
        val yourNameStr: String = binding.etNickName.text.toString()
        if (TextUtils.isEmpty(yourNameStr)) {
            if (showToast) ToastUtil.showShort(R.string.your_name_should_not_be_empty)
            return false
        }
        val roomTypeStr: String = binding.etSceneType.text.toString()
        if (TextUtils.isEmpty(roomTypeStr)) {
            if (showToast) ToastUtil.showShort(R.string.room_type_should_not_be_empty)
            return false
        }
        if (!binding.rbTeacher.isChecked && !binding.rbStudent.isChecked) {
            if (showToast) ToastUtil.showShort(R.string.identity_not_selected)
            return false
        }
        return true
    }

    private val initObserver: (t: NEResult<NEEduUiKit>) -> Unit = { t ->
        if (!t.success()) {
            starting = false
            htLoading?.dismiss()
            ToastUtil.showShort(getString(R.string.join_class_fail_try_again))// show error whit error code
        } else {
            enterClassroom(t.data!!, classOptions)
        }
    }

    /**
     * 加入课堂
     *
     */
    private fun joinClass() {
        if (!checkInputLegal(true)) {
            return
        }
        if (starting) {
            ToastUtil.showShort(R.string.entering_class)
            return
        }
        if (!NetworkUtil.isNetAvailable(requireActivity())) {
            ToastUtil.showShort(getString(R.string.network_is_not_available))
            return
        }
        starting = true
        htLoading = HTLoading(requireActivity())
        htLoading?.show()
        val classId: String = binding.etRoomId.text.toString()
        val className: String = binding.etClassName.text.toString()
        val nickname: String = binding.etNickName.text.toString()

        var roleType = getRoleType()
        if (sceneType!! == NEEduSceneType.BIG && getRoleType() == NEEduRoleType.BROADCASTER) {
            roleType = NEEduRoleType.AUDIENCE
        }
        classOptions = NEEduClassOptions(classId, className, nickname, sceneType!!, roleType)
        NEEduUiKit.init().observe(viewLifecycleOwner, initObserver)
    }

    private fun enterClassroom(eduUiKit: NEEduUiKit, neEduClassOptions: NEEduClassOptions) {
        eduUiKit.enterClass(requireActivity(), neEduClassOptions).observe(viewLifecycleOwner, { t ->
            starting = false
            htLoading?.dismiss()
            when {
                t.success() -> {
                    clearInput()
                }
                t.code == NEEduErrorCode.ROOM_ROLE_EXCEED.code -> {
                    ToastUtil.showShort(
                        if (neEduClassOptions.roleType == NEEduRoleType.HOST) getString(
                            R.string
                                .teacher_over_limit
                        ) else getString(
                            R.string
                                .student_over_limit
                        )
                    )
                }
                else -> {
                    ToastUtil.showShort(getString(R.string.join_class_fail_try_again))
                }
            }
        })
    }

    private fun clearInput() {
        binding.etRoomId.text?.clear()
        binding.etClassName.text?.clear()
        binding.etNickName.text?.clear()
        binding.etSceneType.text?.clear()
        binding.radioGroupRole.clearCheck()
    }

    private var onClickListener = View.OnClickListener { v ->
        when (v) {
            binding.btnJoin -> {
                activity?.let {
                    joinClass()
                }
            }
            binding.tvOne2one -> {
                sceneType = NEEduSceneType.ONE_TO_ONE
                binding.etSceneType.setText(R.string.one2one_class)
                hideCardRoomType()
            }
            binding.tvSmallClass -> {
                sceneType = NEEduSceneType.SMALL
                binding.etSceneType.setText(R.string.small_class)
                hideCardRoomType()
            }
            binding.tvLargeClass -> {
                sceneType = NEEduSceneType.BIG
                binding.etSceneType.setText(R.string.big_class)
                hideCardRoomType()
            }
            binding.etSceneType -> {
                binding.cardRoomType.let { it ->
                    if (it.visibility == View.GONE) showCardRoomType() else hideCardRoomType()
                }
            }
            binding.tvBack -> {
                requireActivity().finish()
            }
        }
    }

    private val onTextChangedListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            switchJoinBtn()
        }
    }

    private fun switchJoinBtn() {
        binding.btnJoin.isSelected = checkInputLegal(false)
    }

    private fun showCardRoomType() {
        binding.cardRoomType.visibility = View.VISIBLE
        binding.ivRoomType.setImageResource(R.drawable.ic_arrow_up)
    }

    private fun hideCardRoomType() {
        binding.cardRoomType.visibility = View.GONE
        binding.ivRoomType.setImageResource(R.drawable.ic_arrow_down)
    }

    private fun getRoleType(): NEEduRoleType {
        return if (binding.rbTeacher.isChecked) NEEduRoleType.HOST else NEEduRoleType.BROADCASTER
    }
}