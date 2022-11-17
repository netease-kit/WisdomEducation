package com.netease.yunxin.app.wisdom.edu.logic.model


/**
 * 麦位信息。
 * @property creator 麦位创建者。
 * @property managers 管理员列表。
 * @property seatItems 麦位列表信息。
 */
data class NESeatInfo(
    val creator: String,
    val managers: List<String>,
    val seatItems: List<NESeatItem>
)

/**
 * 单个麦位信息。
 * @property index 麦位位置。
 * @property status 麦位状态，参考[NESeatItemStatus]。
 * @property user 当前状态关联的用户。
 * @property updated 更新时间戳，单位ms。
 */
data class NESeatItem(
    val index: Int,
    val status: Int,
    val user: String?,
    val userName: String?,
    val icon: String?,
    val updated: Long
)

/**
 * 麦位状态
 */
object NESeatItemStatus {

    /**
     * 麦位初始化（无人，可以上麦）
     */
    const val INITIAL = 0

    /**
     * 该麦位正在等待管理员通过申请或等待成员接受邀请后上麦。
     */
    const val WAITING = 1

    /**
     * 当前麦位已被占用
     */
    const val TAKEN = 2

    /**
     * 当前麦位已关闭，不能操作上麦
     */
    const val CLOSED = -1
}