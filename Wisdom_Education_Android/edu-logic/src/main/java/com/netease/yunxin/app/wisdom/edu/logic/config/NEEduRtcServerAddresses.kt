package com.netease.yunxin.app.wisdom.edu.logic.config

/**
 * G2 RTC私有化
 *
 */
data class NEEduRtcServerAddresses(
    var appkey: String? = null,
    var demoServer: String? = null,
    var channelServer: String? = null,
    var statisticsServer: String? = null,
    var roomServer: String? = null,
    var compatServer: String? = null,
    var nosLbsServer: String? = null,
    var nosUploadSever: String? = null,
    var nosTokenServer: String? = null,
    var useIPv6: Boolean? = null
)