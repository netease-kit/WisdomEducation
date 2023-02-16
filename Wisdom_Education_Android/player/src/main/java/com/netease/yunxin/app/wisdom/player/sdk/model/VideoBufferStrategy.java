/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.model;


import com.netease.neliveplayer.sdk.constant.NEBufferStrategy;

/**
 * Video buffer strategy
 *
 * @author netease
 */

public enum VideoBufferStrategy {
    /**
     * Fast mode
     */
    FAST(NEBufferStrategy.NELPTOPSPEED),

    /**
     * Low-latency mode
     */
    LOW_LATENCY(NEBufferStrategy.NELPLOWDELAY),

    /**
     * Fluency mode
     */
    FLUENCY(NEBufferStrategy.NELPFLUENT),

    /**
     * Anti-jitter mode
     */
    ANTI_JITTER(NEBufferStrategy.NELPANTIJITTER),

    /**
     * Delay pull-up mode
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
