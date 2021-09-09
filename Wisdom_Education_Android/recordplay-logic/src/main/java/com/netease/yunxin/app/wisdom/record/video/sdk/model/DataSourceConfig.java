/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.sdk.model;

/**
 * 播放数据源配置项
 * 在设置播放地址和切换播放地址时如果有需要可以配置该信息
 *
 * @author netease
 */
public class DataSourceConfig {

    /**
     * 点播本地缓存配置信息
     */
    public CacheConfig cacheConfig;

    /**
     * 点播视频解密配置
     */
    public DecryptionConfig decryptionConfig;
}

