/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.constant;

import com.netease.neliveplayer.sdk.constant.NEPreloadStatusType;

/**
 * The preload status
 */
public class PreloadStatusType {
    /**
     * Wait for preload
     */
    int WAIT = NEPreloadStatusType.WAIT;
    /**
     * Running the preload
     */
    int RUNNING = NEPreloadStatusType.RUNNING;
    /**
     * The preload is complete
     */
    int COMPLETE = NEPreloadStatusType.COMPLETE;
}
