/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.model;

/**
 * Scaling and cropping mode for video rendering
 * <p>
 *
 * @author netease
 */
public enum VideoScaleMode {
    NONE, // Original dimension
    FIT,  // Scale aspect ratio. Black bars appear at a side
    FILL, // full screen. The image may stretch
    FULL  // Scale aspect ratio to fit full screen. One side will be cropped
}
