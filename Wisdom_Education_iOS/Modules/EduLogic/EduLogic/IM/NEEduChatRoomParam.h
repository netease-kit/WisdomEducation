//
//  NEEduChatRoomParam.h
//  EduLogic
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduChatRoomParam : NSObject
@property (nonatomic, copy) NSString *chatRoomID;
@property (nonatomic, copy ,nullable) NSString *nickname;
@property (nonatomic, copy ,nullable) NSString *avatar;
@end

NS_ASSUME_NONNULL_END
