/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.view;

import android.view.View;
import android.view.View.MeasureSpec;

import com.netease.yunxin.app.wisdom.player.sdk.model.VideoScaleMode;
import com.netease.yunxin.kit.alog.ALog;

import java.lang.ref.WeakReference;

/**
 * @author netease
 */
public final class MeasureHelper {

    private WeakReference<View> mWeakView;

    private int mVideoWidth; // 视频帧宽度
    private int mVideoHeight; // 视频帧高度
    private int mVideoSarNum; // 视频帧像素宽高比的宽，计算机产生的像素宽高比都是1:1
    private int mVideoSarDen; // 视频帧像素宽高比的高

    private int mMeasuredWidth; // 测量结果宽spec
    private int mMeasuredHeight; // 测量结果高spec

    private VideoScaleMode mVideoScaleMode = VideoScaleMode.FULL;

    MeasureHelper(View view) {
        mWeakView = new WeakReference<>(view);
    }

    public View getView() {
        if (mWeakView == null) {
            return null;
        }

        return mWeakView.get();
    }

    boolean setVideoSize(int videoWidth, int videoHeight) {
        if (mVideoWidth == videoWidth && mVideoHeight == videoHeight) {
            return false; // the same
        }

        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        return true; // changed
    }

    boolean setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if (mVideoSarNum == videoSarNum && mVideoSarDen == videoSarDen) {
            return false; // the same
        }

        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
        return true; // changed
    }

    boolean setVideoScaleMode(VideoScaleMode mode) {
        if (mVideoScaleMode == mode) {
            return false; // the same
        }

        mVideoScaleMode = mode;
        return true;
    }

    boolean doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mVideoWidth <= 0 || mVideoHeight <= 0) {
            mMeasuredWidth = 0;
            mMeasuredHeight = 0;
            return false; // 还没有收到画面,先不显示
        }

        // 输出在父容器的约束下，根据视频帧的分辨率，默认的渲染宽高
        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (width == 0 && height == 0) {
            mMeasuredWidth = 0;
            mMeasuredHeight = 0;
            return true; // 主动隐藏
        }

        // 输入宽高spec
       ALog.i("on measure, input widthMeasureSpec=" + MeasureSpec.toString(widthMeasureSpec)
                + ", heightMeasureSpec=" + MeasureSpec.toString(heightMeasureSpec) + ", video scale mode=" + mVideoScaleMode);

        /*
         * 父容器约束下的View最大宽、高最大尺寸
         * 自定义View，无论选择WRAP_CONTENT还是MATCH_PARENT，他的尺寸都是size, 即父亲的尺寸;
         * 当然模式会不一样，MATCH_PARENT对应EXACTLY，WRAP_CONTENT对应AT_MOST。
         */
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec); // 父容器的宽度
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec); // 父容器的高度

        // 父容器的宽高比
        final float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;

        // 视频帧的宽高比
        float displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
        }

        // 是否视频帧的宽高比更大
        final boolean shouldBeWider = displayAspectRatio > specAspectRatio;

        // 根据用户指定的缩放模式来调整最终渲染的宽高
        if (mVideoScaleMode == VideoScaleMode.FILL) {
            // 全屏，拉伸到控件允许的最大宽高(即父容器宽高)
            width = widthSpecSize;
            height = heightSpecSize;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
                // WRAP_CONTENT:根据模式来确定最终绘制的宽高
                if (mVideoScaleMode == VideoScaleMode.FIT) {
                    // 按比例拉伸，有一边会贴黑边
                    if (shouldBeWider) {
                        // too wide, fix width
                        width = widthSpecSize;
                        height = (int) (width / displayAspectRatio);
                    } else {
                        // too high, fix height
                        height = heightSpecSize;
                        width = (int) (height * displayAspectRatio);
                    }
                } else if (mVideoScaleMode == VideoScaleMode.FULL) {
                    // 按比例拉伸至全屏，有一边会被裁剪
                    if (shouldBeWider) {
                        // not high enough, fix height
                        height = heightSpecSize;
                        width = (int) (height * displayAspectRatio);
                    } else {
                        // not wide enough, fix width
                        width = widthSpecSize;
                        height = (int) (width / displayAspectRatio);
                    }
                } else if (mVideoScaleMode == VideoScaleMode.NONE) {
                    // 原始大小
                    if (shouldBeWider) {
                        // too wide, fix width
                        width = Math.min(mVideoWidth, widthSpecSize);
                        height = (int) (width / displayAspectRatio);
                    } else {
                        // too high, fix height
                        height = Math.min(mVideoHeight, heightSpecSize);
                        width = (int) (height * displayAspectRatio);
                    }
                } else {
                   ALog.i("on measure, unsupported scale mode!!!");
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // MATCH_PARENT: 控件大小已经确定即填满父容器，或者已经制定宽度高度具体dip了。
                // 这里只做等比例拉伸，有一边会贴黑边。即 VideoScaleMode.FIT
                // 这样会改变用户预期的控件大小，不推荐！
                if (shouldBeWider) {
                    width = widthSpecSize;
                    height = (int) (width / displayAspectRatio);
                } else {
                    height = heightSpecSize;
                    width = (int) (height * displayAspectRatio);
                }
            } else {
               ALog.i("on measure, unsupported spec mode!!!");
            }
        } else {
           ALog.i("on measure, unsupported spec mode!!!");
        }

        // 最后输出的结果
        mMeasuredWidth = width;
        mMeasuredHeight = height;

       ALog.i("on measure done, set measure width=" + mMeasuredWidth + ", height=" + mMeasuredHeight);
        return true;
    }

    int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    int getMeasuredHeight() {
        return mMeasuredHeight;
    }
}
