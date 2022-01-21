//
//  NEEduEnterRoomResponse.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduHttpRoom.h"
#import "NEEduHttpUser.h"

NS_ASSUME_NONNULL_BEGIN

/// 进入房间 回调
@interface NEEduEnterRoomResponse : NSObject
@property (nonatomic , strong) NEEduHttpUser              * member;
@property (nonatomic , strong) NEEduHttpRoom              * room;
@end

NS_ASSUME_NONNULL_END
