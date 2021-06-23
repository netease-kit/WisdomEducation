//
//  NEEduRoleConfig.h
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduRoleLimit : NSObject
@property (nonatomic, assign) NSInteger limit;
@end

@interface NEEduRoleConfig : NSObject
/// 老师角色
@property (nonatomic, strong) NEEduRoleLimit *host;
/// 学生角色
@property (nonatomic, strong) NEEduRoleLimit *broadcaster;
/// 大班课学生角色
@property (nonatomic, strong) NEEduRoleLimit *audience;

@end

NS_ASSUME_NONNULL_END
