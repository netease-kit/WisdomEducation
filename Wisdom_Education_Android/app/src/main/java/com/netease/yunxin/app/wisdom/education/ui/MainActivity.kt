/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.education.ui

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.netease.yunxin.app.wisdom.base.util.StatusBarUtil
import com.netease.yunxin.app.wisdom.education.R
import com.netease.yunxin.app.wisdom.education.ui.main.MainFragment
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_EduApp)
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
        StatusBarUtil.hideStatusBar(window, true)
        requestPermissions()
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    private fun requestPermissions() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String?>? ->
                scope.showRequestReasonDialog(
                    deniedList,
                    resources.getString(R.string.request_permission_reason),
                    resources.getString(R.string.i_have_understood)
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String?>? ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    resources.getString(R.string.settings_to_manually_enable_permissions),
                    resources.getString(R.string.i_have_understood)
                )
            }
            .request { allGranted: Boolean, _: List<String?>?, deniedList: List<String?> ->
                if (!allGranted) {
                    Toast.makeText(this@MainActivity,
                        resources.getString(R.string.not_granted_permissions, deniedList),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}