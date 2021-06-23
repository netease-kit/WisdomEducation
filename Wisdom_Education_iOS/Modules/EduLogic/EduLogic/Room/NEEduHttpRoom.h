//
//  NEEduHttpRoom.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduRoomProperty.h"
#import "NEEduRoomStates.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduHttpRoom : NSObject
@property (nonatomic , copy) NSString              * roomName;
@property (nonatomic , copy) NSString              * roomUuid;
@property (nonatomic , copy) NSString              * rtcCid;
@property (nonatomic , strong) NEEduRoomProperty              * properties;
@property (nonatomic , strong) NEEduRoomStates              * states;
@end

NS_ASSUME_NONNULL_END
