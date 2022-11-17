/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.model;

/**
 * Data source configuration
 * The option is required if you want to Set or change the URL of the data source
 *
 * @author netease
 */
public class DataSourceConfig {

    /**
     * VOD cache configuration
     */
    public CacheConfig cacheConfig;

    /**
     * VOD decryption configuration
     */
    public DecryptionConfig decryptionConfig;
}

