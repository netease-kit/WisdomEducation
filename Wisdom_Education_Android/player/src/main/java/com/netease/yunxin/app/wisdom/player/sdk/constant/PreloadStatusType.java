/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.constant;

import com.netease.neliveplayer.sdk.constant.NEPreloadStatusType;

/**
 * 预调度结果状态.
 */
public class PreloadStatusType {
    /**
     * 等待预调度
     */
    int WAIT = NEPreloadStatusType.WAIT;
    /**
     * 正在预调度
     */
    int RUNNING = NEPreloadStatusType.RUNNING;
    /**
     * 已经完成预调度
     */
    int COMPLETE = NEPreloadStatusType.COMPLETE;
}
