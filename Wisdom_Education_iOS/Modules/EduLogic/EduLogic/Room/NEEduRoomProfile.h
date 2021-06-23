//
//  NEEduRoomProfile.h
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

@interface Snapshot :NSObject
@property (nonatomic , copy) NSArray<NEEduHttpUser *>              * members;
@property (nonatomic , strong) NEEduHttpRoom              * room;
@end

@interface NEEduRoomProfile : NSObject
@property (nonatomic , strong) Snapshot              * snapshot;
@property (nonatomic , assign) NSInteger              sequence;
@property (nonatomic, assign) NSInteger ts;

@end

NS_ASSUME_NONNULL_END
