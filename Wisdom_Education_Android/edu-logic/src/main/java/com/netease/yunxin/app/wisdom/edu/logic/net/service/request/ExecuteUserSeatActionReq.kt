package com.netease.yunxin.app.wisdom.edu.logic.net.service.request

/**
 * The request parameters for seat action
 * @param action Operation type
 */
data class ExecuteUserSeatActionReq(
    val action: Int,
    val userName:String?
)

object UserSeatAction {
    const val SUBMIT_REQUEST = 1
    const val CANCEL_REQUEST = 2
    const val LEAVE = 5
}