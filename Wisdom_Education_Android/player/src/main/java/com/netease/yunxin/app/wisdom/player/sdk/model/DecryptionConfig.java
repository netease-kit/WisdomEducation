/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.model;


import com.netease.yunxin.app.wisdom.player.sdk.constant.DecryptionConfigCode;

/**
 * 点播视频解密配置
 * 只适用于点播
 *
 * @author netease
 */
public class DecryptionConfig {

    /**
     * 解密选项
     * 只适用于点播
     * {@link DecryptionConfigCode}
     */
    public int decryptionCode;


    /**
     * 获取密钥所需的令牌
     */
    public String transferToken;
    /**
     * 视频云用户创建的其子用户id
     */
    public String accid;
    /**
     * 开发者平台分配的AppKey
     */
    public String appKey;
    /**
     * 视频云用户子用户的token
     */
    public String token;

    /**
     * 密钥
     */
    public byte[] flvKey;
    /**
     * 密钥长度
     */
    public int flvKeyLen;

    /**
     * 解密信息方式
     * 使用解密信息对视频进行解密时需要设置相关的解密信息
     *
     * @param transferToken 获取密钥所需的令牌
     * @param accid         视频云用户创建的其子用户id
     * @param appKey        开发者平台分配的AppKey
     * @param token         视频云用户子用户的token
     */
    public DecryptionConfig(String transferToken, String accid, String appKey, String token) {
        this.transferToken = transferToken;
        this.accid = accid;
        this.appKey = appKey;
        this.token = token;
        decryptionCode = DecryptionConfigCode.CODE_DECRYPTION_INFO;
    }

    /**
     * 解密密钥方式
     * 在已知密钥的情况下直接使用密钥对密钥做相关的校验
     * 使用解密秘钥对视频进行解密时需要设置相关的解密秘钥
     *
     * @param flvKey    密钥
     * @param flvKeyLen 密钥长度
     */
    public DecryptionConfig(byte[] flvKey, int flvKeyLen) {
        this.flvKey = flvKey;
        this.flvKeyLen = flvKeyLen;
        decryptionCode = DecryptionConfigCode.CODE_DECRYPTION_KEY;
    }

}
