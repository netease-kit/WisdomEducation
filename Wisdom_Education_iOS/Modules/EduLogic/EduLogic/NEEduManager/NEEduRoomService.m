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
#import "NEEduManager.h"
#import "NEEduRoomConfigResponse.h"

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
    request.config = room.config;
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
- (void)getRoom:(NEEduRoom *)room completion:(void(^)(NEEduRoomConfigResponse *result,NSError *error))completion {
    [HttpManager getRoom:room.roomUuid param:nil classType:[NEEduRoomConfigResponse class] success:^(id  _Nonnull objModel) {
        if (completion) {
            NEEduRoomConfigResponse *res = (NEEduRoomConfigResponse *)objModel;
            if (room.sceneType == NEEduSceneTypeLive && !res.isLiveClass) {
                NSError *error = [HttpManager errorWithErrorCode:1017];
                if (completion) {
                    completion(nil,error);
                }
            }else {
                [NEEduManager shared].localUser.userName = room.nickName;
                completion(objModel,nil);
            }
            
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
        __strong typeof(self) strongSelf = weakSelf;
        if ([NEEduManager shared].profile && objModel.snapshot.room.rtcCid != [NEEduManager shared].profile.snapshot.room.rtcCid) {
            //如果请求的与当前房间信息不符
            if (completion) {
                NSError *error = [NSError errorWithDomain:NEEduErrorDomain code:NEEduErrorTypeRoomNotFound userInfo:@{NSLocalizedDescriptionKey:@"房间不存在"}];
                completion(error,nil);
            }
        }
        objModel.ts = ts;
        
        strongSelf.room.roomUuid = objModel.snapshot.room.roomUuid;
        strongSelf.room.roomName = objModel.snapshot.room.roomName;
        
        [[NEEduManager shared].userService setupProfile:objModel];
        [[NEEduManager shared].messageService updateProfile:objModel];
        objModel.snapshot.members = [strongSelf sortMembers:objModel.snapshot.members];
        [NEEduManager shared].profile = objModel;
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
        if ([obj1.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) return NSOrderedAscending;
        if ([obj2.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) return NSOrderedDescending;
        return NSOrderedSame;
    }];
}

- (void)dealloc
{
    [self stopNetMonitor];
}
@end
