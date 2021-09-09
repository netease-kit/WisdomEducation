//
//  NEEduHttpUser.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduUserProperty.h"
#import "NEEduStreams.h"

NS_ASSUME_NONNULL_BEGIN

extern NSString * const NEEduRoleHost;
extern NSString * const NEEduRoleBroadcaster;
extern NSString * const NEEduRoleAudience;

@interface NEWebAuth : NSObject

@property (nonatomic, strong) NSString *checksum;
@property (nonatomic, strong) NSString *curTime;
@property (nonatomic, strong) NSString *nonce;
@end

@interface NEEduHttpUser : NSObject
@property (nonatomic , copy) NSString              * rtcKey;
@property (nonatomic , assign) NSInteger              rtcUid;
@property (nonatomic , copy) NSString              * userName;
@property (nonatomic , copy) NSString              * role;
@property (nonatomic , strong) NEEduUserProperty   * properties;
@property (nonatomic , copy) NSString              * userUuid;
@property (nonatomic , copy) NSString              * rtcToken;
@property (nonatomic , strong) NEEduStreams        * streams;
@property (nonatomic, strong) NEWebAuth *wbAuth;

/// 是否上台 默认为NO 仅大班课场景使用
@property (nonatomic, assign) BOOL isOnStage;
@property (nonatomic, assign) BOOL isTeacher;

+ (instancetype)teacher;
+ (instancetype)student;
- (instancetype)initWithRole:(NSString *)role;
@end

NS_ASSUME_NONNULL_END
