/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    /**
     * Check whether SDCard is available
     */
    fun existSDCard(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * Create a file based on the system time stamp, prefix, and suffix
     */
    fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) {
            folder.mkdirs()
        }
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
    }

    fun filePathFromUri(context: Context, uri: Uri): String? {
        var path = uri.path
        if (path != null && File(path).exists()) {
            return path
        }
        return try {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                cursor.getString(cursor.getColumnIndex("_data")) // File path
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}