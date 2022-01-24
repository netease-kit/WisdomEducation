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

    private int mVideoWidth; // Video frame width
    private int mVideoHeight; // Video frame height
    private int mVideoSarNum; // width of the aspect ratio
    private int mVideoSarDen; // height of the aspect ratio

    private int mMeasuredWidth; // measured width
    private int mMeasuredHeight; // measured height

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
            return false; // No data is loaded. No graphic is displayed
        }

        // The output is contained by the parent container. The default rendering width and heigth based on the video resolution
        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (width == 0 && height == 0) {
            mMeasuredWidth = 0;
            mMeasuredHeight = 0;
            return true; // Automatic hide
        }

        // Enter width and height
       ALog.i("on measure, input widthMeasureSpec=" + MeasureSpec.toString(widthMeasureSpec)
                + ", heightMeasureSpec=" + MeasureSpec.toString(heightMeasureSpec) + ", video scale mode=" + mVideoScaleMode);

        /*
         * The maximum width and height constrained by the parent container
         * Custom View has the size of the parent container whether WRAP_CONTENT or MATCH_PARENT is selected
         * The mode is different, MATCH_PARENT uses EXACTLY，WRAP_CONTENT selects AT_MOST。
         */
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec); // The width of the parent container
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec); // The height of the parent container

        // Aspect ratio of the parent container
        final float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;

        // Aspect ratio of the video frame
        float displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
        }

        // Check whether the video aspect ratio is greater
        final boolean shouldBeWider = displayAspectRatio > specAspectRatio;

        // Adjust the aspect ratio based on the specified scale mode
        if (mVideoScaleMode == VideoScaleMode.FILL) {
            // Full screen. Zoom to the maximum aspect ratio (the aspect ratio of the parent container)
            width = widthSpecSize;
            height = heightSpecSize;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
                // WRAP_CONTENT: determine width and height based on mode
                if (mVideoScaleMode == VideoScaleMode.FIT) {
                    // Zoom proportionally with a black bar at a side
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
                    // Zoom to full screen at a scale. One side will be cropped
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
                    // original size
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
                // MATCH_PARENT: The control size fit the parent container, or the width and height dip are specified.
                // Zoom proportionally with one side of black bars. VideoScaleMode.FIT
                // The operation changes the expected control size. Not recommended！
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

        // Final output
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
