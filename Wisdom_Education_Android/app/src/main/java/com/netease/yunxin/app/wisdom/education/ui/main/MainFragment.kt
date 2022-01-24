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
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduConfig
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHttpCode
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduResource
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduSceneType
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit
import com.netease.yunxin.app.wisdom.education.BuildConfig
import com.netease.yunxin.app.wisdom.education.R
import com.netease.yunxin.app.wisdom.education.databinding.MainFragmentBinding
import com.netease.yunxin.app.wisdom.education.ui.MainActivity
import com.netease.yunxin.app.wisdom.education.ui.dialog.LoadingDialog
import com.netease.yunxin.app.wisdom.record.NERecordPlayUiKit
import com.netease.yunxin.app.wisdom.record.NERecordPlayer
import com.netease.yunxin.app.wisdom.record.net.service.RecordPlayRepository
import com.netease.yunxin.app.wisdom.record.ui.NERecordActivity
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding
import com.netease.yunxin.kit.alog.ALog

class MainFragment : Fragment(R.layout.main_fragment) {
    private val binding: MainFragmentBinding by viewBinding()
    private var starting = false
    private var sceneType: NEEduSceneType? = null
    private var loading: LoadingDialog? = null

    private lateinit var classOptions: NEEduClassOptions

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreSavedInstance(savedInstanceState)
        binding.etSceneType.setOnClickListener(onClickListener)
        binding.tvOne2one.setOnClickListener(onClickListener)
        binding.tvSmallClass.setOnClickListener(onClickListener)
        binding.tvLargeClass.setOnClickListener(onClickListener)
        binding.tvCdnBigClass.setOnClickListener(onClickListener)
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

        }
        binding.tvSetting.visibility = View.VISIBLE
        binding.tvSetting.setOnClickListener(onClickListener)
    }

    private fun restoreSavedInstance(savedInstanceState: Bundle?) {
        sceneType = savedInstanceState?.getSerializable("sceneType") as NEEduSceneType?
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("sceneType", sceneType)
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
            loading?.dismiss()
            loading = null
            ALog.i("init failed, code ${t.code}")
            if (t.code == NEEduHttpCode.IM_LOGIN_ERROR.code && PreferenceUtil.reuseIM) {
                ToastUtil.showLong(R.string.should_login_im)
            } else {
                val tip = context?.let { NEEduErrorCode.tipsWithErrorCode(it, t.code) }
                if (!TextUtils.isEmpty(tip)) {
                    ToastUtil.showLong(tip!!)
                } else {
                    ToastUtil.showLong(getString(R.string.join_class_fail_try_again))// prompt the error message with a specified error code
                }
            }
        } else {
            enterClassroom(t.data!!, classOptions)
        }
    }

    /**
     * Join a class
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
        loading = LoadingDialog.show(requireActivity())
        val uuid: String = binding.etUser.text.toString().trim()
        val token: String = binding.etToken.text.toString().trim()
        val classId: String = binding.etRoomId.text.toString()
        val nickname: String = binding.etNickName.text.toString()
        val className: String = getString(R.string.clazz_name, nickname)
        var roleType = getRoleType()
        if (sceneType!! == NEEduSceneType.BIG && getRoleType() == NEEduRoleType.BROADCASTER) {
            roleType = NEEduRoleType.AUDIENCE
        }
        val isLiveClass = sceneType == NEEduSceneType.LIVE_SIMPLE
        classOptions = NEEduClassOptions(
            classId,
            className,
            nickname,
            sceneType!!,
            roleType,
            if (isLiveClass) NEEduConfig(NEEduResource(chatroom = PreferenceUtil.enableChatRoom, live = true, rtc = false))
            else NEEduConfig(NEEduResource(chatroom = PreferenceUtil.enableChatRoom))
        )
        NEEduUiKit.init(uuid, token).observeOnce(viewLifecycleOwner, initObserver)
    }

    private fun enterClassroom(eduUiKit: NEEduUiKit, neEduClassOptions: NEEduClassOptions) {

        eduUiKit.enterClass(requireActivity(), neEduClassOptions).observeOnce(viewLifecycleOwner, { t ->
            loading?.dismiss()
            loading = null
            starting = false
            when {
                t.success() -> {
                    clearInput()
                }
                t.code == NEEduHttpCode.ROOM_ROLE_EXCEED.code -> {
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
                t.code == NEEduHttpCode.ROOM_MEMBER_EXIST.code -> {
                    ToastUtil.showShort(R.string.room_member_exist)
                }
                else -> {
                    val tip = context?.let { NEEduErrorCode.tipsWithErrorCode(it, t.code) }
                    if (!TextUtils.isEmpty(tip)) {
                        ToastUtil.showLong(tip!!)
                    } else {
                        ToastUtil.showLong(getString(R.string.join_class_fail_try_again))// prompt the error message with a specified error code
                    }
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
        switchLiveClass(false)
    }

    /**
     * Recording playback
     *
     */
    private fun recordPlay() {
        if (!NetworkUtil.isNetAvailable(requireActivity())) {
            ToastUtil.showShort(getString(R.string.network_is_not_available))
            return
        }
        starting = true
        loading = LoadingDialog.show(requireActivity())
        RecordPlayRepository.appKey = BuildConfig.APP_KEY
        RecordPlayRepository.baseUrl = BuildConfig.API_BASE_URL
        PreferenceUtil.recordPlay.apply {
            NERecordPlayUiKit.createPlayer(roomUuid = first, rtcCid = second)
                .observeOnce(viewLifecycleOwner, recordObserver)
        }
    }

    private val recordObserver = { t: NEResult<NERecordPlayer> ->
        starting = false
        loading?.dismiss()
        loading = null
        when {
            t.success() -> {
                enterRecordPlay()
            }
            t.code == NEEduHttpCode.NO_CONTENT.code -> {
                ALog.i("create record failed, result $t")
                ToastUtil.showLong(getString(R.string.course_playback_file_is_being_transcoded))
            }
            else -> {
                ALog.i("create record failed, result $t")
                val tip = context?.let { NEEduErrorCode.tipsWithErrorCode(it, t.code) }
                if (!TextUtils.isEmpty(tip)) {
                    ToastUtil.showLong(tip!!)
                } else {
                    ToastUtil.showLong(getString(R.string.open_recordplay_fail_try_again))
                }
            }
        }
    }

    private fun enterRecordPlay() {
        NERecordActivity.start(requireActivity())
    }

    private var onClickListener = View.OnClickListener { v ->
        when (v) {
            binding.btnRecordPlay -> {
                activity?.let {
                    recordPlay()
                }
            }
            binding.btnJoin -> {
                activity?.let {
                    joinClass()
                }
            }
            binding.tvOne2one -> {
                sceneType = NEEduSceneType.ONE_TO_ONE
                binding.etSceneType.setText(R.string.one2one_class)
                hideCardRoomType()
                switchLiveClass(false)
            }
            binding.tvSmallClass -> {
                sceneType = NEEduSceneType.SMALL
                binding.etSceneType.setText(R.string.small_class)
                hideCardRoomType()
                switchLiveClass(false)
            }
            binding.tvLargeClass -> {
                sceneType = NEEduSceneType.BIG
                binding.etSceneType.setText(R.string.interactive_big_class)
                hideCardRoomType()
                switchLiveClass(false)
            }
            binding.tvCdnBigClass -> {
                sceneType = NEEduSceneType.LIVE_SIMPLE
                binding.etSceneType.setText(R.string.live_big_class)
                hideCardRoomType()
                switchLiveClass(true)
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

    private fun switchLiveClass(isCdnClassSelected: Boolean) {
        if(isCdnClassSelected) {
            binding.rbTeacher.visibility = View.GONE
            binding.tvTeacher.visibility = View.GONE
            binding.rbStudent.isChecked = true
        } else {
            binding.rbTeacher.visibility = View.VISIBLE
            binding.tvTeacher.visibility = View.VISIBLE
        }
        switchJoinBtn()
    }

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

    override fun onResume() {
        super.onResume()
        PreferenceUtil.recordPlay.let {
            if (!TextUtils.isEmpty(it.first) && !TextUtils.isEmpty(it.second)) {
                binding.btnRecordPlay.visibility = View.VISIBLE
                binding.btnRecordPlay.setOnClickListener(onClickListener)
            }
        }
    }
}