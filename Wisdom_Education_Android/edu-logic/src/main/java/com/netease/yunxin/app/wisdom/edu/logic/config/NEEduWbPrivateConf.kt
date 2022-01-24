package com.netease.yunxin.app.wisdom.edu.logic.config

/**
 * 白板私有化
 *
 */
data class NEEduWbPrivateConf(
    var roomServerAddr: String? = null,
    var sdkLogNosAddr: String? = null,
    var dataReportAddr: String? = null,
    var directNosAddr: String? = null,
    var mediaUploadAddr: String? = null,
    var docTransAddr: String? = null,
    var fontDownloadUrl: String? = null
)