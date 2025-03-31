package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.ExecuteUserSeatActionReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatInfo
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatRequestItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface SeatService {
    @POST("scene/apps/{appKey}/v1/rooms/{uuid}/seat/user/action")
    fun executeUserAction(
        @Path("appKey") appKey: String,
        @Path("uuid") uuid: String,
        @Body eduStreamStatusReq: ExecuteUserSeatActionReq,
    ): LiveData<NEResult<Void>>

    @GET("scene/apps/{appKey}/v1/rooms/{uuid}/seat/seatList")
     fun getSeatInfo(
        @Path("appKey") appKey: String,
        @Path("uuid") uuid: String
    ): LiveData<NEResult<NESeatInfo>>

    @GET("scene/apps/{appKey}/v1/rooms/{uuid}/seat/applyList")
     fun getSeatRequestList(
        @Path("appKey") appKey: String,
        @Path("uuid") uuid: String
    ): LiveData<NEResult<List<NESeatRequestItem>>>
}