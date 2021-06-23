//
//  NEEduEnterRoomParam.h
//  EduLogic
//
//  Created by Groot on 2021/5/20.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduRoom.h"
#import "NEEduUser.h"
NS_ASSUME_NONNULL_BEGIN

@interface NEEduEnterRoomParam : NEEduRoom
@property (nonatomic, assign) NEEduRoleType role;
@property (nonatomic, strong) NSString *userName;

// default NO
@property (nonatomic, assign) BOOL autoPublish;
// default NO
@property (nonatomic, assign) BOOL autoSubscribeVideo;
// default NO
@property (nonatomic, assign) BOOL autoSubscribeAudio;
@end

NS_ASSUME_NONNULL_END
