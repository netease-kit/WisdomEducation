/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.model;

import com.netease.yunxin.app.wisdom.player.sdk.LivePlayer;

/**
 * Playback options
 *
 * @author netease
 */

public class VideoOptions {

    public static VideoOptions getDefault() {
        return new VideoOptions();
    }

    /**
     * Playback cache strategy. The default value is the fast mode.
     * {@link VideoBufferStrategy}
     */
    public VideoBufferStrategy bufferStrategy = VideoBufferStrategy.FAST;

    /**
     * The cache size.
     * Applies only to VOD not live streaming.
     * The value range: 5M to 100M.
     */
    public int bufferSize = 150 * 1024 * 1024;

    /**
     * Specify whether to use hardware decoder. The default value is software decoder.
     */
    public boolean hardwareDecode = false;

    /**
     * Specify whether to continue playback in the background
     * If set to true，the application has to enable the service option to allow background playback. The player stops switching to foreground after 3 minutes in AdvanceLivePlayer
     * If set to false，the player resets the stream 3 minutes after the player is switched back to foreground using streamsAdvanceLivePlayer
     */
    public boolean isPlayLongTimeBackground = false;

    /**
     * Playback timeout. Unit: seconds. Value range: 0 to 10. A value of 0 or a number higher than 10 will be set to the default vale 10.
     */
    public int playbackTimeout = 10;

    /**
     * Set loop playback
     * The default value 0 inidcates no loop, -1: infinite loop. 1: loop playback for once and 2 for twice and the like
     */
    public int loopCount = 0;

    /**
     * Set accurate seek
     * Apply only to VOD
     * false: disable accurate seek；true: enable accurate seek. The default value is true.
     */
    public boolean isAccurateSeek = true;

    /**
     * Specify whether to enable auto playback
     * If set to false, the playback is implemented using the start() manually
     * true: enables auto playback  false: disables auto playback
     */
    public boolean isAutoStart = false;

    /**
     * Data source configuration
     */
    public DataSourceConfig dataSourceConfig;

    /**
     * Specify whether to register the callbacks for timestamp and content synchronization events
     * To register the callback, enable the switch
     * {@link LivePlayer#registerPlayerCurrentSyncContentListener}
     * {@link LivePlayer#getCurrentSyncTimestamp}
     * {@link LivePlayer#registerPlayerCurrentSyncTimestampListener}
     */
    public boolean isSyncOpen = false;

}
