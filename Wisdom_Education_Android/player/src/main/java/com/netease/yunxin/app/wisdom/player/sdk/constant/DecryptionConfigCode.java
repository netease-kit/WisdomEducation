/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.constant;

import com.netease.neliveplayer.sdk.constant.NEDecryptionConfigCode;

/**
 * Decryption configuration
 * Apply only to VOD
 */
public interface DecryptionConfigCode {

    /**
     * Decruption not required
     */
    int CODE_DECRYPTION_NONE = NEDecryptionConfigCode.CODE_DECRYPTION_NONE;

    /**
     * Decrypt a video using the decryption information
     */
    int CODE_DECRYPTION_INFO = NEDecryptionConfigCode.CODE_DECRYPTION_INFO;

    /**
     * Decrypt a video using a decryption key
     */
    int CODE_DECRYPTION_KEY = NEDecryptionConfigCode.CODE_DECRYPTION_KEY;

}
