/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.sdk.model;

import com.netease.yunxin.app.wisdom.record.video.sdk.LivePlayer;

/**
 * 视频播放配置项
 *
 * @author netease
 */

public class VideoOptions {

    public static VideoOptions getDefault() {
        return new VideoOptions();
    }

    /**
     * 播放缓冲策略，默认是极速模式
     * {@link VideoBufferStrategy}
     */
    public VideoBufferStrategy bufferStrategy = VideoBufferStrategy.FAST;

    /**
     * 缓冲区大小
     * 只适用于点播，不能用于直播
     * 可设置范围是5M~100M
     */
    public int bufferSize = 150 * 1024 * 1024;

    /**
     * 是否为硬件解码，默认为软件解码
     */
    public boolean hardwareDecode = false;

    /**
     * 是否长时间后台播放
     * 如果是true，那么APP应用层需要开启service后台播放长时间播放，AdvanceLivePlayer里会关闭切后台3分钟回到前台就重置播放器拉流
     * 如果是false，那么AdvanceLivePlayer里切后台3分钟回到前台就重置播放器拉流
     */
    public boolean isPlayLongTimeBackground = false;

    /**
     * 拉流超时时间，单位秒。只能设置(0, 10]之间的值，填0或者超过10则改为默认超时时间10秒
     */
    public int playbackTimeout = 10;

    /**
     * 设置循环播放
     * 默认是0，不循环；-1无限循环；1循环一次，2循环两次，以此类推
     */
    public int loopCount = 0;

    /**
     * 设置精确seek
     * 只适用于点播
     * false，关闭精确seek；true，打开精确seek，默认打开精确seek
     */
    public boolean isAccurateSeek = true;

    /**
     * 是否自动播放
     * 若设置成false，需要手动调用start()进行播放
     * true:自动播放  false:不自动播放
     */
    public boolean isAutoStart = false;

    /**
     * 播放数据源配置项
     */
    public DataSourceConfig dataSourceConfig;

    /**
     * 是否开启获取同步时间戳、同步内容信息回调
     * 如果使用同步时间戳、同步内容信息回调相关功能，需要先打开该开关
     * {@link LivePlayer#registerPlayerCurrentSyncContentListener}
     * {@link LivePlayer#getCurrentSyncTimestamp}
     * {@link LivePlayer#registerPlayerCurrentSyncTimestampListener}
     */
    public boolean isSyncOpen = false;

}
