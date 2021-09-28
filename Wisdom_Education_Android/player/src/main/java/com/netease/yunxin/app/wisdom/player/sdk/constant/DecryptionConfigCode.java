/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.constant;

import com.netease.neliveplayer.sdk.constant.NEDecryptionConfigCode;

/**
 * 解密选项
 * 只适用于点播
 */
public interface DecryptionConfigCode {

    /**
     * 不需要对视频进行解密
     */
    int CODE_DECRYPTION_NONE = NEDecryptionConfigCode.CODE_DECRYPTION_NONE;

    /**
     * 使用解密信息对视频进行解密
     */
    int CODE_DECRYPTION_INFO = NEDecryptionConfigCode.CODE_DECRYPTION_INFO;

    /**
     * 解密秘钥对视频进行解密
     */
    int CODE_DECRYPTION_KEY = NEDecryptionConfigCode.CODE_DECRYPTION_KEY;

}
