//
//  NEEduRoomService.h
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduCreateRoomRequest.h"
#import "HttpManager.h"
#import "NEEduRoomProfile.h"
#import "NEEduEnterRoomResponse.h"
#import "NEEduEnterRoomParam.h"
#import <AFNetworking/AFNetworkReachabilityManager.h>
#import "NEEduRoomConfigResponse.h"
#import "NEEduErrorType.h"

NS_ASSUME_NONNULL_BEGIN

@protocol NEEduRoomServiceDelegate <NSObject>
- (void)netStateChangeWithState:(AFNetworkReachabilityStatus)state;

@end

@interface NEEduRoomService : NSObject
@property (nonatomic, strong) NEEduRoom *room;
@property (nonatomic, weak) id<NEEduRoomServiceDelegate> delegate;

- (void)createRoom:(NEEduRoom *)room completion:(void(^)(NEEduCreateRoomRequest *result,NSError *error))completion;
- (void)enterRoom:(NEEduEnterRoomParam *)room completion:(void(^)(NSError *error,NEEduEnterRoomResponse* response))completion;
- (void)getRoom:(NEEduRoom *)room completion:(void(^)(NEEduRoomConfigResponse *result,NSError *error))completion;
- (void)getRoomProfile:(NSString *)roomUuid completion:(void(^)(NSError *error,NEEduRoomProfile *profile))completion;
- (void)startLesson:(int)start completion:(void(^)(NSError *error,NEEduPropertyItem *item))completion;
- (void)muteAll:(BOOL)mute completion:(void(^)(NSError *error,NEEduPropertyItem *item))completion;
- (void)muteAllText:(BOOL)mute completion:(void(^)(NSError *error,NEEduPropertyItem *item))completion;

@end

NS_ASSUME_NONNULL_END
