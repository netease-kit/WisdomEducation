/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.model;


import com.netease.yunxin.app.wisdom.player.sdk.constant.DecryptionConfigCode;

/**
 * VOD decryption configuration
 * Applies only to VOD
 *
 * @author netease
 */
public class DecryptionConfig {

    /**
     * Decryption options
     * Applies only to VOD
     * {@link DecryptionConfigCode}
     */
    public int decryptionCode;


    /**
     * Token required to get the key
     */
    public String transferToken;
    /**
     * subaccount ID
     */
    public String accid;
    /**
     * The AppKey in the CommsEase console
     */
    public String appKey;
    /**
     * The token the belongs to the subaccount
     */
    public String token;

    /**
     * Key
     */
    public byte[] flvKey;
    /**
     * Key length
     */
    public int flvKeyLen;

    /**
     * Decryption method
     * The decryption configuration
     *
     * @param transferToken The token used to get the key
     * @param accid         The subaccount ID
     * @param appKey        The AppKey in the CommsEase console
     * @param token         The token the belongs to the subaccount
     */
    public DecryptionConfig(String transferToken, String accid, String appKey, String token) {
        this.transferToken = transferToken;
        this.accid = accid;
        this.appKey = appKey;
        this.token = token;
        decryptionCode = DecryptionConfigCode.CODE_DECRYPTION_INFO;
    }

    /**
     * Decryption method
     * Verify the key if the key is obtained
     * Set the key to decrypt the video content
     *
     * @param flvKey    Key
     * @param flvKeyLen Key length
     */
    public DecryptionConfig(byte[] flvKey, int flvKeyLen) {
        this.flvKey = flvKey;
        this.flvKeyLen = flvKeyLen;
        decryptionCode = DecryptionConfigCode.CODE_DECRYPTION_KEY;
    }

}
