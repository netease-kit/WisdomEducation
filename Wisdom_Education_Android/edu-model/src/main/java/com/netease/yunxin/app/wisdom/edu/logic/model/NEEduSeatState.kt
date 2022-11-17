package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * @property state [NEEduHandsUpStateValue]
 * @property userUuid
 * @property operateByUuid
 */
data class NEEduSeatState(
    val state:Int,
    val userUuid:String,
    val operateByUuid:String?
)
