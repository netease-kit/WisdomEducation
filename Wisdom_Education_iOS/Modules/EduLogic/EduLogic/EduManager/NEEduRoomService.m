//
//  NEEduRoomService.m
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduRoomService.h"
#import "NEEduCreateRoomRequest.h"
#import "NEEduEnterRoomRequest.h"
#import "NEEduEnterRoomResponse.h"
#import "EduManager.h"

@interface NEEduRoomService ()

@end


@implementation NEEduRoomService
- (instancetype)init
{
    self = [super init];
    if (self) {
        [self addNetMonitor];
    }
    return self;
}

- (void)createRoom:(NEEduRoom *)room completion:(void(^)(NEEduCreateRoomRequest *result,NSError *error))completion {
    NEEduCreateRoomRequest *request = [[NEEduCreateRoomRequest alloc] init];
    request.roomName = room.roomName;
    request.configId = room.configId;
    self.room = room;
    NSDictionary *param = [request yy_modelToJSONObject];
    [HttpManager createRoom:room.roomUuid param:param classType:[NEEduCreateRoomRequest class] success:^(id  _Nonnull objModel) {
        if (completion) {
            completion(objModel,nil);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (completion) {
            completion(nil,error);
        }
    }];
}

- (void)enterRoom:(NEEduEnterRoomParam *)room completion:(void(^)(NSError *error,NEEduEnterRoomResponse* response))completion {
    NEEduEnterRoomRequest *request = [[NEEduEnterRoomRequest alloc] init];
    NEEduStreams *streams = [[NEEduStreams alloc] init];
    NEEduPropertyItem *video = [[NEEduPropertyItem alloc] init];
    NEEduPropertyItem *audio = [[NEEduPropertyItem alloc] init];
    NEEduPropertyItem *subVideo = [[NEEduPropertyItem alloc] init];    
    audio.value = room.autoPublish;
    video.value = room.autoPublish;
    subVideo.value = 0;
    
    if (audio.value) {
        streams.audio = audio;
    }
    if (video.value) {
        streams.video = video;
    }
    if (subVideo.value) {
        streams.subVideo = subVideo;
    }
//    streams.audio = audio;
//    streams.video = video;
    
    request.userName = room.userName;
    request.role = NEEduRoleBroadcaster;
    if (room.role == NEEduRoleTypeStudent) {
        request.role = NEEduRoleBroadcaster;
    }else {
        request.role = NEEduRoleHost;
    }
//    streams.subVideo = subVideo;
    if (room.sceneType == NEEduSceneTypeBig && room.role == NEEduRoleTypeStudent) {
        //大班课接口请求没有流权限 所以没有streams结构
        request.role = NEEduRoleAudience;
    }else {
        request.streams = streams;
    }
    NSDictionary *param = [request yy_modelToJSONObject];
    [HttpManager enterRoom:room.roomUuid param:param classType:[NEEduEnterRoomResponse class] success:^(id  _Nonnull objModel) {
        if (completion) {
            completion(nil,objModel);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (completion) {
            completion(error,nil);
        }
    }];
}
- (void)getRoomProfile:(NSString *)roomUuid completion:(void(^)(NSError *error,NEEduRoomProfile *profile))completion {
    __weak typeof(self)weakSelf = self;
    [HttpManager getRoomProfile:roomUuid classType:[NEEduRoomProfile class] success:^(NEEduRoomProfile * objModel,NSInteger ts) {
        objModel.ts = ts;
        __strong typeof(self) strongSelf = weakSelf;
        strongSelf.room.roomUuid = objModel.snapshot.room.roomUuid;
        strongSelf.room.roomName = objModel.snapshot.room.roomName;
        
        [[EduManager shared].userService setupProfile:objModel];
        [[EduManager shared].messageService updateProfile:objModel];
        objModel.snapshot.members = [strongSelf sortMembers:objModel.snapshot.members];
        [EduManager shared].profile = objModel;
        if (completion) {
            completion(nil,objModel);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (completion) {
            completion(error,nil);
        }
    }];
}

- (void)startLesson:(int)start completion:(void(^)(NSError *error,NEEduPropertyItem *item))completion {
    NSDictionary *param = @{@"value":@(start)};
    [HttpManager startLessonWithRoomUuid:self.room.roomUuid param:param classType:[NEEduPropertyItem class] success:^(id  _Nonnull objModel) {
        if (completion) {
            completion(nil,objModel);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (completion) {
            completion(error,nil);
        }
    }];
}

- (void)muteAll:(BOOL)mute completion:(void(^)(NSError *error,NEEduPropertyItem *item))completion {
    int muteInt = mute;
    NSDictionary *param = @{@"value":@(muteInt)};
    [HttpManager muteAllWithRoomUuid:self.room.roomUuid param:param classType:[NEEduPropertyItem class] success:^(id  _Nonnull objModel) {
        if (completion) {
            completion(nil,objModel);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (completion) {
            completion(error,nil);
        }
    }];
}
- (void)muteAllText:(BOOL)mute completion:(void(^)(NSError *error,NEEduPropertyItem *item))completion {
    int muteInt = mute;
    NSDictionary *param = @{@"value":@(muteInt)};
    [HttpManager muteAllTextWithRoomUuid:self.room.roomUuid param:param classType:[NEEduPropertyItem class] success:^(id  _Nonnull objModel) {
        if (completion) {
            completion(nil,objModel);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (completion) {
            completion(error,nil);
        }
    }];
}

#pragma mark - private
- (void)addNetMonitor {
    __weak typeof(self)weakSelf = self;
    [[AFNetworkReachabilityManager sharedManager] startMonitoring];
    [[AFNetworkReachabilityManager sharedManager] setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
        [weakSelf handleNetChange:status];
    }];
}

- (void)stopNetMonitor {
    [[AFNetworkReachabilityManager manager] stopMonitoring];
}
- (void)handleNetChange:(AFNetworkReachabilityStatus)state {
    if (self.delegate && [self.delegate respondsToSelector:@selector(netStateChangeWithState:)]) {
        [self.delegate netStateChangeWithState:state];
    }
}
- (NSArray *)sortMembers:(NSArray <NEEduHttpUser *>*)members {
    return [members sortedArrayUsingComparator:^NSComparisonResult(NEEduHttpUser *  _Nonnull obj1, NEEduHttpUser *  _Nonnull obj2) {
        //规则：老师第一位 自己第二位
        if ([obj1.role isEqualToString:NEEduRoleHost]) return NSOrderedAscending;
        if ([obj2.role isEqualToString:NEEduRoleHost]) return NSOrderedDescending;
        if ([obj1.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) return NSOrderedAscending;
        if ([obj2.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) return NSOrderedDescending;
        return NSOrderedSame;
    }];
}

- (void)dealloc
{
    [self stopNetMonitor];
}
@end
