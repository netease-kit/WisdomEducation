//
//  URL.h
//  YXEducation
//
//  Created by Netease on 2020/4/16.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

// 登录：/scene/apps/{appKey}/v1/login
#define HTTP_LOGIN @"%@/scene/apps/%@/%@/login"

// 匿名登录：/scene/apps/{appKey}/v1/anonymous/login
#define HTTP_EASY_LOGIN @"%@/scene/apps/%@/%@/anonymous/login"

//创建房间: /scene/apps/{appKey}/v1/rooms/{roomUuid}
#define HTTP_CREATE_ROOM @"%@/scene/apps/%@/%@/rooms/%@"

//获取房间配置信息: /scene/apps/{appKey}/v1/rooms/{roomUuid}/config
#define HTTP_GET_ROOM @"%@/scene/apps/%@/%@/rooms/%@/config"

//加入房间: scene/apps/{appKey}/v1/rooms/{roomUuid}/entry
#define HTTP_ENTER_ROOM @"%@/scene/apps/%@/%@/rooms/%@/entry"

//房间预览: /scene/apps/{appKey}/v1/rooms/{roomUuid}/snapshot
#define HTTP_ROOM_profile @"%@/scene/apps/%@/%@/rooms/%@/snapshot"

//开关音视频: /scene/apps/{appKey}/v1/rooms/{roomUuid}/members/{userUuid}/streams/{streamType}
#define HTTP_STREAM_STATE @"%@/scene/apps/%@/%@/rooms/%@/members/%@/streams/%@"

//用户属性改变 /scene/apps/{appKey}/v1/rooms/{roomId}/members/{userUuid}/properties/{key}
#define HTTP_MEMBER_PROPERTY @"%@/scene/apps/%@/%@/rooms/%@/members/%@/properties/%@"
//课堂开启关闭 scene/apps/{appKey}/v1/rooms/{roomId}/states/step
#define HTTP_LESSON_START @"%@/scene/apps/%@/%@/rooms/%@/states/step"

//全体静音 scene/apps/{appKey}/v1/rooms/{roomId}/states/muteAudio
#define HTTP_LESSON_MUTE @"%@/scene/apps/%@/%@/rooms/%@/states/muteAudio"

//全体禁言 scene/apps/{appKey}/v1/rooms/{roomId}/states/muteChat
#define HTTP_LESSON_MUTE_CHAT @"%@/scene/apps/%@/%@/rooms/%@/states/muteChat"

//拉取丢失的IM消息 scene/apps/{appKey}/v1/rooms/{roomId}/sequence?nextId=
#define HTTP_MESSAGE_GET @"%@/scene/apps/%@/%@/rooms/%@/sequence?nextId=%ld"

//拉取回放地址 /scene/apps/{appKey}/v1/rooms/{roomUuid}/{rtcCid}/record/playback
#define HTTP_RECORD_GET @"%@/scene/apps/%@/%@/rooms/%@/%@/record/playback"

// 离开房间
#define HTTP_LEAVE_ROOM @"%@/scene/apps/%@/v1/rooms/%@/members/%@"

#pragma mark ------------------------ 麦位 ------------------------
// 用户麦位操作
#define HTTP_SEAT_USER_OPERATION @"%@/scene/apps/%@/%@/rooms/%@/seat/user/action"
// 获取麦位信息
#define HTTP_SEAT_INFO @"%@/scene/apps/%@/%@/rooms/%@/seat/seatList"
// 麦位请求列表
#define HTTP_SEAT_REQUEST_LIST @"%@/scene/apps/%@/v1/rooms/%@/seat/applyList"
