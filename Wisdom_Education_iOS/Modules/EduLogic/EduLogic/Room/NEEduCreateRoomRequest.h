//
//  NEEduCreateRoomOption.h
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduRoleConfig.h"
#import "NEEduRoomConfig.h"
#import "NERoomConfig.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduCreateRoomRequest : NSObject
/// 班级名称
@property (nonatomic, strong) NSString *roomName;
@property (nonatomic, assign) NSInteger configId;
@property (nonatomic, strong) NSString *roomUuid;
@property (nonatomic, strong) NERoomConfig *config;

@end

NS_ASSUME_NONNULL_END
