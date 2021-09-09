//
//  NEEduRoom.h
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NERoomConfig.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, NEEduSceneType) {
    NEEduSceneType1V1 = 0,
    NEEduSceneTypeSmall = 1,
    NEEduSceneTypeBig = 2,
};

@interface NEEduRoom : NSObject

/// 班级名称
@property (nonatomic, strong) NSString *roomName;
/// 班级Id
@property (nonatomic, strong) NSString *roomUuid;
/// 班级类型
@property (nonatomic, assign) NEEduSceneType sceneType;
@property (nonatomic, assign) NSInteger configId;
/// 房间功能开关配置
@property (nonatomic, strong) NERoomConfig *config;
@end

NS_ASSUME_NONNULL_END
