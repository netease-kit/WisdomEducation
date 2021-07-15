/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.base.util

import android.annotation.TargetApi
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.*

object BitmapDecoder {
    private fun pickReqBoundWithRatio(bound: IntArray, reqBounds: Array<IntArray>, ratio: Float): IntArray {
        val hRatio: Float = if (bound[1] == 0) 0F else bound[0].toFloat() / bound[1].toFloat()
        val vRatio: Float = if (bound[0] == 0) 0F else bound[1].toFloat() / bound[0].toFloat()
        return if (hRatio >= ratio) {
            reqBounds[0]
        } else if (vRatio >= ratio) {
            reqBounds[1]
        } else {
            reqBounds[2]
        }
    }

    fun decodeSampledForDisplay(pathName: String?): Bitmap? {
        return decodeSampledForDisplay(pathName, true)
    }

    fun decodeSampledForDisplay(pathName: String?, withTextureLimit: Boolean): Bitmap? {
        val ratio: Float = ImageUtil.MAX_IMAGE_RATIO
        val reqBounds = arrayOf(
            intArrayOf(ScreenUtil.screenWidth * 2, ScreenUtil.screenHeight),
            intArrayOf(ScreenUtil.screenWidth, ScreenUtil.screenHeight * 2),
            intArrayOf(
                (ScreenUtil.screenWidth * 1.414).toInt(),
                (ScreenUtil.screenHeight * 1.414).toInt()
            )
        )
        // decode bound
        val bound = decodeBound(pathName)
        // pick request bound
        val reqBound: IntArray = pickReqBoundWithRatio(bound!!, reqBounds, ratio)
        val width = bound!![0]
        val height = bound[1]
        val reqWidth = reqBound[0]
        val reqHeight = reqBound[1]
        // calculate sample size
        var sampleSize: Int = SampleSizeUtil.calculateSampleSize(width, height, reqWidth, reqHeight)
        if (withTextureLimit) {
            // adjust sample size
            sampleSize = SampleSizeUtil.adjustSampleSizeWithTexture(sampleSize, width, height)
        }
        var RETRY_LIMIT = 5
        var bitmap: Bitmap? = BitmapDecoder.decodeSampled(pathName, sampleSize)
        while (bitmap == null && RETRY_LIMIT > 0) {
            sampleSize++
            RETRY_LIMIT--
            bitmap = BitmapDecoder.decodeSampled(pathName, sampleSize)
        }
        return bitmap
    }


    fun decodeBound(pathName: String?): IntArray? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        return intArrayOf(options.outWidth, options.outHeight)
    }

    fun decodeBound(res: Resources?, resId: Int): IntArray? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)
        return intArrayOf(options.outWidth, options.outHeight)
    }

    fun decodeBound(file: File?): IntArray {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            return decodeBound(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return intArrayOf(0, 0)
    }

    fun decodeBound(inputStream: InputStream?): IntArray {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)
        return intArrayOf(options.outWidth, options.outHeight)
    }

    fun decodeSampled(resources: Resources?, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap? {
        return decodeSampled(resources, resId, getSampleSize(resources, resId, reqWidth, reqHeight))
    }

    fun getSampleSize(`is`: String?, reqWidth: Int, reqHeight: Int): Int {
        // decode bound
        val bound = decodeBound(`is`)
        // calculate sample size
        return SampleSizeUtil.calculateSampleSize(bound!![0], bound[1], reqWidth, reqHeight)
    }

    fun getSampleSize(resources: Resources?, resId: Int, reqWidth: Int, reqHeight: Int): Int {
        // decode bound
        val bound = decodeBound(resources, resId)
        // calculate sample size
        return SampleSizeUtil.calculateSampleSize(bound!![0], bound[1], reqWidth, reqHeight)
    }

    fun getSampleSize(`is`: InputStream?, reqWidth: Int, reqHeight: Int): Int {
        // decode bound
        val bound = decodeBound(`is`)
        // calculate sample size
        return SampleSizeUtil.calculateSampleSize(bound[0], bound[1], reqWidth, reqHeight)
    }

    fun decodeSampled(pathName: String?, reqWidth: Int, reqHeight: Int): Bitmap? {
        return decodeSampled(pathName, getSampleSize(pathName, reqWidth, reqHeight))
    }

    fun decodeSampled(`is`: InputStream?, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        // RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565
        // sample size
        options.inSampleSize = getSampleSize(`is`, reqWidth, reqHeight)
        try {
            return BitmapFactory.decodeStream(`is`, null, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        return null
    }


    fun decodeSampled(res: Resources?, resId: Int, sampleSize: Int): Bitmap? {
        val options = BitmapFactory.Options()
        // RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565
        // sample size
        options.inSampleSize = sampleSize
        try {
            return BitmapFactory.decodeResource(res, resId, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        return null
    }

    fun decodeSampled(pathName: String?, sampleSize: Int): Bitmap? {
        val options = BitmapFactory.Options()
        // RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565
        // sample size
        options.inSampleSize = sampleSize
        var bitmap: Bitmap? = null
        bitmap = try {
            BitmapFactory.decodeFile(pathName, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }
        return if(bitmap != null && pathName != null) {
            checkInBitmap(bitmap, options, pathName)
        } else {
            null
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun checkInBitmap(bitmap: Bitmap, options: BitmapFactory.Options, path: String): Bitmap? {
        var bitmap: Bitmap? = bitmap
        val honeycomb = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
        if (honeycomb && bitmap != options.inBitmap && options.inBitmap != null) {
            options.inBitmap.recycle()
            options.inBitmap = null
        }
        if (bitmap == null) {
            try {
                bitmap = BitmapFactory.decodeFile(path, options)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }
        }
        return bitmap
    }
}