/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.model;


import com.netease.yunxin.app.wisdom.player.sdk.LivePlayer;

/**
 * Player status class
 *
 * @author netease
 */

public class StateInfo {

    private LivePlayer.STATE state;
    private int causeCode;

    public StateInfo(LivePlayer.STATE state, int causeCode) {
        this.state = state;
        this.causeCode = causeCode;
    }

    /**
     * The player current state
     *
     * @return The current state
     */
    public LivePlayer.STATE getState() {
        return state;
    }

    /**
     * The reason to the state
     *
     * @return Status codes or causes. For more information, see {@link CauseCode}
     */
    public int getCauseCode() {
        return causeCode;
    }
}
