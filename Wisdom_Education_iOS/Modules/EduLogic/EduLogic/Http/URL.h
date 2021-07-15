//
//  URL.h
//  YXEducation
//
//  Created by Netease on 2020/4/16.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

// /scene/apps/{appKey}/v1/users/{userUuid}/login
#define HTTP_LOGIN @"%@/scene/apps/%@/%@/users/%@/login"

// 登录：/scene/apps/{appKey}/v1/anonymous/login
#define HTTP_EASY_LOGIN @"%@/scene/apps/%@/%@/anonymous/login"

//创建房间: /scene/apps/{appKey}/v1/rooms/{roomUuid}
#define HTTP_CREATE_ROOM @"%@/scene/apps/%@/%@/rooms/%@"

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


