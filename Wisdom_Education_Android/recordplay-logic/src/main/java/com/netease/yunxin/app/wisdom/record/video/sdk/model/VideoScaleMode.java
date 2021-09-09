/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.sdk.model;

/**
 * 视频渲染时的缩放/裁减模式
 * <p>
 *
 * @author netease
 */
public enum VideoScaleMode {
    NONE, // 原始大小
    FIT,  // 按比例拉伸，有一边会贴黑边
    FILL, // 全屏，画面可能会变形
    FULL  // 按比例拉伸至全屏，有一边会被裁剪
}
