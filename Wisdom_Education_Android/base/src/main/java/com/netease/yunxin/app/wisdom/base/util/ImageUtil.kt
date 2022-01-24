/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.base.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.text.TextUtils
import java.io.*
import java.util.*
import kotlin.math.sqrt

object ImageUtil {

    class ImageSize(width: Int, height: Int) {
        var width = 0
        var height = 0

        init {
            this.width = width
            this.height = height
        }
    }

    const val MAX_IMAGE_RATIO = 5f

//    fun getDefaultBitmapWhenGetFail(context: Context): Bitmap? {
//        return try {
//            getBitmapImmutableCopy(context.getResources(), R.drawable.nim_image_download_failed)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    fun getBitmapImmutableCopy(res: Resources, id: Int): Bitmap {
        return getBitmap(res.getDrawable(id))!!.copy(Bitmap.Config.RGB_565, false)
    }

    fun getBitmap(dr: Drawable?): Bitmap? {
        if (dr == null) {
            return null
        }
        return if (dr is BitmapDrawable) {
            dr.bitmap
        } else null
    }

    fun rotateBitmapInNeeded(path: String?, srcBitmap: Bitmap?): Bitmap? {
        if (TextUtils.isEmpty(path) || srcBitmap == null) {
            return null
        }
        val localExifInterface: ExifInterface
        return try {
            localExifInterface = ExifInterface(path!!)
            val rotateInt = localExifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val rotate = getImageRotate(rotateInt)
            if (rotate != 0f) {
                val matrix = Matrix()
                matrix.postRotate(rotate)
                val dstBitmap = Bitmap.createBitmap(
                    srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height,
                    matrix, false
                )
                if (dstBitmap == null) {
                    srcBitmap
                } else {
                    if (srcBitmap != null && !srcBitmap.isRecycled) {
                        srcBitmap.recycle()
                    }
                    dstBitmap
                }
            } else {
                srcBitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
            srcBitmap
        }
    }

    /**
     * Get the rotary angle
     *
     * @param rotate
     * @return
     */
    private fun getImageRotate(rotate: Int): Float {
        val f: Float = when (rotate) {
            6 -> {
                90.0f
            }
            3 -> {
                180.0f
            }
            8 -> {
                270.0f
            }
            else -> {
                0.0f
            }
        }
        return f
    }

//    fun makeThumbnail(imageFile: File): String? {
//        val thumbFilePath: String = StorageUtil.getWritePath(imageFile.name, StorageType.TYPE_THUMB_IMAGE)
//        val thumbFile: File = AttachmentStore.create(thumbFilePath) ?: return null
//        val result = scaleThumbnail(
//            imageFile, thumbFile, MsgViewHolderThumbBase.getImageMaxEdge(),
//            MsgViewHolderThumbBase.getImageMinEdge(), CompressFormat.JPEG, 60
//        )
//        if (!result) {
//            AttachmentStore.delete(thumbFilePath)
//            return null
//        }
//        return thumbFilePath
//    }

    fun scaleThumbnail(
        srcFile: File, dstFile: File?, dstMaxWH: Int, dstMinWH: Int,
        compressFormat: CompressFormat?, quality: Int
    ): Boolean {
        var bRet = false
        var srcBitmap: Bitmap? = null
        var dstBitmap: Bitmap? = null
        var bos: BufferedOutputStream? = null
        try {
            val bound = BitmapDecoder.decodeBound(srcFile)
            val size = getThumbnailDisplaySize(
                bound[0].toFloat(),
                bound[1].toFloat(), dstMaxWH.toFloat(), dstMinWH.toFloat()
            )
            srcBitmap = BitmapDecoder.decodeSampled(srcFile.path, size.width, size.height)
            // Rotation
            val localExifInterface = ExifInterface(srcFile.absolutePath)
            val rotateInt = localExifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val rotate = getImageRotate(rotateInt)
            val matrix = Matrix()
            matrix.postRotate(rotate)
            var inSampleSize = 1f
            if (srcBitmap!!.width >= dstMinWH && srcBitmap.height <= dstMaxWH && srcBitmap.width >= dstMinWH && srcBitmap.height <= dstMaxWH) {
                //If the size of srcBitmap meets the requirements in the first round, no scaling is required
            } else {
                if (srcBitmap.width != size.width || srcBitmap.height != size.height) {
                    val widthScale = size.width.toFloat() / srcBitmap.width.toFloat()
                    val heightScale = size.height.toFloat() / srcBitmap.height.toFloat()
                    if (widthScale >= heightScale) {
                        size.width = srcBitmap.width
                        size.height /= widthScale.toInt() // Must be smaller than srcBitmap.getHeight()
                        inSampleSize = widthScale
                    } else {
                        size.width /= heightScale.toInt() //Must be smaller than srcBitmap.getWidth()
                        size.height = srcBitmap.height
                        inSampleSize = heightScale
                    }
                }
            }
            matrix.postScale(inSampleSize, inSampleSize)
            dstBitmap = if (rotate == 0f && inSampleSize == 1f) {
                srcBitmap
            } else {
                Bitmap.createBitmap(srcBitmap, 0, 0, size.width, size.height, matrix, true)
            }
            bos = BufferedOutputStream(FileOutputStream(dstFile))
            dstBitmap!!.compress(compressFormat, quality, bos)
            bos.flush()
            bRet = true
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (srcBitmap != null && !srcBitmap.isRecycled) {
                srcBitmap.recycle()
                srcBitmap = null
            }
            if (dstBitmap != null && !dstBitmap.isRecycled) {
                dstBitmap.recycle()
                dstBitmap = null
            }
        }
        return bRet
    }

    fun getThumbnailDisplaySize(srcWidth: Float, srcHeight: Float, dstMaxWH: Float, dstMinWH: Float): ImageSize {
        var srcWidth = srcWidth
        var srcHeight = srcHeight
        if (srcWidth <= 0 || srcHeight <= 0) { // bounds check
            return ImageSize(dstMinWH.toInt(), dstMinWH.toInt())
        }
        var shorter: Float
        var longer: Float
        val widthIsShorter: Boolean
        //store
        if (srcHeight < srcWidth) {
            shorter = srcHeight
            longer = srcWidth
            widthIsShorter = false
        } else {
            shorter = srcWidth
            longer = srcHeight
            widthIsShorter = true
        }
        if (shorter < dstMinWH) {
            val scale = dstMinWH / shorter
            shorter = dstMinWH
            if (longer * scale > dstMaxWH) {
                longer = dstMaxWH
            } else {
                longer *= scale
            }
        } else if (longer > dstMaxWH) {
            val scale = dstMaxWH / longer
            longer = dstMaxWH
            if (shorter * scale < dstMinWH) {
                shorter = dstMinWH
            } else {
                shorter *= scale
            }
        }
        //restore
        if (widthIsShorter) {
            srcWidth = shorter
            srcHeight = longer
        } else {
            srcWidth = longer
            srcHeight = shorter
        }
        return ImageSize(srcWidth.toInt(), srcHeight.toInt())
    }

//    private fun getTempFilePath(extension: String): String {
//        return StorageUtil.getWritePath(
//            NimUIKit.getContext(), "temp_image_" + StringUtil.get36UUID().toString() + "." + extension,
//            StorageType.TYPE_TEMP
//        )
//    }

    /**
     * Get the image type
     *
     * @param path The absolute Path of the image
     * @return Image type: image/jpeg and image/png
     */
    fun getImageType(path: String?): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        return options.outMimeType
    }

    fun scaleImage(
        srcFile: File, dstFile: File?, dstMaxWH: Int, compressFormat: CompressFormat?,
        quality: Int
    ): Boolean? {
        var success = false
        try {
            val inSampleSize: Int = SampleSizeUtil.calculateSampleSize(srcFile.absolutePath, dstMaxWH * dstMaxWH)
            var srcBitmap: Bitmap? = BitmapDecoder.decodeSampled(srcFile.path, inSampleSize)
            if (srcBitmap == null) {
                return success
            }
            val rotate: Float
            val mimeType = getImageType(srcFile.absolutePath)
            rotate = if (!TextUtils.isEmpty(mimeType) && mimeType == "image/png") {
                // Images in PNG format cannot use ExifInterface
                0f
            } else {
                // Rotation
                val localExifInterface = ExifInterface(srcFile.absolutePath)
                val rotateInt = localExifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                getImageRotate(rotateInt)
            }
            var dstBitmap: Bitmap?
            val scale = sqrt(
                (dstMaxWH.toFloat() * dstMaxWH.toFloat() /
                        (srcBitmap.width.toFloat() * srcBitmap.height.toFloat())).toDouble()
            ).toFloat()
            if (rotate == 0f && scale >= 1) {
                dstBitmap = srcBitmap
            } else {
                try {
                    val matrix = Matrix()
                    if (rotate != 0f) {
                        matrix.postRotate(rotate)
                    }
                    if (scale < 1) {
                        matrix.postScale(scale, scale)
                    }
                    dstBitmap = Bitmap.createBitmap(
                        srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height,
                        matrix, true
                    )
                } catch (e: OutOfMemoryError) {
                    val bos = BufferedOutputStream(FileOutputStream(dstFile))
                    srcBitmap.compress(compressFormat, quality, bos)
                    bos.flush()
                    bos.close()
                    success = true
                    if (!srcBitmap.isRecycled) {
                        srcBitmap.recycle()
                    }
                    srcBitmap = null
                    return success
                }
            }
            val bos = BufferedOutputStream(FileOutputStream(dstFile))
            dstBitmap!!.compress(compressFormat, quality, bos)
            bos.flush()
            bos.close()
            success = true
            if (!srcBitmap.isRecycled) {
                srcBitmap.recycle()
            }
            srcBitmap = null
            if (!dstBitmap.isRecycled) {
                dstBitmap.recycle()
            }
            dstBitmap = null
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        return success
    }

//    fun getThumbnailDisplaySize(maxSide: Int, minSide: Int, imagePath: String?): ImageSize? {
//        val bound = BitmapDecoder.decodeBound(imagePath)
//        return getThumbnailDisplaySize(
//            bound!![0].toFloat(),
//            bound[1].toFloat(), maxSide.toFloat(), minSide.toFloat()
//        )
//    }
//
//    fun getBoundWithLength(maxSide: Int, imageObject: Any?, resizeToDefault: Boolean): IntArray? {
//        var width = -1
//        var height = -1
//        val bound: IntArray?
//        if (String::class.java.isInstance(imageObject)) {
//            bound = BitmapDecoder.decodeBound(imageObject as String?)
//            width = bound!![0]
//            height = bound[1]
//        } else if (Int::class.java.isInstance(imageObject)) {
//            bound = BitmapDecoder.decodeBound(NimUIKit.getContext().getResources(), (imageObject as Int?)!!)
//            width = bound!![0]
//            height = bound[1]
//        } else if (InputStream::class.java.isInstance(imageObject)) {
//            bound = BitmapDecoder.decodeBound(imageObject as InputStream?)
//            width = bound[0]
//            height = bound[1]
//        }
//        if (width <= 0 || height <= 0) {
//            width = maxSide
//            height = maxSide
//        } else if (resizeToDefault) {
//            if (width > height) {
//                height = (maxSide * (height.toFloat() / width.toFloat())).toInt()
//                width = maxSide
//            } else {
//                width = (maxSide * (width.toFloat() / height.toFloat())).toInt()
//                height = maxSide
//            }
//        }
//        return intArrayOf(width, height)
//    }

    /**
     * Display the default download failure image if downloading fails
     *
     * @return
     */
    fun getBitmapFromDrawableRes(context: Context, res: Int): Bitmap? {
        return try {
            getBitmapImmutableCopy(context.resources, res)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isInvalidPictureFile(mimeType: String): Boolean {
        val lowerCaseFilepath = mimeType.lowercase(Locale.getDefault())
        return lowerCaseFilepath.contains("jpg") || lowerCaseFilepath.contains("jpeg") ||
                lowerCaseFilepath.lowercase(Locale.getDefault()).contains("png") || lowerCaseFilepath.lowercase(Locale.getDefault()).contains("bmp") ||
                lowerCaseFilepath.lowercase(Locale.getDefault()).contains("gif")
    }

    fun isGif(extension: String): Boolean {
        return !TextUtils.isEmpty(extension) && extension.lowercase(Locale.getDefault()) == "gif"
    }
//
//    fun getOptions(path: String?): BitmapFactory.Options? {
//        val options = BitmapFactory.Options()
//        options.inJustDecodeBounds = true
//        BitmapFactory.decodeFile(path, options)
//        return options
//    }



}