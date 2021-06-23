//
//  NEEduChatRoomResponse.h
//  EduLogic
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduChatRoomResponse : NSObject
@property (nullable,nonatomic, copy) NSString *chatRoomID;
@property (nullable,nonatomic, copy) NSString *chatRoomName;

@property (nullable,nonatomic, copy) NSString *userID;
@property (nullable,nonatomic, copy) NSString *userNickName;
@property (nullable,nonatomic, copy) NSString *avatar;

@end

NS_ASSUME_NONNULL_END
