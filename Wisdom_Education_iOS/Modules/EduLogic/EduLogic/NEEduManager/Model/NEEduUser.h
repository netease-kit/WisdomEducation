//
//  NEEduUser.h
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "BaseModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger,NEEduRoleType) {
    NEEduRoleTypeStudent = 0,
    NEEduRoleTypeTeacher = 1
};

@interface NEEduUser : NSObject
@property (nonatomic, copy) NSString *userUuid;
@property (nonatomic, copy) NSString *imToken;
@property (nonatomic, copy) NSString *userToken;
@property (nonatomic, copy) NSString *rtcKey;
@property (nonatomic, copy) NSString *imKey;
@property (nonatomic, assign) UInt64 rtcUserId;

@property (nonatomic, assign) NEEduRoleType roleType;
@property (nonatomic, strong) NSString *userName;
/// 是否上台 默认为NO 仅大班课场景使用
@property (nonatomic, assign) BOOL isOnStage;

@end

NS_ASSUME_NONNULL_END
