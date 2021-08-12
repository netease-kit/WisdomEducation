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
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.throttleFirst
import com.netease.yunxin.app.wisdom.base.util.NetworkUtil
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.base.util.ToastUtil
import com.netease.yunxin.app.wisdom.base.util.observeOnce
import com.netease.yunxin.app.wisdom.edu.logic.NEEduErrorCode
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduSceneType
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding
import com.netease.yunxin.app.wisdom.education.BuildConfig
import com.netease.yunxin.app.wisdom.education.R
import com.netease.yunxin.app.wisdom.education.databinding.MainFragmentBinding
import com.netease.yunxin.app.wisdom.education.ui.MainActivity
import com.netease.yunxin.kit.alog.ALog
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
        binding.radioGroupRole.clearCheck()

        binding.etRoomId.addTextChangedListener(onTextChangedListener)
        binding.etNickName.addTextChangedListener(onTextChangedListener)
        binding.etSceneType.addTextChangedListener(onTextChangedListener)
        binding.radioGroupRole.setOnCheckedChangeListener { _, _ -> switchJoinBtn() }
        if (BuildConfig.ENV == "test") {
            binding.tips.text = "${context?.getString(R.string.app_tips)}(test)"
            binding.etUser.visibility = View.VISIBLE
            binding.etToken.visibility = View.VISIBLE
            // 以下代码用于演示IM复用
            binding.tvSetting.visibility = View.VISIBLE
            binding.tvSetting.setOnClickListener(onClickListener)
        }
    }

    private fun checkInputLegal(): Boolean {
        if (TextUtils.isEmpty(binding.etRoomId.text)) {
            return false
        }
        if (TextUtils.isEmpty(binding.etNickName.text)) {
            return false
        }
        if (TextUtils.isEmpty(binding.etSceneType.text)) {
            return false
        }
        if (!binding.rbTeacher.isChecked && !binding.rbStudent.isChecked) {
            return false
        }
        return true
    }

    private val initObserver = { t: NEResult<NEEduUiKit> ->
        if (!t.success()) {
            starting = false
            htLoading?.dismiss()
            ALog.i("init failed, code ${t.code}")
            if (t.code == NEEduErrorCode.IM_LOGIN_ERROR.code && PreferenceUtil.reuseIM) {
                ToastUtil.showLong(R.string.should_login_im)
            } else {
                var tip = context?.let { NEEduErrorCode.tipsWithErrorCode(it, t.code) }
                if (!TextUtils.isEmpty(tip)) {
                    ToastUtil.showLong(tip!!)
                } else {
                    ToastUtil.showLong(getString(R.string.join_class_fail_try_again))// show error whit error code
                }
            }
        } else {
            enterClassroom(t.data!!, classOptions)
        }
    }

    /**
     * 加入课堂
     *
     */
    private fun joinClass() {
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
        val uuid: String = binding.etUser.text.toString().trim()
        val token: String = binding.etToken.text.toString().trim()
        val classId: String = binding.etRoomId.text.toString()
        val nickname: String = binding.etNickName.text.toString()
        val className: String = getString(R.string.clazz_name, nickname)
        var roleType = getRoleType()
        if (sceneType!! == NEEduSceneType.BIG && getRoleType() == NEEduRoleType.BROADCASTER) {
            roleType = NEEduRoleType.AUDIENCE
        }
        classOptions = NEEduClassOptions(classId, className, nickname, sceneType!!, roleType)
        NEEduUiKit.init(uuid, token).observeOnce(viewLifecycleOwner, initObserver)
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
                t.code == NEEduErrorCode.ROOM_MEMBER_EXIST.code -> {
                    ToastUtil.showShort(R.string.room_member_exist)
                }
                else -> {
                    ToastUtil.showShort(getString(R.string.join_class_fail_try_again))
                }
            }
        })
    }

    private fun clearInput() {
        binding.etUser.text?.clear()
        binding.etToken.text?.clear()
        binding.etRoomId.text?.clear()
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
                binding.cardRoomType.let {
                    if (it.visibility == View.GONE) showCardRoomType() else hideCardRoomType()
                }
            }
            binding.tvSetting -> {
                (activity as MainActivity).showSettingFragment()
            }
        }
    }.throttleFirst()

    private val onTextChangedListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            switchJoinBtn()
        }
    }

    private fun switchJoinBtn() {
        binding.btnJoin.isEnabled = checkInputLegal()
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