/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.sdk.model;


import com.netease.neliveplayer.sdk.constant.NEBufferStrategy;

/**
 * 视频缓冲策略
 *
 * @author netease
 */

public enum VideoBufferStrategy {
    /**
     * 直播极速模式
     */
    FAST(NEBufferStrategy.NELPTOPSPEED),

    /**
     * 直播低延时模式
     */
    LOW_LATENCY(NEBufferStrategy.NELPLOWDELAY),

    /**
     * 直播流畅模式
     */
    FLUENCY(NEBufferStrategy.NELPFLUENT),

    /**
     * 点播抗抖动模式
     */
    ANTI_JITTER(NEBufferStrategy.NELPANTIJITTER),

    /**
     * 直播延时追赶模式
     */
    DELAY_PULL_UP(NEBufferStrategy.NELPDELAYPULLUP);

    final private int value;

    VideoBufferStrategy(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static VideoBufferStrategy typeOfValue(int value) {
        for (VideoBufferStrategy e : values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return LOW_LATENCY;
    }
}
