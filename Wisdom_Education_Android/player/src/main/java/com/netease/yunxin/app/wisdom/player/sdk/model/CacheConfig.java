/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.model;

/**
 * Cache configuration information
 *
 * @author netease
 */
public class CacheConfig {
    /**
     * Check whether to cache the audio and video data
     */
    public boolean isCache;
    /**
     * The address of the cache path
     * If the cache path is unspecified, the default cache path is used, and the cache is deleted after the player is released or reset, or the URL is changed
     * If the cache path is specified, the SDK cache the data is the specified path and the application layer manages the cached data.
     */
    public String cachePath;

    public CacheConfig(boolean isCache, String cachePath) {
        this.isCache = isCache;
        this.cachePath = cachePath;
    }
}
