//
//  NEEduRoomConfig.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduRoleConfig.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduRoomConfig : NSObject
@property (nonatomic ,strong) NEEduRoleConfig *roleConfigs;
@property (nonatomic ,copy) NSString *sceneType;
//@property (nonatomic ,strong) Permissions *permissions;
@end

NS_ASSUME_NONNULL_END
