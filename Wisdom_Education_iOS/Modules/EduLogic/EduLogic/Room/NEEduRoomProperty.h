//
//  NEEduRoomProperty.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduChatRoom.h"
#import "NEEduHandsupProperty.h"
#import "NEEduWhiteboardInfo.h"
#import "NEEduLiveInfo.h"

NS_ASSUME_NONNULL_BEGIN

/// 房间属性
@interface NEEduRoomProperty : NSObject
/// 聊天室
@property (nonatomic, strong) NEEduChatRoom *chatRoom;
@property (nonatomic, strong) NEEduHandsupProperty *avHandsUp;
@property (nonatomic, strong) NEEduWhiteboardInfo *whiteboard;
/// 直播信息
@property (nonatomic, strong) NEEduLiveInfo *live;

@end

NS_ASSUME_NONNULL_END
